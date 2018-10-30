package com.lightningkite.reacktive.list

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail


/**
 * Created by joseph on 9/26/16.
 */
class FilteringObservableListTest {
    fun makeTestList() = WrapperObservableList((0..20).toMutableList())
    fun makeTestData(): Pair<MutableObservableList<Int>, MutableObservableList<Int>> {
        val list = makeTestList()
        val filtering = list.filtering { it % 2 == 0 }
        return list to filtering
    }

    @Test
    fun filteringWorks() {
        val (list, filtering) = makeTestData()
        assertEquals(list.filter { it % 2 == 0 }.size, filtering.size)
        assertTrue(filtering.all { it % 2 == 0 }, "filtering isn't working. ${filtering.joinToString()}")
    }

    @Test
    fun removeAll() {
        val (list, filtering) = makeTestData()
        list.removeAll { it % 3 == 0 }
        println(filtering.joinToString(transform = Int::toString))
        for (item in filtering) {
            assertTrue(item % 2 == 0)
        }
    }

    @Test
    fun setSourceMissCatch() {
        val (list, filtering) = makeTestData()

        val changeIndex = 3
        val expectIndex = 2
        val newItem = 32

        var callbackOccurred = false

        filtering.onListAdd += { char, index ->
            assertEquals(newItem, char)
            assertEquals(expectIndex, index)

            callbackOccurred = true
        }
        filtering.onListRemove += { item, index -> fail() }
        filtering.onListChange += { old, item, index -> fail() }
        list[changeIndex] = newItem

        assertTrue(callbackOccurred, "callback occurred")
    }

    @Test
    fun setSourceCatchCatch() {
        val (list, filtering) = makeTestData()

        val changeIndex = 4
        val expectIndex = 2
        val newItem = 32
        val oldItem = list[changeIndex]

        var callbackOccurred = false
        filtering.onListChange += { old, char, index ->
            assertEquals(oldItem, old)
            assertEquals(newItem, char)
            assertEquals(expectIndex, index)

            callbackOccurred = true
        }

        filtering.onListAdd += { item, index -> fail() }
        filtering.onListRemove += { item, index -> fail() }
        list[changeIndex] = newItem

        assertTrue(callbackOccurred, "callback occurred")
    }

    @Test
    fun setSourceCatchMiss() {
        val (list, filtering) = makeTestData()

        val changeIndex = 4
        val newItem = 33
        val expectIndex = 2
        var oldItem = list[changeIndex]

        var callbackOccurred = false
        filtering.onListRemove += { char, index ->
            assertEquals(oldItem, char)
            assertEquals(expectIndex, index)

            callbackOccurred = true
        }

        filtering.onListAdd += { item, index -> fail() }
        filtering.onListChange += { old, item, index ->
            if(expectIndex == index) oldItem = item
        }
        list[changeIndex] = newItem

        assertTrue(callbackOccurred, "callback occurred")
    }

    @Test
    fun setSourceMissMiss() {
        val (list, filtering) = makeTestData()

        val changeIndex = 3
        val newItem = 33

        filtering.onListAdd += { item, index -> fail() }
        filtering.onListRemove += { item, index -> fail() }
        filtering.onListChange += { old, item, index -> fail() }
        list[changeIndex] = newItem
    }

    @Test
    fun addSourceCatch() {
        val (list, filtering) = makeTestData()

        val newItem = 22
        val expectIndex = list.filter { it % 2 == 0 }.size

        var callbackOccurred = false
        filtering.onListAdd += { char, index ->
            assertEquals(newItem, char)
            assertEquals(expectIndex, index)

            callbackOccurred = true
        }
        list.add(newItem)

        assertTrue(callbackOccurred, "callback occurred")
    }

    @Test
    fun removeSourceMiss() {
        val (list, filtering) = makeTestData()

        val removeIndex = 3
        assertTrue { list[removeIndex] % 2 != 0 }

        filtering.onListRemove += { char, index ->
            fail()
        }
        list.removeAt(removeIndex)

        filtering.last()
    }

//    @Test
//    fun addAtSource(){
//        val addIndex = 2
//        val newItem = 'z'
//
//        var callbackOccurred = false
//        val list = makeTestList()
//        val originalSize = list.size
//
//        list.onAdd += { char, index ->
//            assertEquals(newItem, char)
//            assertEquals(addIndex, index)
//
//            callbackOccurred = true
//        }
//        list.add(addIndex, 'z')
//
//        assertEquals(originalSize+1, list.size)
//        assertTrue(callbackOccurred, {"callback occurred"})
//    }
//
//    @Test
//    fun removeAtSource(){
//        val removeIndex = 2
//
//        var callbackOccurred = false
//        val list = makeTestList()
//        val originalSize = list.size
//        val oldElement = list[removeIndex]
//
//        list.onRemove += { char, index ->
//            assertEquals(char, oldElement)
//            assertEquals(removeIndex, index)
//
//            callbackOccurred = true
//        }
//        list.removeAt(removeIndex)
//
//        assertEquals(originalSize-1, list.size)
//        assertTrue(callbackOccurred, {"callback occurred"})
//    }
//
//    @Test
//    fun moveSource(){
//        val sourceIndex = 2
//        val destIndex = 3
//
//        var callbackOccurred = false
//        val list = makeTestList()
//        val originalSize = list.size
//
//        list.onMove += { char, oldIndex, index ->
//            assertEquals(char, 'c')
//            assertEquals(sourceIndex, oldIndex)
//            assertEquals(destIndex, index)
//
//            callbackOccurred = true
//        }
//        list.move(sourceIndex,destIndex)
//
//        assertEquals(originalSize, list.size)
//        assertTrue(callbackOccurred, {"callback occurred"})
//    }

