package com.matryoshka.projectx.ui.event

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.matryoshka.projectx.SavedStateKey.INTEREST_KEY
import com.matryoshka.projectx.SavedStateKey.LOCATION_KEY
import com.matryoshka.projectx.navigation.Screen
import com.matryoshka.projectx.ui.common.ScreenStatus
import com.matryoshka.projectx.ui.common.ScreenStatus.LOADING
import com.matryoshka.projectx.ui.common.ScreenStatus.READY
import com.matryoshka.projectx.ui.common.ScreenStatus.SUBMITTING
import com.matryoshka.projectx.ui.event.form.EventFormActions
import com.matryoshka.projectx.ui.event.form.EventFormState

private const val TAG = "NewEventViewModel"

class NewEventScreenViewModel : ViewModel() {
    var state by mutableStateOf(NewEventState())
        private set

    val eventFormActions = EventFormActions(
        onLocationClick = { navController, lifecycleOwner ->
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<String>(LOCATION_KEY)
                ?.observe(lifecycleOwner) { location ->
                    state.formState.location.onChange(location)
                }
            navController.navigate(Screen.LOCATION_SELECTION_SCREEN)
        },
        onInterestClick = { navController, lifecycleOwner ->
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<String>(INTEREST_KEY)
                ?.observe(lifecycleOwner) { interest ->
                    state.formState.interest.onChange(interest)
                }
            navController.navigate(Screen.INTEREST_SELECTION_SCREEN)
        }
    )

    fun init() {
        state = state.copy(status = READY)
        Log.d(TAG, "init: $state")
    }
}

@Stable
data class NewEventState(
    val status: ScreenStatus = LOADING,
    val formState: EventFormState = EventFormState()
) {
    val displayForm: Boolean
        get() = status != LOADING
    val showProgress
        get() = status == LOADING || status == SUBMITTING
}