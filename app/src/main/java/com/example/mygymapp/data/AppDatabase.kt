package com.example.mygymapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Exercise::class,
        DailyPlan::class,
        WeeklyPlan::class,
        DailyPlanExerciseCrossRef::class,
        WeeklyPlanExerciseCrossRef::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun dailyPlanDao(): DailyPlanDao
    abstract fun weeklyPlanDao(): WeeklyPlanDao


    // Hier hinzufügen:
    abstract fun dailyPlanExerciseCrossRefDao(): DailyPlanExerciseCrossRefDao
    abstract fun weeklyPlanExerciseCrossRefDao(): WeeklyPlanExerciseCrossRefDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mygymapp.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
