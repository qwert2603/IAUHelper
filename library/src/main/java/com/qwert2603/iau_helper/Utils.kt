package com.qwert2603.iau_helper

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal fun prefsLong(prefs: SharedPreferences, key: String, defaultValue: Long = 0L) =
    object : ReadWriteProperty<Any?, Long> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Long {
            return prefs.getLong(key, defaultValue)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
            prefs.edit()
                .putLong(key, value)
                .apply()
        }
    }
