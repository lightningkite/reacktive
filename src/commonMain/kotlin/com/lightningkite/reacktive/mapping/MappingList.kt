package com.lightningkite.reacktive.mapping

/**
 * A class that makes an immutable list's elements act as if it were of a different type.
 */
class MappingList<S, T>(val around: List<S>, val read: (S) -> T, val write: (T) -> S) : List<T> {
    override fun contains(element: T): Boolean = around.contains(write(element))
    override fun containsAll(elements: Collection<T>): Boolean = around.containsAll(elements.map(write))
    override fun get(index: Int): T = read(around.get(index))
    override fun indexOf(element: T): Int = around.indexOf(write(element))
    override fun isEmpty(): Boolean = around.isEmpty()
    override fun iterator(): Iterator<T> = around.iterator().mapping(read)
    override fun lastIndexOf(element: T): Int = around.lastIndexOf(write(element))
    override fun listIterator(): ListIterator<T> = around.listIterator().mapping(read)
    override fun listIterator(index: Int): ListIterator<T> = around.listIterator(index).mapping(read)
    override fun subList(fromIndex: Int, toIndex: Int): List<T> = around.subList(fromIndex, toIndex).mapping(read, write)
    override val size: Int get() = around.size

}

/**
 * Wraps a list, making it act as if the elements were of type [T] instead of [S].
 */
fun <S, T> List<S>.mapping(read: (S) -> T, write: (T) -> S): List<T> = MappingList(this, read, write)
