package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.NoOpMutableCollection
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty

class ConstantObservableList<T>(val originalList: List<T>): ObservableList<T>, List<T> by originalList {
    override val onListAdd: MutableCollection<(T, Int) -> Unit> get() = NoOpMutableCollection.type()
    override val onListChange: MutableCollection<(T, T, Int) -> Unit> get() = NoOpMutableCollection.type()
    override val onListMove: MutableCollection<(T, Int, Int) -> Unit> get() = NoOpMutableCollection.type()
    override val onListRemove: MutableCollection<(T, Int) -> Unit> get() = NoOpMutableCollection.type()
    override val onListReplace: MutableCollection<(ObservableList<T>) -> Unit> get() = NoOpMutableCollection.type()
    override val onListUpdate: ObservableProperty<ObservableList<T>> get() = ConstantObservableProperty(this)
}