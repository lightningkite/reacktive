package com.lightningkite.reacktive.property


/**
 * A constant observable property - the value never changes.
 * Created by joseph on 12/2/16.
 */
class ConstantObservableProperty<T>(override val value: T) : ObservableProperty<T> {
    override val size: Int get() = 0
    override fun contains(element: (T) -> Unit): Boolean = false
    override fun containsAll(elements: Collection<(T) -> Unit>): Boolean = false
    override fun isEmpty(): Boolean = true
    override fun addAll(elements: Collection<(T) -> Unit>): Boolean = false
    override fun clear() {}
    override fun iterator(): MutableIterator<(T) -> Unit> = object : MutableIterator<(T)->Unit>{
        override fun hasNext(): Boolean = false
        override fun next(): (T) -> Unit = throw UnsupportedOperationException()
        override fun remove() {}
    }
    override fun removeAll(elements: Collection<(T) -> Unit>): Boolean = false
    override fun retainAll(elements: Collection<(T) -> Unit>): Boolean = false
    override fun add(element: (T) -> Unit): Boolean = false
    override fun remove(element: (T) -> Unit): Boolean = false
}