package com.lightningkite.reacktive.property


import com.lightningkite.kommon.property.MutablePropertyDelegate
import kotlin.reflect.KProperty

/**
 * An observable property that can be modified.
 * Created by josep on 5/31/2016.
 */
interface MutableObservableProperty<T> : ObservableProperty<T>, MutablePropertyDelegate<T>