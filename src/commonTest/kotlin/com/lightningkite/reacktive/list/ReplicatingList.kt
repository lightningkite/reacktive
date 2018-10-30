package com.lightningkite.reacktive.list

import kotlin.test.assertTrue
import kotlin.test.assertEquals

class ReplicatingList<E>(val other: ObservableList<E>) : MutableList<E> by other.toMutableList() {
    var updateCounter = 0
    var updateExpectedFrom: String? = null
    init{
        other.onListAdd += { new, index ->
            updateExpectedFrom = "onListAdd $new $index"
            add(index, new)
        }
        other.onListChange += { old, new, index ->
            updateExpectedFrom = "onListChange $old $new $index"
            val myOld = get(index)
            assertEquals(myOld, old)
            set(index, new)
        }
        other.onListRemove += { old, index ->
            updateExpectedFrom = "onListRemove $old $index"
            val myOld = get(index)
            assertEquals(myOld, old)
            removeAt(index)
        }
        other.onListMove += { element, old, new ->
            updateExpectedFrom = "onListMove $element $old $new"
            add(new, removeAt(old))
        }
        other.onListReplace += { new ->
            updateExpectedFrom = "onListReplace $new"
            clear()
            addAll(new)
        }
        other.onListUpdate += {
            updateCounter++
        }
    }
    fun check(){
        assertEquals(this.size, other.size)
        assertTrue("Lists not the same"){this deepEquals other}
        assertTrue("Update expected from $updateExpectedFrom but did not occur"){ updateExpectedFrom == null || updateCounter > 0 }
        updateCounter = 0
        updateExpectedFrom = null
    }

    infix fun <T> List<T>.deepEquals(other: List<T>): Boolean {
        if (size != other.size) return false
        for (index in indices) {
            if (this[index] != other[index]) return false
        }
        return true
    }
}