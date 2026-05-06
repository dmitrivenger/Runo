package com.dmitrivenger.runo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run: RunEntity): Long

    @Query("SELECT * FROM runs ORDER BY startTime DESC")
    fun getAllRuns(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runs WHERE id = :id")
    suspend fun getRunById(id: Long): RunEntity?

    @Query("SELECT * FROM runs WHERE startTime >= :from ORDER BY startTime DESC")
    fun getRunsSince(from: Long): Flow<List<RunEntity>>
}
