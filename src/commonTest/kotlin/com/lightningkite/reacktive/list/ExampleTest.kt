package com.lightningkite.reacktive.list

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExampleTest {

    @Test
    fun lists() {
        //Let's make a list!
        val list = StandardObservableList<Int>()

        //Empty is boring.  Let's add some stuff - it's just a normal list after all.
        list.add(1)
        list.add(2)
        list.add(3)

        //Now I want to watch for when elements are added to the list.
        var lastSeenAdded: Int = 0
        val addListener = { it: Int ->
            println("Somebody added an $it")
            lastSeenAdded = it
        }
        list.onCollectionAdd += addListener

        list.add(4) //Somebody's gonna see this now
        assertEquals(4, lastSeenAdded) //Ah-hah!  Our listener picked it up!

        //There are actually a bunch of listeners.  They all start with the word `on`.  Go take a look!
    }
}