package com.matryoshka.projectx.ui.signin

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.matryoshka.projectx.navigation.NavAdapter
import com.matryoshka.projectx.navigation.Screen

@Composable
fun SignInRouter(
    viewModel: SignInViewModel = hiltViewModel(),
    navAdapter: NavAdapter
) {
    SignInScreen(
        state = viewModel.state,
        onLogInClicked = viewModel::onLogInClicked,
        onSignUpClicked = { navAdapter.navigateTo(Screen.SIGN_UP) }
    )
}