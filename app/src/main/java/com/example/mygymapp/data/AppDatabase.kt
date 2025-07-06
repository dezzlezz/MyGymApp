// app/src/main/java/com/example/mygymapp/data/AppDatabase.kt  
package com.example.mygymapp.data

import android.content.Context
import androidx.room.*

@Database(
    entities = [
        Exercise::class,
        Plan::class,
        PlanExerciseCrossRef::class,
        DailyPlanExerciseCrossRef::class,
        WeeklyPlanDayEntity::class,
        WeeklyPlanExerciseCrossRef::class
    ],
    version = 13,
    exportSchema = false
)

@Database(
    entities = [
        Plan::class,
        PlanExerciseCrossRef::class,
        WeeklyPlanDayEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
    // ...
}

@TypeConverters(ExerciseConverters::class, PlanConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun planDao(): PlanDao

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
