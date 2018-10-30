package com.lightningkite.reacktive.list


import com.lightningkite.kommon.collection.mappingWriteOnly
import com.lightningkite.reacktive.collection.ObservableCollection
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform

/**
 * Allows you to observe the changes to a list.
 * Created by josep on 9/7/2015.
 */
interface ObservableList<E> : List<E>, ObservableCollection<E> {
    val onListAdd: MutableCollection<(E, Int) -> Unit>
    val onListChange: MutableCollection<(E, E, Int) -> Unit>
    val onListMove: MutableCollection<(E, Int, Int) -> Unit>
    val onListRemove: MutableCollection<(E, Int) -> Unit>
    val onListReplace: MutableCollection<(ObservableList<E>) -> Unit>
    val onListUpdate: ObservableProperty<ObservableList<E>>

    override val onCollectionAdd: MutableCollection<(value: E) -> Unit> get() = onListAdd.mappingWriteOnly { callback ->
        { item, index -> callback.invoke(item) }
    }
    override val onCollectionChange: MutableCollection<(old: E, new: E) -> Unit> get() = onListChange.mappingWriteOnly { callback ->
        { old, item, index -> callback.invoke(old, item) }
    }
    override val onCollectionRemove: MutableCollection<(value: E) -> Unit> get() = onListRemove.mappingWriteOnly { callback ->
        { item, index -> callback.invoke(item) }
    }
    override val onCollectionUpdate: ObservableProperty<ObservableCollection<E>> get() = onListUpdate.transform { this }
    override val onCollectionReplace: MutableCollection<(ObservableCollection<E>) -> Unit> get() = onListReplace.mappingWriteOnly { callback ->
        { list -> callback.invoke(this) }
    }
}
