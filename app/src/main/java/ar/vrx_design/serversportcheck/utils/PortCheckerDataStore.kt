package ar.vrx_design.serversportcheck.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("port_checker_data")

object PortCheckerDataStore {
    private val KEY_ROWS = stringSetPreferencesKey("rows")
    private val gson = Gson()

    suspend fun saveRows(context: Context, rows: List<Pair<String, String>>) {
        val serializedRows = rows.map { gson.toJson(it) }.toSet()
        context.dataStore.edit { preferences ->
            preferences[KEY_ROWS] = serializedRows
        }
    }

    suspend fun loadRows(context: Context): List<Pair<String, String>> {
        val serializedRows = context.dataStore.data.map { preferences ->
            preferences[KEY_ROWS] ?: emptySet()
        }.first()
        return serializedRows.map { gson.fromJson(it, Pair::class.java) as Pair<String, String> }
    }
}
