package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.EnablingMutableCollection


/**
 *
 * Created by josep on 8/19/2017.
 */
class VirtualMutableObservableProperty<T>(
    val getterFun: () -> T,
    val setterFun: (T) -> Unit,
    val event: MutableCollection<(T) -> Unit>
) : MutableObservableProperty<T>, MutableCollection<(T) -> Unit> by event {

    override var value: T
        get() = getterFun()
        set(value) {
            setterFun(value)
        }
}
