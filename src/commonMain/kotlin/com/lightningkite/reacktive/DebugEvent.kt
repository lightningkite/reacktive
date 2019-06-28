package com.lightningkite.reacktive

import com.lightningkite.kommon.exception.stackTraceString

class DebugEvent<E>(): Event<E> {
    val backing = ArrayList<(E)->Unit>()
    var lastModificationTrace: String? = null

    override val size: Int get() = backing.size

    override fun add(element: (E) -> Unit): Boolean {
        lastModificationTrace = Exception().stackTraceString()
        return backing.add(element)
    }

    override fun addAll(elements: Collection<(E) -> Unit>): Boolean {
        lastModificationTrace = Exception().stackTraceString()
        return backing.addAll(elements)
    }

    override fun clear() {
        lastModificationTrace = Exception().stackTraceString()
        return backing.clear()
    }

    override fun contains(element: (E) -> Unit): Boolean {
        return backing.contains(element)
    }

    override fun containsAll(elements: Collection<(E) -> Unit>): Boolean {
        return backing.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return backing.isEmpty()
    }

    override fun iterator(): MutableIterator<(E) -> Unit> {
        return object : MutableIterator<(E)->Unit> {
            val itr = backing.iterator()
            override fun hasNext(): Boolean {
                try {
                    return itr.hasNext()
                } catch(e: ConcurrentModificationException){
                    throw Exception("Concurrent modification, last set at ${lastModificationTrace}", e)
                }
            }

            override fun next(): (E) -> Unit {
                try {
                    return itr.next()
                } catch(e: ConcurrentModificationException){
                    throw Exception("Concurrent modification, last set at ${lastModificationTrace}", e)
                }
            }

            override fun remove() {
                try {
                    return itr.remove()
                } catch(e: ConcurrentModificationException){
                    throw Exception("Concurrent modification, last set at ${lastModificationTrace}", e)
                }
            }
        }
    }

    override fun remove(element: (E) -> Unit): Boolean {
        lastModificationTrace = Exception().stackTraceString()
        return backing.remove(element)
    }

    override fun removeAll(elements: Collection<(E) -> Unit>): Boolean {
        lastModificationTrace = Exception().stackTraceString()
        return backing.removeAll(elements)
    }

    override fun retainAll(elements: Collection<(E) -> Unit>): Boolean {
        lastModificationTrace = Exception().stackTraceString()
        return backing.retainAll(elements)
    }


}