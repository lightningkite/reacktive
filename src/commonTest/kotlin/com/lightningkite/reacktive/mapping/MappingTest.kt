package com.lightningkite.reacktive.mapping

import kotlin.test.Test
import kotlin.test.assertTrue

class MappingTestMappingTest {

    @Test
    fun readOnlyList() {
        val source = listOf(1, 2, 3, 4)
        val mapper = { it: Int -> it * 2 }
        val virtualTransform = source.mapping(mapper, { it / 2 })
        run {
            val transformed = source.map(mapper)
            assertTrue { transformed contentEquals virtualTransform }
        }
    }

    @Test
    fun mutableList() {
        val source = arrayListOf(1, 2, 3, 4)
        val mapper = { it: Int -> it * 2 }
        val virtualTransform = source.mapping(mapper, { it / 2 })
        run {
            val transformed = source.map(mapper)
            assertTrue { transformed contentEquals virtualTransform }
        }
        source.add(33)
        run {
            val transformed = source.map(mapper)
            assertTrue { transformed contentEquals virtualTransform }
        }
    }

    @Test
    fun writeOnly() {
        val source = arrayListOf(1, 2, 3, 4)
        val writeOnly = source.mappingWriteOnly { it: String -> it.toIntOrNull() ?: 0 }
        writeOnly.add("42")
        assertTrue { source.contains(42) }
    }
}
