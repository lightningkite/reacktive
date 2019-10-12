package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.event.*
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.*

class FilteringObservableCollection<V>(
        val source: ObservableCollection<V>,
        val filter: ObservableProperty<(V) -> Boolean>
) : ObservableCollection<V> {
    override var size: Int = 0

    private val filterPrevious = filter.onChangeWithPrevious()
    override val onCollectionAdd: Event<V> = combine(
            source.onCollectionAdd.filter(filter.value),
            source.onCollectionChange.filter { !filter.value(it.first) && filter.value(it.second) }.map { it.second },
            filterPrevious.multiMap { (old, new) -> source.filter { !old(it) && new(it) } }
    )
    override val onCollectionChange: Event<Pair<V, V>> = source.onCollectionChange.filter { filter.value(it.first) && filter.value(it.second) }
    override val onCollectionRemove: Event<V> = combine(
            source.onCollectionRemove.filter(filter.value),
            source.onCollectionChange.filter { filter.value(it.first) && !filter.value(it.second) }.map { it.first },
            filterPrevious.multiMap { (old, new) -> source.filter { old(it) && !new(it) } }
    )
    override val onCollectionReplace: Event<ObservableCollection<V>> = source.onCollectionReplace.map { this }
    private val manualOnChange = StandardEvent<ObservableCollection<V>>()
    override val onChange: Event<ObservableCollection<V>> = combine<ObservableCollection<V>>(manualOnChange, source.onChange.map { this })

    override fun contains(element: V): Boolean = filter.value(element) && source.contains(element)
    override fun containsAll(elements: Collection<V>): Boolean = elements.all(filter.value) && source.containsAll(elements)
    override fun isEmpty(): Boolean = size == 0

    override fun iterator(): Iterator<V>  = object : Iterator<V> {
        val underlying = source.iterator()

        var ready: Boolean = false
        var current: V? = null
        var atEnd: Boolean = false

        init{ advance() }
        fun advance(){
            if(ready || atEnd) return
            while(underlying.hasNext()){
                val maybe = underlying.next()
                if(filter.value(maybe)){
                    ready = true
                    current = maybe
                    break
                }
            }
            current = null
            atEnd = true
        }

        override fun hasNext(): Boolean {
            advance()
            return !atEnd
        }

        override fun next(): V {
            advance()
            ready = false
            @Suppress("UNCHECKED_CAST")
            return current as V
        }
    }
}

fun <V> ObservableCollection<V>.filtering(filter: (V)->Boolean) = FilteringObservableCollection<V>(this, ConstantObservableProperty(filter))
fun <V> ObservableCollection<V>.filtering(filter: ObservableProperty<(V)->Boolean>) = FilteringObservableCollection<V>(this, filter)