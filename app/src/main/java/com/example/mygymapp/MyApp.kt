package com.example.mygymapp

import android.app.Application
import androidx.room.Room
import com.example.mygymapp.data.*

class MyApp : Application() {

    // Room-Datenbank
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "mygym-db"
        ).build()
    }

    // Repositories
    val exerciseRepository: ExerciseRepository by lazy {
        ExerciseRepository(database.exerciseDao())
    }
    val dailyPlanRepository: DailyPlanRepository by lazy {
        DailyPlanRepository(database.dailyPlanDao())
    }
    val weeklyPlanRepository: WeeklyPlanRepository by lazy {
        WeeklyPlanRepository(database.weeklyPlanDao())
    }
}
