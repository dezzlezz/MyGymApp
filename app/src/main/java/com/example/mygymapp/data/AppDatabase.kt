package com.example.mygymapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Plan::class,
        PlanExerciseCrossRef::class,
        Exercise::class              // <<< hier hinzufügen!
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(PlanConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun planDao(): PlanDao
    abstract fun exerciseDao(): ExerciseDao   // <<< sicherstellen, dass es hier steht

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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

