package com.dmitrivenger.runo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RunEntity::class], version = 1, exportSchema = false)
abstract class RunDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao
}
