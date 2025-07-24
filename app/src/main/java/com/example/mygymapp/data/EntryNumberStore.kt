package com.example.mygymapp.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.time.LocalDate

private val Context.entryDataStore by preferencesDataStore("journal_entries")

class EntryNumberStore private constructor(private val context: Context) {
    private val dataStore = context.entryDataStore
    private val ENTRY_KEY = intPreferencesKey("entry_number")
    private val DATE_KEY = stringPreferencesKey("last_finished_date")

    val entryNumberFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[ENTRY_KEY] ?: 1
    }

    suspend fun loadCurrent(): Int {
        val prefs = dataStore.data.first()
        val storedNum = prefs[ENTRY_KEY] ?: 1
        val lastFinished = prefs[DATE_KEY]?.let { LocalDate.parse(it) }
        val today = LocalDate.now()
        return if (lastFinished == today.minusDays(1)) {
            val next = storedNum + 1
            dataStore.edit {
                it[ENTRY_KEY] = next
                it[DATE_KEY] = today.toString()
            }
            next
        } else {
            storedNum
        }
    }

    suspend fun markFinishedToday() {
        dataStore.edit {
            it[DATE_KEY] = LocalDate.now().toString()
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