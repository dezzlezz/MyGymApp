package com.example.mygymapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mygymapp.data.StringListConverter
import com.example.mygymapp.data.PlanDay
import com.example.mygymapp.data.ExerciseConverters


@Database(
    entities = [
        Plan::class,
        PlanExerciseCrossRef::class,
        Exercise::class,
        PlanDay::class,
        ParagraphEntity::class
    ],
    version = 12,
    exportSchema = false
)
@TypeConverters(PlanConverters::class, StringListConverter::class, ExerciseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun planDao(): PlanDao
    abstract fun exerciseDao(): ExerciseDao   // <<< sicherstellen, dass es hier steht
    abstract fun paragraphDao(): ParagraphDao

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

