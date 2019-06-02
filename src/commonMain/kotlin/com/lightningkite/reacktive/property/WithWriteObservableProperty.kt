package com.lightningkite.reacktive.property

class WithWriteObservableProperty<T>(val wraps: ObservableProperty<T>, val setter: (T) -> Unit) : MutableObservableProperty<T>, ObservableProperty<T> by wraps {
    override var value: T
        get() = wraps.value
        set(value) {
            setter(value)
        }
}

fun <T> ObservableProperty<T>.withWrite(setter: (T)->Unit) = WithWriteObservableProperty(this, setter)