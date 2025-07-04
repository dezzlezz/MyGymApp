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
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun dailyPlanDao(): DailyPlanDao
    abstract fun weeklyPlanDao(): WeeklyPlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(ctx: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    ctx.applicationContext,
                    AppDatabase::class.java,
                    "mygym-db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = inst
                inst
            }
    }
}
