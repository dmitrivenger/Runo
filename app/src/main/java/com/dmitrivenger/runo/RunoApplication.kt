package com.dmitrivenger.runo

import android.app.Application
import androidx.room.Room
import com.dmitrivenger.runo.data.local.RunDatabase
import com.dmitrivenger.runo.data.preferences.UserPreferences
import com.dmitrivenger.runo.data.repository.RunRepository

class RunoApplication : Application() {

    val database: RunDatabase by lazy {
        Room.databaseBuilder(this, RunDatabase::class.java, "runo.db").build()
    }

    val userPreferences: UserPreferences by lazy { UserPreferences(this) }

    val runRepository: RunRepository by lazy { RunRepository(database.runDao()) }
}
