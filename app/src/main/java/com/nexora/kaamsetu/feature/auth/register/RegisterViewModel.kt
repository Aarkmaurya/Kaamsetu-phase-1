package com.nexora.kaamsetu.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.AuthRepository
import com.nexora.kaamsetu.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val INDIAN_MOBILE_REGEX = Regex("^(\\+91[- ]?|0)?[6-9]\\d{9}$")

object RegisterFormField {
    const val NAME = "name"
    const val PHONE = "phone"
    const val PASSWORD = "password"
}

data class RegisterUiState(
    val role: UserRole,
    val name: String = "",
    val phone: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val errors: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val registered: Boolean = false
)

/**
 * Deliberately no role field anywhere in this state or screen — the role is
 * fixed by which button the person tapped on Role Selection and threaded
 * through navigation, so there is no way to register as Admin from Android.
 */
class RegisterViewModel(
    role: UserRole,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState(role = role))
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errors = _uiState.value.errors - RegisterFormField.NAME)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value, errors = _uiState.value.errors - RegisterFormField.PHONE, submitError = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errors = _uiState.value.errors - RegisterFormField.PASSWORD)
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
            val result = authRepository.register(state.name.trim(), state.phone.trim(), state.password, state.role)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isSubmitting = false, registered = true)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        submitError = error.message ?: "Unable to create account. Please try again."
                    )
                }
            )
        }
    }

    private fun validate(state: RegisterUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (state.name.isBlank()) {
            errors[RegisterFormField.NAME] = "Please enter your name."
        }

        if (state.phone.isBlank()) {
            errors[RegisterFormField.PHONE] = "Please enter your phone number."
        } else if (!INDIAN_MOBILE_REGEX.matches(state.phone.trim())) {
            errors[RegisterFormField.PHONE] = "Enter a valid 10-digit Indian mobile number."
        }

        if (state.password.isBlank()) {
            errors[RegisterFormField.PASSWORD] = "Please choose a password."
        } else if (state.password.length < 6) {
            errors[RegisterFormField.PASSWORD] = "Password must be at least 6 characters."
        }

        return errors
    }
}
