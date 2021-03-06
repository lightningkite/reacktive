


package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.EnablingMutableCollection


/**
 * Transforms an observable property from one type to another.
 * Created by jivie on 2/22/16.
 */
class TransformMutableObservableProperty<S, T>(
        val observable: MutableObservableProperty<S>,
        val transformer: (S) -> T,
        val reverseTransformer: (T) -> S
) : EnablingMutableCollection<(T) -> Unit>(), MutableObservableProperty<T> {
    override var value: T
        get() = transformer(observable.value)
        set(value) {
            observable.value = reverseTransformer(value)
        }

    val callback = { a: S ->
        val wrapped = transformer(a)
        forEach { it.invoke(wrapped) }
    }

    override fun enable() {
        observable.add(callback)
    }

    override fun disable() {
        observable.remove(callback)
    }
}

fun <S, T> MutableObservableProperty<S>.transform(mapper: (S) -> T, reverseMapper: (T) -> S): TransformMutableObservableProperty<S, T> {
    return TransformMutableObservableProperty(this, mapper, reverseMapper)
}