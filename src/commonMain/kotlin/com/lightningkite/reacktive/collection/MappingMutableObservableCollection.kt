package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.mapping.mappingMutable
import com.lightningkite.reacktive.mapping.mappingWriteOnly
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform

class MappingMutableObservableCollection<A, B>(
        val mutableSource: MutableObservableCollection<A>,
        transform: (A)->B,
        reverse: (B)->A
) : MappingObservableCollection<A, B>(mutableSource, transform, reverse), MutableObservableCollection<B> {
    override fun change(old: B, new: B) = mutableSource.change(old.let(reverse), new.let(reverse))
    override fun replace(collection: Collection<B>) = mutableSource.replace(collection.map(reverse))
    override fun add(element: B): Boolean = mutableSource.add(element.let(reverse))
    override fun addAll(elements: Collection<B>): Boolean = mutableSource.addAll(elements.map(reverse))
    override fun clear() = mutableSource.clear()
    override fun iterator(): MutableIterator<B> = mutableSource.iterator().mappingMutable(transform)
    override fun remove(element: B): Boolean = mutableSource.remove(element.let(reverse))
    override fun removeAll(elements: Collection<B>): Boolean = mutableSource.removeAll(elements.map(reverse))
    override fun retainAll(elements: Collection<B>): Boolean = mutableSource.retainAll(elements.map(reverse))
}

fun <S, E> MutableObservableCollection<S>.mapping(read: (S) -> E, write: (E) -> S) = MappingMutableObservableCollection<S, E>(this, read, write)
fun <S, E> MutableObservableCollection<S>.mapping(read: (S) -> E) = MappingMutableObservableCollection(this, read) { throw IllegalArgumentException() }

