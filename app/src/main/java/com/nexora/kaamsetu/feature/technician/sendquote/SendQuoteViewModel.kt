package com.nexora.kaamsetu.feature.technician.sendquote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.data.repository.TechnicianRepository
import com.nexora.kaamsetu.domain.model.Quote
import com.nexora.kaamsetu.domain.model.QuoteStatus
import com.nexora.kaamsetu.feature.technician.DEMO_TECHNICIAN_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

object SendQuoteField {
    const val PRICE = "price"
}

/** Practical fixed set of arrival-time options, per the Phase 3 spec. */
val ARRIVAL_TIME_OPTIONS = listOf("15 minutes", "30 minutes", "1 hour", "2 hours")
const val QUOTE_MESSAGE_MAX_LENGTH = 200

data class SendQuoteUiState(
    val jobId: String,
    val estimatedPrice: String = "",
    val estimatedArrivalTime: String = ARRIVAL_TIME_OPTIONS[1],
    val message: String = "",
    val isUpdating: Boolean = false,
    val errors: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false
)

class SendQuoteViewModel(
    jobId: String,
    private val technicianRepository: TechnicianRepository,
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SendQuoteUiState(jobId = jobId))
    val uiState: StateFlow<SendQuoteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Prefill from this technician's existing quote, if any — the
            // repository itself will still upsert-by-natural-key on submit
            // regardless of what id/timestamps we do or don't know here.
            val existing = jobRepository.getMyQuoteForJob(jobId, DEMO_TECHNICIAN_ID)
            if (existing != null) {
                _uiState.value = _uiState.value.copy(
                    estimatedPrice = existing.estimatedPrice.toInt().toString(),
                    estimatedArrivalTime = existing.estimatedArrivalTime,
                    message = existing.message.orEmpty(),
                    isUpdating = true
                )
            }
        }
    }

    fun onPriceChange(value: String) {
        _uiState.value = _uiState.value.copy(
            estimatedPrice = value,
            errors = _uiState.value.errors - SendQuoteField.PRICE
        )
    }

    fun onArrivalTimeChange(value: String) {
        _uiState.value = _uiState.value.copy(estimatedArrivalTime = value)
    }

    fun onMessageChange(value: String) {
        _uiState.value = _uiState.value.copy(message = value.take(QUOTE_MESSAGE_MAX_LENGTH))
    }

    fun submit() {
        val state = _uiState.value
        val priceValue = state.estimatedPrice.trim().toDoubleOrNull()

        val errors = mutableMapOf<String, String>()
        if (state.estimatedPrice.isBlank()) {
            errors[SendQuoteField.PRICE] = "Please enter an estimated price."
        } else if (priceValue == null) {
            errors[SendQuoteField.PRICE] = "Enter a valid number."
        } else if (priceValue <= 0.0) {
            errors[SendQuoteField.PRICE] = "Price must be greater than 0."
        }

        if (errors.isNotEmpty()) {
            _uiState.value = state.copy(errors = errors)
            return
        }

        _uiState.value = state.copy(isSubmitting = true)

        viewModelScope.launch {
            val technician = technicianRepository.observeAll().first()
                .firstOrNull { it.id == DEMO_TECHNICIAN_ID }

            val quote = Quote(
                id = "quote_${UUID.randomUUID()}",
                jobRequestId = state.jobId,
                technicianId = DEMO_TECHNICIAN_ID,
                technicianName = technician?.name ?: "Technician",
                technicianRating = technician?.rating ?: 0.0,
                technicianVerified = technician?.isVerified ?: false,
                estimatedPrice = priceValue!!,
                estimatedArrivalTime = state.estimatedArrivalTime,
                message = state.message.trim().ifBlank { null },
                status = QuoteStatus.PENDING,
                createdAt = System.currentTimeMillis()
            )
            // submitQuote() upserts by (jobRequestId, technicianId) internally,
            // so this is safe to call whether this is a new quote or an update.
            jobRepository.submitQuote(quote)
            _uiState.value = _uiState.value.copy(isSubmitting = false, submitted = true)
        }
    }
}
