package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.NoOpMutableCollection
import com.lightningkite.reacktive.collection.ObservableCollection
import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.NeverEvent
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty

class ConstantObservableList<T>(val list: List<T>): ObservableList<T>, List<T> by list {
    override val onListAdd: Event<Pair<T, Int>>
        get() = NeverEvent()
    override val onListChange: Event<Triple<T, T, Int>>
        get() = NeverEvent()
    override val onListMove: Event<Triple<T, Int, Int>>
        get() = NeverEvent()
    override val onListRemove: Event<Pair<T, Int>>
        get() = NeverEvent()
    override val onListReplace: Event<ObservableList<T>>
        get() = NeverEvent()
    override val onCollectionAdd: Event<T>
        get() = NeverEvent()
    override val onCollectionChange: Event<Pair<T, T>>
        get() = NeverEvent()
    override val onCollectionRemove: Event<T>
        get() = NeverEvent()
    override val onCollectionReplace: Event<ObservableCollection<T>>
        get() = NeverEvent()
    override val onChange: Event<ObservableList<T>>
        get() = NeverEvent()
}

fun <T> List<T>.constant() = ConstantObservableList<T>(this)