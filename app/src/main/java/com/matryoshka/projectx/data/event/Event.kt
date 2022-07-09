package com.matryoshka.projectx.data.event

import com.matryoshka.projectx.data.interest.Interest
import com.matryoshka.projectx.data.map.Coordinates
import java.time.LocalDateTime

data class Event(
    val name: String,
    val summary: String,
    val details: String,
    val interest: Interest,
    val public: Boolean,
    val maxParticipants: Int?,
    val location: Location,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val withApproval: Boolean,
    val participants: List<UserRef> = emptyList(),
    val creator: UserRef? = null,
    val id: String? = null,
)

data class Location(
    val name: String?,
    val address: String?,
    val coordinates: Coordinates?
)

data class UserRef(val id: String, val name: String)

val Event.isNew
    get() = id == null