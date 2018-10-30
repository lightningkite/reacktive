package com.lightningkite.reacktive.list


import com.lightningkite.reacktive.collection.MutableObservableCollection
import com.lightningkite.reacktive.property.ObservableProperty

/**
 * Allows you to observe the changes to a list.
 * Created by josep on 9/7/2015.
 */
interface MutableObservableList<E> : MutableList<E>, ObservableList<E>, MutableObservableCollection<E> {

    override fun change(old: E, new: E) {
        set(indexOf(old), new)
    }

    fun move(fromIndex: Int, toIndex: Int)
    fun updateAt(index: Int) {
        this[index] = this[index]
    }

    fun update(element: E): Boolean {
        val index = indexOf(element)
        if (index != -1)
            updateAt(index)
        return index != -1
    }
}
