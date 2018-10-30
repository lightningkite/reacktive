package com.lightningkite.reacktive.set

import com.lightningkite.kommon.collection.mappingMutable
import com.lightningkite.kommon.collection.mappingWriteOnly
import com.lightningkite.reacktive.collection.MutableObservableCollection
import com.lightningkite.reacktive.collection.ObservableCollection
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform

class MappingMutableObservableSet<A, B>(
        val mutableSource: MutableObservableSet<A>,
        transform: (A)->B,
        reverse: (B)->A
) : MappingObservableSet<A, B>(mutableSource, transform, reverse), MutableObservableSet<B> {
    override fun replace(collection: Collection<B>) = mutableSource.replace(collection.map(reverse))
    override fun add(element: B): Boolean = mutableSource.add(element.let(reverse))
    override fun addAll(elements: Collection<B>): Boolean = mutableSource.addAll(elements.map(reverse))
    override fun clear() = mutableSource.clear()
    override fun iterator(): MutableIterator<B> = mutableSource.iterator().mappingMutable(transform)
    override fun remove(element: B): Boolean = mutableSource.remove(element.let(reverse))
    override fun removeAll(elements: Collection<B>): Boolean = mutableSource.removeAll(elements.map(reverse))
    override fun retainAll(elements: Collection<B>): Boolean = mutableSource.retainAll(elements.map(reverse))
}

fun <A, B> MutableObservableSet<A>.mapping(transform: (A) -> B, reverse: (B) -> A): MappingMutableObservableSet<A, B> = MappingMutableObservableSet(this, transform, reverse)
