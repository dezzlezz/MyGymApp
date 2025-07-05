package com.example.mygymapp

import android.app.Application
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.DailyPlanDao
import com.example.mygymapp.data.WeeklyPlanDao

class MyApp : Application() {

    // Datenbank-Instanz
    private val database by lazy { AppDatabase.getDatabase(this) }

    // DAOs zum globalen Zugriff
    val dailyPlanDao: DailyPlanDao
        get() = database.dailyPlanDao()

    val weeklyPlanDao: WeeklyPlanDao
        get() = database.weeklyPlanDao()

    override fun onCreate() {
        super.onCreate()
        // hier könntest du noch Logging oder Initialisierung machen
    }
}
