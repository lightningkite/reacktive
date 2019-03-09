package com.lightningkite.reacktive.property


import com.lightningkite.kommon.property.PropertyDelegate
import com.lightningkite.reacktive.Event
import kotlin.reflect.KProperty

/**
 * A property that can be observed.
 * Created by josep on 1/28/2016.
 */
interface ObservableProperty<T> : Event<T>, PropertyDelegate<T>