package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.EnablingMutableCollection
import com.lightningkite.reacktive.invokeAll


/**
 * Transforms an observable property from one type to another.
 * Created by jivie on 2/22/16.
 */
class TransformOnChangeObservableProperty<S, T>(
        val observable: ObservableProperty<S>,
        val transformer: (S) -> T
) : EnablingMutableCollection<(T) -> Unit>(), ObservableProperty<T> {
    var lastTransformed: S = observable.value
    override var value: T = lastTransformed.let(transformer)
        get() {
            updateSelfIfNeeded()
            return field
        }

    private fun updateSelfIfNeeded() {
        if (lastTransformed != observable.value) {
            lastTransformed = observable.value
            value = lastTransformed.let(transformer)
        }
    }

    val callback = { a: S ->
        updateSelfIfNeeded()
        invokeAll(value)
    }

    override fun enable() {
        observable.add(callback)
    }

    override fun disable() {
        observable.remove(callback)
    }
}

fun <S, T> ObservableProperty<S>.transformOnChange(mapper: (S) -> T): TransformObservableProperty<S, T> {
    return TransformObservableProperty(this, mapper)
}