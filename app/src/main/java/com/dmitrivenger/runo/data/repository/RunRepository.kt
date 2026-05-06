package com.dmitrivenger.runo.data.repository

import com.dmitrivenger.runo.data.local.RunDao
import com.dmitrivenger.runo.data.local.RunEntity
import com.dmitrivenger.runo.domain.model.Run
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RunRepository(private val dao: RunDao) {

    val allRuns: Flow<List<Run>> = dao.getAllRuns().map { list -> list.map { it.toDomain() } }

    fun runsSince(epochMs: Long): Flow<List<Run>> =
        dao.getRunsSince(epochMs).map { list -> list.map { it.toDomain() } }

    suspend fun saveRun(run: Run): Long = dao.insert(RunEntity.fromDomain(run))

    suspend fun getRunById(id: Long): Run? = dao.getRunById(id)?.toDomain()
}
