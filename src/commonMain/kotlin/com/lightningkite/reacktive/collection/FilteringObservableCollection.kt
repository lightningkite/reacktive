package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.invokeAll

class FilteringObservableCollection<V>(
        val source: MutableObservableCollection<V>,
        filter: (V)->Boolean
) : EnablingObservableCollection<V>(), MutableObservableCollection<V> {
    override var size: Int = 0

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
            field = value
            refresh()
        }

    val onCollectionAddListener: (value: V) -> Unit = { value: V ->
        if(filter(value)){
            size++
            onCollectionAdd.invokeAll(value)
        }
    }
    val onCollectionChangeListener: (old: V, new: V) -> Unit = { old: V, new: V ->
        val prev = filter(old)
        val now = filter(new)
        if(!prev && now){
            size++
            onCollectionAdd.invokeAll(new)
        } else if(prev && !now){
            size--
            onCollectionRemove.invokeAll(old)
        } else  if(now) {
            onCollectionChange.invokeAll(old, new)
        }
    }
    val onCollectionRemoveListener: (value: V) -> Unit = { value: V ->
        if(filter(value)){
            size--
            onCollectionRemove.invokeAll(value)
        }
    }
    val onCollectionReplaceListener: (ObservableCollection<V>) -> Unit = { collection: ObservableCollection<V> ->
        refresh()
    }

    override fun refresh() {
        size = source.count(filter)
        onCollectionReplace.invokeAll(this)
        onCollectionUpdate.update()
    }

    override fun enable() {
        source.onCollectionAdd.add(onCollectionAddListener)
        source.onCollectionChange.add(onCollectionChangeListener)
        source.onCollectionRemove.add(onCollectionRemoveListener)
        source.onCollectionReplace.add(onCollectionReplaceListener)
    }

    override fun disable() {
        source.onCollectionAdd.remove(onCollectionAddListener)
        source.onCollectionChange.remove(onCollectionChangeListener)
        source.onCollectionRemove.remove(onCollectionRemoveListener)
        source.onCollectionReplace.remove(onCollectionReplaceListener)
    }

}
