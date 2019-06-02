package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.EnablingMutableCollection
import com.lightningkite.reacktive.invokeAll


class SemiboundObservableProperty<T, S>(
        startValue: T,
        val source: MutableObservableProperty<S>,
        val consumeUpdate: CanSetValue<T, S, T>.(S) -> Unit,
        val tryWrite: CanSetValue<T, S, S>.(T) -> Unit
) : EnablingMutableCollection<(T) -> Unit>(), MutableObservableProperty<T> {
    var _value: T = startValue
    override var value: T
        get() = _value
        set(value) {
            updateFromSelf(value)
        }
    var suppressUpdatesFromParent = false

    fun updateFromParent(value: T) {
        this._value = value
        this.invokeAll(this._value)
    }

    fun updateFromSelf(value: T) {
        this._value = value
        this.invokeAll(this._value)
        canSetSource.tryWrite(value)
    }

    val listener = { it: S ->
        if (!suppressUpdatesFromParent) {
            canSetSelf.consumeUpdate(it)
        } else {
            println("Suppressed update")
        }
    }

    override fun enable() {
        source.add(listener)
    }

    override fun disable() {
        source.remove(listener)
    }

    val canSetSelf = object : CanSetValue<T, S, T> {
        override val self: SemiboundObservableProperty<T, S>
            get() = this@SemiboundObservableProperty
        override var value: T
            get() = _value
            set(value) {
                updateFromParent(value)
            }
    }
    val canSetSource = object : CanSetValue<T, S, S> {
        override val self: SemiboundObservableProperty<T, S>
            get() = this@SemiboundObservableProperty
        override var value: S
            get() = source.value
            set(value) {
                suppressUpdatesFromParent = true
                source.value = value
                suppressUpdatesFromParent = false
            }
    }

    interface CanSetValue<T, S, V> {
        val self: SemiboundObservableProperty<T, S>
        var value: V
    }
}