    @Test
    fun set() {
        val (list, filtering) = makeTestData()

        val changeIndex = 2
        val newItem = 22
        val oldItem = filtering[changeIndex]
        val expectedSize = filtering.size

        var callbackOccurred = false
        filtering.onListChange += { oldChar, char, index ->
            assertEquals(oldItem, oldChar)
            assertEquals(newItem, char)
            assertEquals(changeIndex, index)

            callbackOccurred = true
        }
        filtering[changeIndex] = newItem

        assertEquals(expectedSize, filtering.size)
        assertTrue(callbackOccurred, "callback occurred" )
    }

    @Test
    fun add() {
        val (list, filtering) = makeTestData()

        val newItem = 22
        val expectedSize = filtering.size

        var callbackOccurred = false
        filtering.onListAdd += { char, index ->
            assertEquals(newItem, char)
            assertEquals(expectedSize, index)

            callbackOccurred = true
        }
        filtering.add(newItem)

        assertEquals(expectedSize + 1, filtering.size)
        assertTrue(callbackOccurred, "callback occurred" )
    }

    @Test
    fun addAt() {
        val (list, filtering) = makeTestData()

        val addIndex = 2
        val newItem = 22
        val expectedSourceAddIndex = 4

        var callbackOccurred = false
        val originalSize = filtering.size

        list.onListAdd += { char, index ->
            assertEquals(newItem, char)
            assertEquals(expectedSourceAddIndex, index)

            callbackOccurred = true
        }
        filtering.onListAdd += { char, index ->
            assertEquals(newItem, char)
            assertEquals(addIndex, index)

            callbackOccurred = true
        }
        filtering.add(addIndex, newItem)

        assertEquals(originalSize + 1, filtering.size)
        assertTrue(callbackOccurred, "callback occurred" )
    }

    @Test
    fun removeAt() {
        val (list, filtering) = makeTestData()

        val removeIndex = 2
        val oldElement = filtering[removeIndex]
        val originalSize = filtering.size

        var callbackOccurred = false
        filtering.onListRemove += { char, index ->
            assertEquals(char, oldElement)
            assertEquals(removeIndex, index)

            callbackOccurred = true
        }
        filtering.removeAt(removeIndex)

        assertEquals(originalSize - 1, filtering.size)
        assertTrue(callbackOccurred, "callback occurred" )
    }

    @Test
    fun removeAtEnd() {
        val (list, filtering) = makeTestData()

        val removeIndex = filtering.lastIndex
        val oldElement = filtering[removeIndex]
        val originalSize = filtering.size

        var callbackOccurred = false
        filtering.onListRemove += { char, index ->
            assertEquals(char, oldElement)
            assertEquals(removeIndex, index)

            callbackOccurred = true
        }
        filtering.removeAt(removeIndex)

        assertEquals(originalSize - 1, filtering.size)
        assertTrue(callbackOccurred, "callback occurred" )
    }

    @Test
    fun move() {
        val (list, filtering) = makeTestData()

        val sourceIndex = 2
        val destIndex = 3

        var callbackOccurred = false
        val originalSize = filtering.size
        val oldElement = filtering[sourceIndex]

        filtering.onListMove += { char, oldIndex, index ->
            assertEquals(char, oldElement)
            assertEquals(sourceIndex, oldIndex)
            assertEquals(destIndex, index)

            callbackOccurred = true
        }
        filtering.move(sourceIndex, destIndex)

        assertEquals(originalSize, filtering.size)
        assertTrue(callbackOccurred, "callback didn't occur" )
    }

    @Test
    fun iterator() {
        val (list, filtering) = makeTestData()
        var index = 0
        val copy = filtering.toList()
        for (item in filtering) {
            assertEquals(copy[index], item)
            index++
        }
        assertEquals(filtering.size, index)
    }
}
