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

        fun check(){
            assertEquals(list.lastOrNull(), obs.value)
            assertEquals(list.lastOrNull(), lastObserved)
        }

        println("Starting...")
        check()

        list.add('a')
        check()
        list.add('b')
        check()
        list.removeAt(list.lastIndex)
        check()
        list.add(0, 'c')
        check()
        list.replace(listOf('a', 'b', 'c'))
        check()
        list[2] = 'd'
        check()
    }
}