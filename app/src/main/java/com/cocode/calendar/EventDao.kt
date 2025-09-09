package com.cocode.calendar

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM event WHERE startDate <= :date AND endDate >= :date")
    fun getEventsForDate(date: String): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)
}
