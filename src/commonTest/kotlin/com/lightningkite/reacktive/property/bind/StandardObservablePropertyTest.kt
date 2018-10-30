package com.lightningkite.reacktive.property.bind

import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import kotlin.test.Test
import kotlin.test.assertEquals

class StandardObservablePropertyTest {

    @Test
    fun propertyExample() {

        val lifecycle = StandardObservableProperty(false)
        val property = StandardObservableProperty(4)

        //Let's add a listener to the property
        var updated = false
        lifecycle.bind(property) { it ->
            updated = true
        }

        //recentUpdate will not be notified.
        updated = false
        property.value += 3
        assertEquals(false, updated)


        lifecycle.value = true
        updated = false
        property.value += 3
        assertEquals(true, updated)
    }

    @Test
    fun startOn() {

        val lifecycle = StandardObservableProperty(true)
        val property = StandardObservableProperty(4)

        //Let's add a listener to the property
        var updated = false
        lifecycle.bind(property) { it ->
            updated = true
        }

        updated = false
        property.value += 3
        assertEquals(true, updated)
    }
}