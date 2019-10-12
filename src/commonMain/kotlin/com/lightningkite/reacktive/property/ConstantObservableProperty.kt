package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.NeverEvent


/**
 * A constant observable property - the value never changes.
 * Created by joseph on 12/2/16.
 */
class ConstantObservableProperty<T>(override val value: T) : ObservableProperty<T> {
    override val onChange: Event<T>
        get() = NeverEvent()
}

fun <T> T.constant() = ConstantObservableProperty(this)