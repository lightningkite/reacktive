package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.collection.sorting
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.random.Random


/**
 * Created by joseph on 9/26/16.
 */
class SortingObservableListTest {

    fun data(): TestData<Char> {
        val myRandom = Random(32342)
        return TestData<Char>(
                label = "Sorting",
                random = myRandom,
                itemGenerator = { myRandom.nextLowercaseLetter() },
                transformer = { it.sorting(compareBy { it }) },
                manualTransformer = { it.sortedWith(compareBy { it }) }
        )
    }

    @Test
    fun isSorted(){
        val data = data()
        assertTrue { data.transformed deepEquals data.manualTransformed() }
    }

    @Test
    fun testOps(){
        val data = data()
        TestData.Operation.opList(data).forEach {
            println(it)
            data.testOp(it)
        }
    }
}
