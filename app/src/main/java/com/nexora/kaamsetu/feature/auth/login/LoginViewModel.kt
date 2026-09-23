package com.nexora.kaamsetu.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.AuthRepository
import com.nexora.kaamsetu.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Basic Indian mobile number check: optional +91/0 prefix, then a 10-digit number starting 6-9. */
private val INDIAN_MOBILE_REGEX = Regex("^(\\+91[- ]?|0)?[6-9]\\d{9}$")

object LoginFormField {
    const val PHONE = "phone"
    const val PASSWORD = "password"
}

data class LoginUiState(
    val role: UserRole,
    val phone: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val errors: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false,
    /** Set only for a failed attempt — e.g. wrong password, or right account/wrong role. */
    val submitError: String? = null,
    val loggedIn: Boolean = false
)

class LoginViewModel(
    role: UserRole,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(role = role))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(
            phone = value,
            errors = _uiState.value.errors - LoginFormField.PHONE,
            submitError = null
        )
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            errors = _uiState.value.errors - LoginFormField.PASSWORD,
            submitError = null
        )
    }

    fun onTogglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun submit() {
        val state = _uiState.value
        val errors = validate(state)
        if (errors.isNotEmpty()) {
            _uiState.value = state.copy(errors = errors)
            return
        }

        _uiState.value = state.copy(isSubmitting = true, submitError = null)

        viewModelScope.launch {
            val result = authRepository.login(state.phone.trim(), state.password, state.role)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isSubmitting = false, loggedIn = true)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        submitError = error.message ?: "Unable to sign in. Please try again."
                    )
                }
            )
        }
    }

    private fun validate(state: LoginUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (state.phone.isBlank()) {
            errors[LoginFormField.PHONE] = "Please enter your phone number."
        } else if (!INDIAN_MOBILE_REGEX.matches(state.phone.trim())) {
            errors[LoginFormField.PHONE] = "Enter a valid 10-digit Indian mobile number."
        }

        if (state.password.isBlank()) {
            errors[LoginFormField.PASSWORD] = "Please enter your password."
        }

        return errors
    }
}
