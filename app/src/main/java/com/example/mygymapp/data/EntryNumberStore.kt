package com.example.mygymapp.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.time.LocalDate

private val Context.entryDataStore by preferencesDataStore("journal_entries")

class EntryNumberStore private constructor(private val context: Context) {
    private val dataStore = context.entryDataStore
    private val ENTRY_KEY = intPreferencesKey("entry_number")
    private val DAY_KEY = longPreferencesKey("last_increment_day")

    val entryNumberFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[ENTRY_KEY] ?: 1
    }

    suspend fun loadCurrent(): Int {
        val prefs = dataStore.data.first()
        val storedNum = prefs[ENTRY_KEY] ?: 1
        val storedDay = prefs[DAY_KEY] ?: LocalDate.now().toEpochDay()
        val today = LocalDate.now().toEpochDay()
        return if (today > storedDay) {
            val next = storedNum + 1
            dataStore.edit {
                it[ENTRY_KEY] = next
                it[DAY_KEY] = today
            }
            next
        } else {
            storedNum
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: EntryNumberStore? = null

        fun getInstance(context: Context): EntryNumberStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EntryNumberStore(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
