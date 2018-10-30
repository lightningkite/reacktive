package com.lightningkite.reacktive

import kotlin.test.Test
import kotlin.test.assertTrue

class EventsExampleTest {

    @Test
    fun example(){
        //Kinda funny, in this test, we're only going to use a single function from this library.

        //Let's say we want to be notified when a race is supposed to start.
        val raceStartEvent: Event0 = ArrayList()

        //Let's break that down.
        //Here, we create an event that gives no particular information to the listeners.

        //The number in `Event#` means how many pieces of data are being passed to the listeners.

        //The `Event0` is actually just a type alias for `MutableCollection<()->Unit>`.  We need to pick a specific
        //implementation of collection to use.  `ArrayList` is what I selected here.


        //Let's get listening.
        var racersStarted = false
        raceStartEvent.add {
            println("The listener has been called - gun's gone off!  Get those racers running!")
            racersStarted = true
        }

        //Now, let's start a race!
        raceStartEvent.invokeAll()
        //`invokeAll` just calls all of the lambdas in there.  That's the only function in my library.

        //Let's confirm that the whole thing worked.  The racers should be running now.
        assertTrue { racersStarted }
    }
}