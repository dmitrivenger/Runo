package com.dmitrivenger.runo

import android.app.Application
import androidx.room.Room
import com.dmitrivenger.runo.data.local.MIGRATION_1_2
import com.dmitrivenger.runo.data.local.RunDatabase
import com.dmitrivenger.runo.data.preferences.UserPreferences
import com.dmitrivenger.runo.data.repository.RunRepository
import org.maplibre.android.MapLibre

class RunoApplication : Application() {

    val database: RunDatabase by lazy {
        Room.databaseBuilder(this, RunDatabase::class.java, "runo.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    val userPreferences: UserPreferences by lazy { UserPreferences(this) }

    val runRepository: RunRepository by lazy { RunRepository(database.runDao()) }

    override fun onCreate() {
        super.onCreate()
        MapLibre.getInstance(this)
    }
}
