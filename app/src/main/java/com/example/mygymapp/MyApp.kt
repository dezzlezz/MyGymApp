package com.example.mygymapp

import android.app.Application
import androidx.room.Room
import com.example.mygymapp.data.AppDatabase

class MyApp : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "mygymapp.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
