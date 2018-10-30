package com.lightningkite.reacktive.collection

import com.lightningkite.kommon.collection.mapping
import com.lightningkite.kommon.collection.mappingMutable
import com.lightningkite.kommon.collection.mappingWriteOnly
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform

open class MappingObservableCollection<A, B>(
        val source: ObservableCollection<A>,
        val transform: (A)->B,
        val reverse: (B)->A
) : ObservableCollection<B> {
    override val size: Int get() = source.size
    override fun contains(element: B): Boolean = source.contains(element.let(reverse))
    override fun containsAll(elements: Collection<B>): Boolean = source.containsAll(elements.map(reverse))
    override fun isEmpty(): Boolean = source.isEmpty()
    override fun iterator(): Iterator<B> = source.iterator().mapping(transform)

    override val onCollectionAdd: MutableCollection<(value: B) -> Unit> = source.onCollectionAdd.mappingWriteOnly { myCallback ->
        { value -> myCallback.invoke(value.let(transform)) }
    }
    override val onCollectionChange: MutableCollection<(old: B, new: B) -> Unit> = source.onCollectionChange.mappingWriteOnly { myCallback ->
        { old, new -> myCallback.invoke(old.let(transform), new.let(transform)) }
    }
    override val onCollectionRemove: MutableCollection<(value: B) -> Unit> = source.onCollectionRemove.mappingWriteOnly { myCallback ->
        { value -> myCallback.invoke(value.let(transform)) }
    }
    override val onCollectionUpdate: ObservableProperty<ObservableCollection<B>> = source.onCollectionUpdate.transform { this }
    override val onCollectionReplace: MutableCollection<(ObservableCollection<B>) -> Unit> = source.onCollectionReplace.mappingWriteOnly { myCallback ->
        { value -> myCallback.invoke(this) }
    }
}

fun <S, E> ObservableCollection<S>.mapping(read: (S) -> E, write: (E) -> S) = MappingObservableCollection<S, E>(this, read, write)
fun <S, E> ObservableCollection<S>.mapping(read: (S) -> E) = MappingObservableCollection(this, read) { throw IllegalArgumentException() }
