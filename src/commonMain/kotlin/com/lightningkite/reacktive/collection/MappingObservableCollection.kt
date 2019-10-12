package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.map
import com.lightningkite.reacktive.mapping.mapping
import com.lightningkite.reacktive.mapping.mappingMutable
import com.lightningkite.reacktive.mapping.mappingWriteOnly
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.map
import com.lightningkite.reacktive.property.transform

internal open class MappingObservableCollection<A, B>(
        val source: ObservableCollection<A>,
        val transform: (A)->B,
        val reverse: (B)->A
) : ObservableCollection<B> {
    override val onChange: Event<ObservableCollection<B>> = source.onChange.map { this }
    override val onCollectionAdd: Event<B> = source.onCollectionAdd.map { transform(it) }
    override val onCollectionChange: Event<Pair<B, B>> = source.onCollectionChange.map { transform(it.first) to transform(it.second) }
    override val onCollectionRemove: Event<B> = source.onCollectionRemove.map { transform(it) }
    override val onCollectionReplace: Event<ObservableCollection<B>> = source.onCollectionReplace.map { this }

    override val size: Int get() = source.size
    override fun contains(element: B): Boolean = source.contains(element.let(reverse))
    override fun containsAll(elements: Collection<B>): Boolean = source.containsAll(elements.map(reverse))
    override fun isEmpty(): Boolean = source.isEmpty()
    override fun iterator(): Iterator<B> = source.iterator().mapping(transform)
}

fun <S, E> ObservableCollection<S>.mapping(read: (S) -> E, write: (E) -> S): ObservableCollection<E> = MappingObservableCollection<S, E>(this, read, write)
fun <S, E> ObservableCollection<S>.mapping(read: (S) -> E): ObservableCollection<E> = MappingObservableCollection(this, read) { throw IllegalArgumentException() }
