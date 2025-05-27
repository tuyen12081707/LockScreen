package com.panda.lockscreen.data.prefernces

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Preferences(private val context: Context,private val name: String? = null) {

    val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(name ?: javaClass.simpleName, Context.MODE_PRIVATE)
    }

    sealed class StorableType {
        data object String : StorableType()
        data object Int : StorableType()
        data object Float : StorableType()
        data object Boolean : StorableType()
        data object Long : StorableType()
        data object StringSet : StorableType()
    }

    inner class GenericPrefDelegate<T : Any>(
        private val prefKey: String?,
        private val defaultValue: T?,
        private val type: StorableType,
    ) : ReadWriteProperty<Preferences, T?> {
        // If not init using by delegate, it always throws an exception when trying to access the value
        private var key: String by Delegates.notNull()
        operator fun provideDelegate(
            thisRef: Preferences,
            property: KProperty<*>
        ): GenericPrefDelegate<T> {
            key = prefKey ?: property.name
            return this
        }

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Preferences, property: KProperty<*>): T? =
            when (type) {
                StorableType.String ->
                    prefs.getString(key, defaultValue as String?) as T?

                StorableType.Int ->
                    prefs.getInt(key, defaultValue as Int) as T?

                StorableType.Float ->
                    prefs.getFloat(key, defaultValue as Float) as T?

                StorableType.Boolean ->
                    prefs.getBoolean(key, defaultValue as Boolean) as T?

                StorableType.Long ->
                    prefs.getLong(prefKey ?: property.name, defaultValue as Long) as T?

                StorableType.StringSet ->
                    prefs.getStringSet(key, defaultValue as Set<String>) as T?
            }


        @Suppress("UNCHECKED_CAST")
        override fun setValue(thisRef: Preferences, property: KProperty<*>, value: T?) =
            when (type) {
                StorableType.String -> {
                    prefs.edit().putString(prefKey ?: property.name, value as String?).apply()
                }

                StorableType.Int -> {
                    prefs.edit().putInt(prefKey ?: property.name, value as Int).apply()
                }

                StorableType.Float -> {
                    prefs.edit().putFloat(prefKey ?: property.name, value as Float).apply()
                }

                StorableType.Boolean -> {
                    prefs.edit().putBoolean(prefKey ?: property.name, value as Boolean).apply()
                }

                StorableType.Long -> {
                    prefs.edit().putLong(prefKey ?: property.name, value as Long).apply()
                }

                StorableType.StringSet -> {
                    prefs.edit().putStringSet(prefKey ?: property.name, value as Set<String>)
                        .apply()
                }
            }
    }

    fun stringPref(prefKey: String? = null, defaultValue: String? = null) =
        GenericPrefDelegate(prefKey, defaultValue, StorableType.String)

    fun intPref(prefKey: String? = null, defaultValue: Int = 0) =
        GenericPrefDelegate(prefKey, defaultValue, StorableType.Int)

    fun floatPref(prefKey: String? = null, defaultValue: Float = 0f) =
        GenericPrefDelegate(prefKey, defaultValue, StorableType.Float)

    fun booleanPref(prefKey: String? = null, defaultValue: Boolean = false) =
        GenericPrefDelegate(prefKey, defaultValue, StorableType.Boolean)

    fun longPref(prefKey: String? = null, defaultValue: Long = 0L) =
        GenericPrefDelegate(prefKey, defaultValue, StorableType.Long)

    fun stringSetPref(prefKey: String? = null, defaultValue: Set<String> = HashSet()) =
        GenericPrefDelegate(prefKey, defaultValue, StorableType.StringSet)
}