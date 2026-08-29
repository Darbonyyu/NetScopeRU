package ru.netscope.core.data
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
private val Context.netScopeSettings by preferencesDataStore("netscope_settings")
class SettingsRepository @Inject constructor(@ApplicationContext private val context: Context) {
 val beginnerMode: Flow<Boolean> = context.netScopeSettings.data.map { it[BEGINNER_MODE] ?: true }
 val backgroundCollection: Flow<Boolean> = context.netScopeSettings.data.map { it[BACKGROUND_COLLECTION] ?: false }
 suspend fun setBeginnerMode(enabled: Boolean) { context.netScopeSettings.edit { it[BEGINNER_MODE] = enabled } }
 suspend fun setBackgroundCollection(enabled: Boolean) { context.netScopeSettings.edit { it[BACKGROUND_COLLECTION] = enabled } }
 private companion object { val BEGINNER_MODE = booleanPreferencesKey("beginner_mode"); val BACKGROUND_COLLECTION = booleanPreferencesKey("background_collection") }
}
