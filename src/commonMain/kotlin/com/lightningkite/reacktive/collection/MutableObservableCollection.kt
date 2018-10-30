package com.lightningkite.reacktive.collection

interface MutableObservableCollection<V> : MutableCollection<V>, ObservableCollection<V>{
    fun change(old: V, new: V){
        if(!remove(old)) throw IllegalArgumentException("$old not in the collection!")
        add(new)
    }
    fun replace(collection: Collection<V>)
}
