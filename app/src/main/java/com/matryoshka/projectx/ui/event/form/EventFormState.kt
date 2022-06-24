package com.matryoshka.projectx.ui.event.form

import androidx.compose.runtime.Stable
import com.matryoshka.projectx.data.Interest
import com.matryoshka.projectx.data.map.LocationInfo
import com.matryoshka.projectx.ui.common.FieldState
import com.matryoshka.projectx.ui.common.numberFieldState
import com.matryoshka.projectx.ui.common.switchState
import com.matryoshka.projectx.ui.common.textFieldState
import java.time.LocalDate
import java.time.LocalTime

@Stable
class EventFormState(
    name: String = "",
    summary: String = "",
    details: String = "",
    public: Boolean = true,
    limitMaxParticipants: Boolean = false,
    maxParticipants: Int? = null,
    location: LocationInfo? = null,
    interest: Interest? = null,
    time: LocalTime = LocalTime.now(),
    date: LocalDate = LocalDate.now()
) {
    val name = textFieldState(name)
    val summary = textFieldState(summary)
    val details = textFieldState(details)
    val public = switchState(public)
    val limitMaxParticipants = switchState(
        checked = limitMaxParticipants,
        onChange = { _, newValue, _ ->
            if (!newValue) {
                this.maxParticipants.onChange("")
            }
            true
        }
    )
    val maxParticipants = numberFieldState(maxParticipants)
    val date = FieldState(date)
    val time = FieldState(time)
    val interest = FieldState(interest)
    val location = FieldState(location)

}