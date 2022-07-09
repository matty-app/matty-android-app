package com.matryoshka.projectx.data.event

interface EventsRepository {

    suspend fun save(event: Event): Event

    suspend fun getAll(): List<Event>
}