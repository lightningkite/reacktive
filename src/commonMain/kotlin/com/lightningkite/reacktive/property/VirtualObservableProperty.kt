package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.EnablingMutableCollection


class VirtualObservableProperty<T>(
        val getterFun: () -> T,
        val event: MutableCollection<(T) -> Unit> = ArrayList()
) : ObservableProperty<T>, MutableCollection<(T)->Unit> by event {

    override val value: T
        get() = getterFun()
}