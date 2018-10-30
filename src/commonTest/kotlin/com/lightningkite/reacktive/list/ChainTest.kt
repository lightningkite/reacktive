package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.collection.sorting
import com.lightningkite.reacktive.collection.sortingDescending
import com.lightningkite.reacktive.map.groupingBy
import kotlin.random.Random
import kotlin.test.Test

/**
 * Created by joseph on 9/26/16.
 */
class ChainTest {
    val newElement: Char = 'q'

    fun makeRandom() = Random(34124)

    fun makeTestData(
            label: String,
            transforms: MutableObservableList<Char>.() -> ObservableList<Char>,
            transformer: List<Char>.() -> List<Char>
    ): TestData<Char> {
        val myRandom =makeRandom()
        return TestData(
                random = myRandom,
                itemGenerator = { myRandom.nextLowercaseLetter() },
                label = label,
                manualTransformer = transformer,
                transformer = transforms
        )
    }

    fun makeTestDatas(): List<TestData<Char>> {
        return listOf(
                makeTestData("control", transforms = { this }, transformer = { this }),
                makeTestData("mapping", transforms = { mapping { it + 2 }.mapping { it - 1 } }, transformer = { map { it + 2 }.map { it - 1 } }),
                makeTestData("filtering", transforms = { filtering { it.toInt() % 2 == 0 } }, transformer = { filter { it.toInt() % 2 == 0 } }),
                makeTestData("sorting", transforms = { sortingDescending()}, transformer = { this.sortedDescending() }),
                makeTestData(
                        "sorting->filtering",
                        transforms = { sortingDescending().filtering { it.toInt() % 2 == 0 } },
                        transformer = { sortedDescending().filter { it.toInt() % 2 == 0 } }
                ),
                makeTestData(
                        "filtering->sorting",
                        transforms = { filtering { it.toInt() % 2 == 0 }.sortingDescending() },
                        transformer = { filter { it.toInt() % 2 == 0 } .sortedDescending() }
                ),
                makeTestData(
                        "groupingBy->sortingX2->flatMapping",
                        transforms = { groupingBy { it.toInt() % 4 }.entries.sorting(compareBy { it.key }).flatMapping { it.value.sortingDescending() } },
                        transformer = { groupBy { it.toInt() % 4 }.entries.sortedBy { it.key }.flatMap { it.value.sortedDescending() } }
                )
        )
    }

    @Test
    fun theRandomScrambler() {
        makeTestDatas().forEachIndexed { index, data ->
            println("Test Data #$index: ${data.label}")
            data.transformed.onListAdd += { _, _ -> }
            TestData.Operation.opList(data).forEach {
                println("\t" + it)
                data.testOp(it)
            }
            repeat(10000) {
                try {
                    data.testOp(TestData.Operation.generate(data))
                } catch(t: Throwable){
                    println("Operation number $it")
                    throw t
                }
            }
        }
    }
}
