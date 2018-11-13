package com.lightningkite.reacktive.list

import kotlin.test.Test
import kotlin.test.assertEquals

class LastObservablePropertyTest {
    @Test fun test(){
        val list = WrapperObservableList<Char>()
        val obs = list.lastOrNullObservable()
        var lastObserved = obs.value
        obs.add {
            println("Update: $it")
            lastObserved = it
        }

        var lastChecksEnd:Char? = null
        fun check(){
            assertEquals(list.lastOrNull(), obs.value)

            if(lastChecksEnd == list.lastOrNull()) return
            assertEquals(list.lastOrNull(), lastObserved)
            lastChecksEnd = list.lastOrNull()
        }

        println("Starting...")
        check()

        lastObserved = null
        list.add('a')
        check()

        lastObserved = null
        list.add('b')
        check()

        lastObserved = null
        list.add('c')
        check()

        lastObserved = null
        list.removeAt(list.lastIndex)
        check()

        lastObserved = null
        list.add(0, 'c')
        check()

        lastObserved = null
        list.replace(listOf('a', 'b', 'c'))
        check()

        lastObserved = null
        list[2] = 'd'
        check()

        lastObserved = null
        list.removeAt(0)
        check()

    }
}