package com.matryoshka.projectx.ui.signup

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matryoshka.projectx.exception.ProjectxException
import com.matryoshka.projectx.navigation.NavAdapter
import com.matryoshka.projectx.service.AuthService
import com.matryoshka.projectx.ui.common.InputField
import com.matryoshka.projectx.ui.common.ScreenStatus
import com.matryoshka.projectx.ui.common.setIsNewUser
import com.matryoshka.projectx.ui.common.setUserEmail
import com.matryoshka.projectx.ui.common.setUserName
import com.matryoshka.projectx.ui.validator.EmailExistsValidator
import com.matryoshka.projectx.ui.validator.EmailValidator
import com.matryoshka.projectx.ui.validator.NameValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authService: AuthService,
    private val sharedPrefs: SharedPreferences,
    private val navAdapter: NavAdapter
) : ViewModel() {

    var state by mutableStateOf(
        SignUpScreenState(
            nameField = InputField(validators = listOf(NameValidator())),
            emailField = InputField(
                validators = listOf(
                    EmailValidator(),
                    EmailExistsValidator(authService)
                )
            )
        )
    )
        private set

    private val nameField: InputField<String>
        get() = state.nameField

    private val emailField: InputField<String>
        get() = state.emailField

    fun onRegisterClicked() {
        viewModelScope.launch {
            changeStatus(ScreenStatus.SUBMITTING)
            try {
                if (validate()) {
                    val email = emailField.value!!
                    val name = nameField.value!!
                    sendLinkToEmail(email)
                    saveSignUpPrefs(email, name)
                    changeStatus(ScreenStatus.READY)
                    goToEmailConfirmationScreen(email)
                } else {
                    changeStatus(ScreenStatus.READY)
                }
            } catch (ex: ProjectxException) {
                setError(ex)
            }
        }
    }

    private suspend fun validate(): Boolean {
        val nameValid = nameField.validate()
        val emailValid = emailField.validate()
        return nameValid && emailValid
    }

    private suspend fun sendLinkToEmail(email: String) {
        authService.sendSignInLinkToEmail(email)
    }

    private fun saveSignUpPrefs(email: String, name: String) {
        sharedPrefs.setUserEmail(email)
        sharedPrefs.setUserName(name)
        sharedPrefs.setIsNewUser(true)
    }

    private fun goToEmailConfirmationScreen(email: String) {
        navAdapter.goToEmailConfirmationScreen(email)
    }

    private fun changeStatus(status: ScreenStatus) {
        state = state.copy(status = status)
    }

    private fun setError(error: ProjectxException) {
        state = state.copy(status = ScreenStatus.ERROR, error = error)
    }
}

data class SignUpScreenState(
    val nameField: InputField<String>,
    val emailField: InputField<String>,
    val status: ScreenStatus = ScreenStatus.READY,
    val error: ProjectxException? = null
) {
    val enabled: Boolean
        get() = status != ScreenStatus.SUBMITTING
    val isProgressIndicatorVisible: Boolean
        get() = status == ScreenStatus.SUBMITTING
    val isErrorToastVisible: Boolean
        get() = status == ScreenStatus.ERROR && error != null
}