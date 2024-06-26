package com.icbc.selfserviceticketing.deviceservice

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * DataStoreManager
 * @date 2022/5/9
 * @author 锅得铁
 * @since v1.0
 */

internal object DataStoreManager {
    /**
     * 旋转角
     */
    private val ROTATION = intPreferencesKey("rotation")
    private val ID_CARD = intPreferencesKey("IDCard")
    const val ID_M40 = 0
    const val ID_180 = 1

    suspend fun setRotation(context: Context, tel: Int) {
        context.dataStore.edit {
            it[ROTATION] = tel
        }
    }

    suspend fun getRotation(context: Context): Flow<Int> {
        return context.dataStore.data.mapNotNull { it[ROTATION] ?: 0 }
    }

    suspend fun setIdCard(context: Context, id: Int) {
        context.dataStore.edit {
            it[ID_CARD] = id
        }
    }

    suspend fun getIDCard(context: Context): Flow<Int> {
        return context.dataStore.data.mapNotNull { it[ID_CARD] ?: 0 }
    }
}