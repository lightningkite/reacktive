package com.lightningkite.reacktive.list


import com.lightningkite.reacktive.mapping.mappingWriteOnly
import com.lightningkite.reacktive.collection.ObservableCollection
import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.map
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform

/**
 * Allows you to observe the changes to a list.
 * Created by josep on 9/7/2015.
 */
interface ObservableList<E> : List<E>, ObservableCollection<E> {
    val onListAdd: Event<Pair<E, Int>>
    val onListChange: Event<Triple<E, E, Int>>
    val onListMove: Event<Triple<E, Int, Int>>
    val onListRemove: Event<Pair<E, Int>>
    val onListReplace: Event<ObservableList<E>>
    override val onCollectionAdd: Event<E> get() = onListAdd.map { it.first }
    override val onCollectionChange: Event<Pair<E, E>> get() = onListChange.map { it.first to it.second }
    override val onCollectionRemove: Event<E> get() = onListRemove.map { it.first }
    override val onCollectionReplace: Event<ObservableCollection<E>> get() = onListReplace.map { this }
    override val onChange: Event<ObservableList<E>>
}
