package com.lightningkite.reacktive.list

import kotlin.random.Random
import kotlin.test.assertTrue

class TestData<E>(
        val label: String,
        val random: Random,
        val itemGenerator: () -> E,
        val manualTransformer: (MutableObservableList<E>) -> List<E>,
        val transformer: (MutableObservableList<E>) -> ObservableList<E>
) {
    val source: MutableObservableList<E> = WrapperObservableList((1..20).map { itemGenerator() }.toMutableList())
    val transformed = source.let(transformer)
    val replicant = ReplicatingList(transformed)
    fun manualTransformed() = source.let(manualTransformer)

    fun testOp(op: Operation<E>) {
        val preManual = manualTransformer.invoke(source)
        val preSource = source.toList()
        try {
            op.apply(this.source)
            this.transformed.asSequence().count()
            val manual = this.manualTransformed()
            assertTrue(transformed.size == manual.size)
            assertTrue(transformed deepEquals manual)
            replicant.check()
        } catch (t: Throwable) {
            println("!!! Failed for $label!!!")
            println("Last operation: $op")
            println("Previous:    " + preManual.joinToString(transform = { it.toString() }))
            println("-Changed to- ")
            println("Manual:      " + manualTransformed().joinToString { it.toString() })
            println("Transformed: " + transformed.joinToString { it.toString() })
            println("Replication: " + replicant.joinToString { it.toString() })
            println("-Sources- ")
            println("Previous:    " + preSource.joinToString { it.toString() })
            println("Now:         " + source.joinToString { it.toString() })
            throw t
        }
    }

    sealed class Operation<E> {

        abstract fun apply(mutableObservableList: MutableObservableList<E>)

        companion object {

            fun <E> opList(testData: TestData<E>): List<Operation<E>> = listOf(
                    Insertion(testData.source.indices.random(testData.random), testData.itemGenerator()),
                    Set(testData.source.indices.random(testData.random), testData.itemGenerator()),
                    Move(testData.source.indices.random(testData.random), testData.source.indices.random(testData.random)),
                    Removal(testData.source.indices.random(testData.random)),
                    MultiInsertion(testData.source.indices.random(testData.random), testData.itemGenerator(), testData.itemGenerator()),
                    RemoveEveryOther()
            )

            fun <E> generate(testData: TestData<E>): Operation<E> {
                if (testData.source.size < 5) {
                    return Insertion(testData.source.indices.random(testData.random), testData.itemGenerator())
                }
                if (testData.source.size > 15) {
                    return RemoveEveryOther()
                }
                return listOf(
                        { Insertion(testData.source.indices.random(testData.random), testData.itemGenerator()) },
                        { Set(testData.source.indices.random(testData.random), testData.itemGenerator()) },
                        { Move(testData.source.indices.random(testData.random), testData.source.indices.random(testData.random)) },
                        { Removal(testData.source.indices.random(testData.random)) },
                        { MultiInsertion(testData.source.indices.random(testData.random), testData.itemGenerator(), testData.itemGenerator()) },
                        { RemoveEveryOther<E>() }
                ).random(testData.random).invoke()
            }
        }

        data class Insertion<E>(
                val position: Int,
                val item: E
        ) : Operation<E>() {
            override fun apply(mutableObservableList: MutableObservableList<E>) {
                mutableObservableList.add(position, item)
            }
        }

        data class Set<E>(
                val position: Int,
                val newItem: E
        ) : Operation<E>() {
            override fun apply(mutableObservableList: MutableObservableList<E>) {
                mutableObservableList.set(position, newItem)
            }
        }

        data class Move<E>(
                val position: Int,
                val otherPosition: Int
        ) : Operation<E>() {
            override fun apply(mutableObservableList: MutableObservableList<E>) {
                mutableObservableList.move(position, otherPosition)
            }
        }

        data class Removal<E>(
                val position: Int
        ) : Operation<E>() {
            override fun apply(mutableObservableList: MutableObservableList<E>) {
                mutableObservableList.removeAt(position)
            }
        }

        data class MultiInsertion<E>(
                val position: Int,
                val item1: E,
                val item2: E
        ) : Operation<E>() {
            override fun apply(mutableObservableList: MutableObservableList<E>) {
                mutableObservableList.addAll(position, listOf(item1, item2))
            }
        }

        class RemoveEveryOther<E>() : Operation<E>() {
            override fun apply(mutableObservableList: MutableObservableList<E>) {
                var on = false
                mutableObservableList.removeAll {
                    on = !on
                    on
                }
            }

            override fun toString(): String = "RemoveEveryOther()"
        }
    }
}

infix fun <T> List<T>.deepEquals(other: List<T>): Boolean {
    if (size != other.size) return false
    for (index in indices) {
        if (this[index] != other[index]) return false
    }
    return true
}


fun Random.nextLowercaseLetter(): Char = ('a'..'z').random(this)
