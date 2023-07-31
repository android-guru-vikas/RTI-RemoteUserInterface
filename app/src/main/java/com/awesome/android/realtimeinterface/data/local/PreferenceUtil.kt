package com.awesome.android.realtimeinterface.data.local

import android.content.Context
import android.content.SharedPreferences
import com.awesome.android.realtimeinterface.utils.Constants

object PreferenceUtil {


    private lateinit var sharedPreferences: SharedPreferences
    private const val PREFERENCE_APP_DATA = "pref_rti"

    fun initPreference(context: Context) {
        sharedPreferences = context.getSharedPreferences(
            PREFERENCE_APP_DATA,
            Context.MODE_PRIVATE
        )
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    operator fun SharedPreferences.set(key: String, value: Any?) = when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        is Set<*> -> edit { it.putStringSet(key, value as MutableSet<String>?) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }

    inline operator fun <reified T : Any> SharedPreferences.get(
        key: String,
        defaultValue: T? = null
    ): T = when (T::class) {
        String::class -> getString(key, defaultValue as? String ?: "") as T
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T
        Set::class -> getStringSet(
            key,
            defaultValue as? MutableSet<String> ?: mutableSetOf<String>()
        ) as T
        else -> throw UnsupportedOperationException("Not yet implemented")
    }

    var UI_VERSION
        set(value) = sharedPreferences.set(Constants.PREF_CURRENT_UI_VERSION, value)
        get() = sharedPreferences[Constants.PREF_CURRENT_UI_VERSION, 0]
}