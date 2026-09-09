package com.nexora.kaamsetu.feature.customer.createrequest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.data.repository.ServiceCatalogRepository
import com.nexora.kaamsetu.domain.model.JobRequest
import com.nexora.kaamsetu.domain.model.JobStatus
import com.nexora.kaamsetu.domain.model.ServiceTiming
import com.nexora.kaamsetu.domain.model.ServiceType
import com.nexora.kaamsetu.feature.customer.jobs.DEMO_CUSTOMER_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

/** Field-level validation error keys, used to look up messages in the UI. */
object RequestFormField {
    const val PROBLEM_DESCRIPTION = "problemDescription"
    const val SCHEDULED_DATE = "scheduledDate"
    const val SCHEDULED_TIME = "scheduledTime"
    const val APPROXIMATE_AREA = "approximateArea"
    const val EXACT_ADDRESS = "exactAddress"
    const val CUSTOMER_NAME = "customerName"
    const val CUSTOMER_PHONE = "customerPhone"
}

data class CreateRequestUiState(
    val serviceId: String,
    val serviceName: String = "",
    val problemDescription: String = "",
    val preferredTime: ServiceTiming = ServiceTiming.NOW,
    val scheduledDate: String = "",
    val scheduledTime: String = "",
    val serviceType: ServiceType = ServiceType.HOME_VISIT,
    val approximateArea: String = "",
    val exactAddress: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val errors: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false
)

/** Basic Indian mobile number check: optional +91/0 prefix, then a 10-digit number starting 6-9. */
private val INDIAN_MOBILE_REGEX = Regex("^(\\+91[- ]?|0)?[6-9]\\d{9}$")

class CreateServiceRequestViewModel(
    serviceId: String,
    private val serviceCatalogRepository: ServiceCatalogRepository,
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRequestUiState(serviceId = serviceId))
    val uiState: StateFlow<CreateRequestUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // One-shot lookup: the category list rarely changes mid-form, and this
            // avoids keeping a long-lived collector alive for the whole form's life.
            val category = serviceCatalogRepository.observeCategories().first()
                .firstOrNull { it.id == serviceId }
            _uiState.value = _uiState.value.copy(serviceName = category?.name ?: serviceId)
        }
    }

    fun onProblemDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(problemDescription = value, errors = _uiState.value.errors - RequestFormField.PROBLEM_DESCRIPTION)
    }

    fun onPreferredTimeChange(value: ServiceTiming) {
        _uiState.value = _uiState.value.copy(preferredTime = value)
    }

    fun onScheduledDateChange(value: String) {
        _uiState.value = _uiState.value.copy(scheduledDate = value, errors = _uiState.value.errors - RequestFormField.SCHEDULED_DATE)
    }

    fun onScheduledTimeChange(value: String) {
        _uiState.value = _uiState.value.copy(scheduledTime = value, errors = _uiState.value.errors - RequestFormField.SCHEDULED_TIME)
    }

    fun onServiceTypeChange(value: ServiceType) {
        _uiState.value = _uiState.value.copy(serviceType = value)
    }

    fun onApproximateAreaChange(value: String) {
        _uiState.value = _uiState.value.copy(approximateArea = value, errors = _uiState.value.errors - RequestFormField.APPROXIMATE_AREA)
    }

    fun onExactAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(exactAddress = value, errors = _uiState.value.errors - RequestFormField.EXACT_ADDRESS)
    }

    fun onCustomerNameChange(value: String) {
        _uiState.value = _uiState.value.copy(customerName = value, errors = _uiState.value.errors - RequestFormField.CUSTOMER_NAME)
    }

    fun onCustomerPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(customerPhone = value, errors = _uiState.value.errors - RequestFormField.CUSTOMER_PHONE)
    }

    fun submit() {
        val state = _uiState.value
        val errors = validate(state)
        if (errors.isNotEmpty()) {
            _uiState.value = state.copy(errors = errors)
            return
        }

        _uiState.value = state.copy(isSubmitting = true)

        viewModelScope.launch {
            val job = JobRequest(
                id = "job_${UUID.randomUUID()}",
                customerId = DEMO_CUSTOMER_ID,
                serviceId = state.serviceId,
                serviceName = state.serviceName,
                problemDescription = state.problemDescription.trim(),
                preferredTime = state.preferredTime,
                scheduledDateTime = if (state.preferredTime == ServiceTiming.SCHEDULED) {
                    "${state.scheduledDate.trim()} ${state.scheduledTime.trim()}".trim()
                } else {
                    null
                },
                serviceType = state.serviceType,
                approximateArea = if (state.serviceType == ServiceType.HOME_VISIT) state.approximateArea.trim() else "",
                exactAddress = if (state.serviceType == ServiceType.HOME_VISIT) state.exactAddress.trim() else null,
                customerName = state.customerName.trim(),
                customerPhone = state.customerPhone.trim(),
                status = JobStatus.REQUEST_CREATED,
                createdAt = System.currentTimeMillis()
            )
            jobRepository.createJobRequest(job)
            _uiState.value = _uiState.value.copy(isSubmitting = false, submitted = true)
        }
    }

    private fun validate(state: CreateRequestUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (state.problemDescription.isBlank()) {
            errors[RequestFormField.PROBLEM_DESCRIPTION] = "Please describe the problem."
        }

        if (state.preferredTime == ServiceTiming.SCHEDULED) {
            if (state.scheduledDate.isBlank()) {
                errors[RequestFormField.SCHEDULED_DATE] = "Please enter a date."
            }
            if (state.scheduledTime.isBlank()) {
                errors[RequestFormField.SCHEDULED_TIME] = "Please enter a time."
            }
        }

        if (state.serviceType == ServiceType.HOME_VISIT) {
            if (state.approximateArea.isBlank()) {
                errors[RequestFormField.APPROXIMATE_AREA] = "Approximate area is required for a home visit."
            }
            if (state.exactAddress.isBlank()) {
                errors[RequestFormField.EXACT_ADDRESS] = "Exact address is required for a home visit."
            }
        }

        if (state.customerName.isBlank()) {
            errors[RequestFormField.CUSTOMER_NAME] = "Please enter your name."
        }

        if (state.customerPhone.isBlank()) {
            errors[RequestFormField.CUSTOMER_PHONE] = "Please enter your phone number."
        } else if (!INDIAN_MOBILE_REGEX.matches(state.customerPhone.trim())) {
            errors[RequestFormField.CUSTOMER_PHONE] = "Enter a valid 10-digit Indian mobile number."
        }

        return errors
    }
}
