package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.event.*
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.combine

class FilteringObservableCollection<V>(
        val source: MutableObservableCollection<V>,
        filter: (V)->Boolean
) : MutableObservableCollection<V> {
    override var size: Int = 0

    private val manualOnAdd = StandardEvent<V>()
    override val onCollectionAdd: Event<V> = combine(
            source.onCollectionAdd.filter(filter),
            source.onCollectionChange.filter { !filter(it.first) && filter(it.second) }.map { it.second },
            manualOnAdd
    )
    override val onCollectionChange: Event<Pair<V, V>> = source.onCollectionChange.filter { filter(it.first) && filter(it.second) }
    private val manualOnRemove = StandardEvent<V>()
    override val onCollectionRemove: Event<V> = combine(
            source.onCollectionRemove.filter(filter),
            source.onCollectionChange.filter { filter(it.first) && !filter(it.second) }.map { it.first },
            manualOnRemove
    )
    override val onCollectionReplace: Event<ObservableCollection<V>> = source.onCollectionReplace.map { this }
    private val manualOnChange = StandardEvent<ObservableCollection<V>>()
    override val onChange: Event<ObservableCollection<V>> = combine<ObservableCollection<V>>(manualOnChange, source.onChange.map { this })

    override fun change(old: V, new: V) = source.change(old, new)
    override fun contains(element: V): Boolean = filter(element) && source.contains(element)
    override fun containsAll(elements: Collection<V>): Boolean = elements.all(filter) && source.containsAll(elements)
    override fun isEmpty(): Boolean = size == 0
    override fun replace(collection: Collection<V>) = source.replace(collection)
    override fun add(element: V): Boolean = source.add(element)
    override fun addAll(elements: Collection<V>): Boolean = source.addAll(elements)
    override fun clear() = source.clear()
    override fun remove(element: V): Boolean = source.remove(element)
    override fun removeAll(elements: Collection<V>): Boolean = source.removeAll(elements)
    override fun retainAll(elements: Collection<V>): Boolean = source.retainAll(elements)

    override fun iterator(): MutableIterator<V>  = object : MutableIterator<V> {
        val underlying = source.iterator()

        var ready: Boolean = false
        var current: V? = null
        var atEnd: Boolean = false

        init{ advance() }
        fun advance(){
            if(ready || atEnd) return
            while(underlying.hasNext()){
                val maybe = underlying.next()
                if(filter(maybe)){
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

        //TODO: This may have issues, as hasNext() changes the internal state.  Might remove the wrong one if the conditions are right.
        override fun remove() = underlying.remove()
    }

    var filter: (V)->Boolean = filter
        set(value){
            val old = field
            field = value
            for(item in source){
                val oldPasses = old(item)
                val newPasses = value(item)
                if(oldPasses && !newPasses) manualOnRemove.invokeAll(item)
                if(!oldPasses && newPasses) manualOnAdd.invokeAll(item)
            }
            manualOnChange.invokeAll(this)
        }

}
