package com.example.mygymapp.data

import android.content.Context
import androidx.room.*

@Database(
    entities = [Exercise::class, Plan::class, PlanExerciseCrossRef::class],
    version = 10,
    exportSchema = false
)
@TypeConverters(ExerciseConverters::class, PlanConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun planDao(): PlanDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mygymapp.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = inst
                inst
            }
    }
}