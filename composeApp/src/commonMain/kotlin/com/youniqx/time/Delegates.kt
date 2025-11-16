package com.youniqx.time

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

infix fun <T> MutableState<T>.onSet(beforeSet: (currentValue: T, newValue: T) -> Unit) =
    object : ReadWriteProperty<Any?, T> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>) = this@onSet.getValue(thisRef, property)
        override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            beforeSet(getValue(thisRef, property), value)
            this@onSet.setValue(thisRef, property, value)
        }
    }