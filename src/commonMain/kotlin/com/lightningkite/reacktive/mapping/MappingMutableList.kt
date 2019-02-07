package com.lightningkite.reacktive.mapping


/**
 * A class that makes a mutable list's elements act as if it were of a different type.
 */
class MappingMutableList<S, T>(val around: MutableList<S>, val mapper: (S) -> T, val reverseMapper: (T) -> S) : MutableList<T> {
    override fun add(element: T): Boolean = around.add(reverseMapper(element))
    override fun add(index: Int, element: T) = around.add(index, reverseMapper(element))
    override fun addAll(index: Int, elements: Collection<T>): Boolean = around.addAll(index, elements.map(reverseMapper))
    override fun addAll(elements: Collection<T>): Boolean = around.addAll(elements.map(reverseMapper))
    override fun clear() = around.clear()
    override fun remove(element: T): Boolean = around.remove(reverseMapper(element))
    override fun removeAll(elements: Collection<T>): Boolean = around.removeAll(elements.map(reverseMapper))
    override fun removeAt(index: Int): T = mapper(around.removeAt(index))
    override fun retainAll(elements: Collection<T>): Boolean = around.retainAll(elements.map(reverseMapper))
    override fun set(index: Int, element: T): T {
        around[index] = reverseMapper(element)
        return element
    }

    override fun contains(element: T): Boolean = around.contains(reverseMapper(element))
    override fun containsAll(elements: Collection<T>): Boolean = around.containsAll(elements.map(reverseMapper))
    override fun get(index: Int): T = mapper(around.get(index))
    override fun indexOf(element: T): Int = around.indexOf(reverseMapper(element))
    override fun isEmpty(): Boolean = around.isEmpty()
    override fun iterator(): MutableIterator<T> = around.iterator().mappingMutable(mapper)
    override fun lastIndexOf(element: T): Int = around.lastIndexOf(reverseMapper(element))
    override fun listIterator(): MutableListIterator<T> = around.listIterator().mapping(mapper, reverseMapper)
    override fun listIterator(index: Int): MutableListIterator<T> = around.listIterator(index).mapping(mapper, reverseMapper)
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = around.subList(fromIndex, toIndex).mappingMutable(mapper, reverseMapper)
    override val size: Int get() = around.size

}

/**
 * Wraps a list, making it act as if the elements were of type [T] instead of [S].
 */
fun <S, T> MutableList<S>.mappingMutable(read: (S) -> T, write: (T) -> S): MutableList<T> = MappingMutableList(this, read, write)
