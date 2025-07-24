package com.example.mygymapp.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.entryDataStore by preferencesDataStore("journal_entries")

class EntryNumberStore private constructor(private val context: Context) {
    private val dataStore = context.entryDataStore
    private val ENTRY_KEY = intPreferencesKey("entry_number")

    val entryNumberFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[ENTRY_KEY] ?: 1
    }

    suspend fun load(): Int = dataStore.data.first()[ENTRY_KEY] ?: 1

    suspend fun save(value: Int) {
        dataStore.edit { prefs ->
            prefs[ENTRY_KEY] = value
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
