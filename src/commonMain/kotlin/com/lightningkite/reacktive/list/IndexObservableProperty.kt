package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.EnablingMutableCollection
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.update

fun <E> ObservableList<E>.firstOrNullObservable() = IndexObservableProperty(this, 0)
fun <E> ObservableList<E>.indexOrNullObservable(index:Int) = IndexObservableProperty(this, index)

class IndexObservableProperty<T>(
    val list: ObservableList<T>,
    val watchIndex: Int = 0
) : ObservableProperty<T?>, EnablingMutableCollection<(T?) -> Unit>() {
    override val value: T? get() = list.lastOrNull()

    val onListAdd = { element: T, index: Int ->
        if (index <= watchIndex) {
            update()
        }
    }
    val onListRemove = { element: T, index: Int ->
        if (index >= watchIndex) {
            update()
        }
    }
    val onListMove = { element: T, oldIndex: Int, newIndex: Int ->
        if (watchIndex in oldIndex .. newIndex) {
            update()
        }
    }
    val onListChange = { oldElement: T, element: T, index: Int ->
        if (index == watchIndex) {
            update()
        }
    }
    val onListReplace = { list: ObservableList<T> ->
        update()
    }

    override fun enable() {
        list.onListAdd.add(onListAdd)
        list.onListRemove.add(onListRemove)
        list.onListMove.add(onListMove)
        list.onListChange.add(onListChange)
        list.onListReplace.add(onListReplace)
    }

    override fun disable() {
        list.onListAdd.remove(onListAdd)
        list.onListRemove.remove(onListRemove)
        list.onListMove.remove(onListMove)
        list.onListChange.remove(onListChange)
        list.onListReplace.remove(onListReplace)
    }
}