package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.map.GroupingObservableMap
import com.lightningkite.reacktive.map.groupingBy
import com.lightningkite.reacktive.set.WrapperObservableSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.random.Random

class GroupingObservableMapTest {

    @Test
    fun simpleTest() {
        val set = WrapperObservableSet(('a'..'z').toMutableSet())
        val grouping = set.groupingBy { it.toInt() % 4 }
        println(grouping.entries.joinToString("\n") { "${it.key}=${it.value.joinToString()}" })
        assertTrue { grouping.size == 4 }
    }

    @Test
    fun insertionTest() {
        val set = WrapperObservableSet(('a'..'y').toMutableSet())
        val grouping = set.groupingBy { it.toInt() % 4 }
        println(grouping.entries.joinToString("\n") { "${it.key}=${it.value.joinToString()}" })
        var callbackHappened = false
        grouping[2]!!.onCollectionAdd += {
            println("callback")
            assertEquals(7, grouping[2]!!.size)
            assertEquals('z', it)
            callbackHappened = true
        }
        println("INSERT Z")
        set.add('z')
        println(grouping.entries.joinToString("\n") { "${it.key}=${it.value.joinToString()}" })
        assertEquals(7, grouping[2]!!.size)
        assertTrue { callbackHappened }
    }

    @Test
    fun removalTest() {
        val set = WrapperObservableSet(('a'..'z').toMutableSet())
        val grouping = set.groupingBy { it.toInt() % 4 }
        println(grouping.entries.joinToString("\n") { "${it.key}=${it.value.joinToString()}" })
        var callbackHappened = false
        grouping[2]!!.onCollectionRemove += {
            println("callback")
            assertEquals(6, grouping[2]!!.size)
            assertEquals('b', it)
            callbackHappened = true
        }
        println("REMOVE b")
        set.remove('b')
        println(grouping.entries.joinToString("\n") { "${it.key}=${it.value.joinToString()}" })
        assertEquals(6, grouping[2]!!.size)
        assertTrue { callbackHappened }
    }

    @Test
    fun removeHalf() {
        val set = WrapperObservableList((0..100).toMutableList())
        val grouping = set.groupingBy { it % 4 }
        grouping.onMapUpdate += {}

        var on = true
        set.removeAll { on = !on; on }

        println(grouping.entries.joinToString("\n") { "${it.key}=${it.value.joinToString()}" })
    }

    @Test
    fun thrash() {
        val set = WrapperObservableList((0..20).toMutableList())
        val grouping = set.groupingBy { it % 4 }
        grouping.onMapUpdate += {}
        repeat(10000) {
            try {
                set.add(testRandom.nextInt(100))
                set.remove(set.random(testRandom))
                set.change(set.random(testRandom), testRandom.nextInt(100))
                grouping.entries.count { true }
            } catch (t: Throwable) {
                println("Died on $it")
                throw t
            }
        }
    }

}
