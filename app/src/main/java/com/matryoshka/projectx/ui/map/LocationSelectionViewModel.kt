package com.matryoshka.projectx.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.matryoshka.projectx.SavedStateKey.LOCATION_KEY
import com.matryoshka.projectx.data.map.GeoPoint
import com.matryoshka.projectx.data.map.LocationInfo
import com.matryoshka.projectx.data.map.SuggestedLocation
import com.matryoshka.projectx.service.YandexLocationService
import com.matryoshka.projectx.ui.common.FieldState
import com.matryoshka.projectx.ui.common.SourceType
import com.matryoshka.projectx.ui.common.SourceType.APPLICATION
import com.matryoshka.projectx.ui.common.SourceType.USER_INPUT
import com.matryoshka.projectx.ui.common.anyPermissionGranted
import com.matryoshka.projectx.ui.common.textFieldState
import com.matryoshka.projectx.utils.debounce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "LocationSelectionVM"

@HiltViewModel
class LocationSelectionViewModel @Inject constructor(
    private val locationService: YandexLocationService
) : ViewModel() {
    var state by mutableStateOf(
        LocationChangeState(
            searchField = textFieldState(onChange = ::onSearchFieldChange),
            mapState = MapState(
                onPositionChanged = ::onCameraPositionChanged
            ),
        )
    )
        private set

    fun onSubmit(navController: NavController) {
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set(LOCATION_KEY, state.searchField.value)
        navController.popBackStack()
    }

    fun onCancel(navController: NavController) {
        navController.popBackStack()
    }

    fun onSuggestionClick(suggestion: SuggestedLocation) {
        viewModelScope.launch {
            val result = locationService.resolveByURI(suggestion.uri)
            state.mapState.move(result.location.geoPoint, result.boundingArea)
            updateLocationState(result.location)
        }
    }

    @SuppressLint("MissingPermission")
    fun displayUserLocation(context: Context) {
        if (
            context.anyPermissionGranted(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            viewModelScope.launch {
                val result = locationService.getUserLocation(context)
                state.mapState.move(result.location.geoPoint, result.boundingArea)
                updateLocationState(result.location)
            }
        }
    }

    private fun onCameraPositionChanged(geoPoint: GeoPoint, zoom: Float) {
        Log.d(TAG, "onMapPositionChanged: $geoPoint. zoom - $zoom")
        viewModelScope.launch {
            val result = locationService.resolveByGeoPoint(geoPoint)
            updateLocationState(result.location)
        }
    }

    private fun updateLocationState(location: LocationInfo) {
        state = state.copy(location = location)
        state.searchField.onChange(location.displayName, APPLICATION)
    }

    private fun onSearchFieldChange(
        prevValue: String,
        newValue: String,
        sourceType: SourceType
    ): Boolean {
        val shouldUpdate = prevValue != newValue
        if (shouldUpdate && sourceType == USER_INPUT) {
            loadSuggestions(newValue)
        }
        return shouldUpdate
    }

    private val loadSuggestions: (String) -> Unit =
        viewModelScope.debounce(800.milliseconds) { locationName ->
            Log.d(TAG, "updateSuggestions: query '$locationName'")
            if (locationName.isEmpty()) {
                state = state.copy(
                    suggestions = emptyList()
                )
                return@debounce
            }
            val suggestions = locationService.getSuggestions(locationName, state.mapState.geoPoint)
            state = state.copy(suggestions = suggestions)
        }

}

@Stable
data class LocationChangeState(
    val mapState: MapState,
    val searchField: FieldState<String>,
    val suggestions: List<SuggestedLocation> = emptyList(),
    val location: LocationInfo? = null,
)