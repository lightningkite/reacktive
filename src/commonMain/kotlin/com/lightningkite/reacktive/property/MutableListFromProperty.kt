package com.lightningkite.reacktive.property



class MutableListFromProperty<E>(
        val property: MutableObservableProperty<List<E>>
) : MutableList<E> {
    fun move(fromIndex: Int, toIndex: Int) {
        property.value = property.value.toMutableList().also {
            it.add(toIndex, it.removeAt(fromIndex))
        }
    }

    override fun add(element: E): Boolean {
        property.value = property.value + element
        return true
    }

    override fun add(index: Int, element: E) {
        property.value = property.value.toMutableList().also {
            it.add(index, element)
        }
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        property.value = property.value.toMutableList().also {
            it.addAll(index, elements)
        }
        return true
    }

    override fun addAll(elements: Collection<E>): Boolean {
        property.value = property.value + elements
        return true
    }

    override fun clear() {
        property.value = listOf()
    }

    override fun remove(element: E): Boolean {
        var result = false
        property.value = property.value.toMutableList().also {
            result = it.remove(element)
        }
        return result
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        var result = false
        property.value = property.value.toMutableList().also {
            result = it.removeAll(elements)
        }
        return result
    }

    override fun removeAt(index: Int): E {
        var result: E
        property.value = property.value.toMutableList().also {
            result = it.removeAt(index)
        }
        return result
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        var result = false
        property.value = property.value.toMutableList().also {
            result = it.retainAll(elements)
        }
        return result
    }

    override fun set(index: Int, element: E): E {
        var result:E
        property.value = property.value.toMutableList().also {
            result = it.set(index, element)
        }
        return result
    }

    fun replace(collection: Collection<E>) {
        property.value = collection.toList()
    }

    override val size: Int
        get() = property.value.size

    override fun contains(element: E): Boolean = property.value.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = property.value.containsAll(elements)

    override fun get(index: Int): E = property.value.get(index)

    override fun indexOf(element: E): Int = property.value.indexOf(element)

    override fun isEmpty(): Boolean = property.value.isEmpty()

    override fun iterator(): MutableIterator<E> = listIterator()

    override fun lastIndexOf(element: E): Int = property.value.lastIndexOf(element)

    override fun listIterator(): MutableListIterator<E> = listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<E> = object : MutableListIterator<E> {
        var currentIndex = index - 1
        override fun hasNext(): Boolean = (currentIndex + 1) < property.value.size

        override fun next(): E = property.value[++currentIndex]

        override fun remove() {
            property.value = property.value.toMutableList().also { it.removeAt(currentIndex) }
            currentIndex--
        }

        override fun add(element: E) {
            property.value = property.value.toMutableList().also { it.add(currentIndex, element) }
            currentIndex++
        }

        override fun hasPrevious(): Boolean = currentIndex > 0

        override fun nextIndex(): Int = currentIndex

        override fun previous(): E = property.value[currentIndex--]

        override fun previousIndex(): Int = currentIndex - 1

        override fun set(element: E) {
            property.value = property.value.toMutableList().also { it[currentIndex] = element }
        }

    }

    class Partial<E>(
            val property: MutableObservableProperty<List<E>>,
            var startIndex: Int = 0,
            var endIndexExclusive: Int = 0
    ) : MutableList<E> {

        inline fun Int.localToGlobal() = this + startIndex
        inline fun Int.globalToLocal() = this - startIndex

        fun move(fromIndex: Int, toIndex: Int) {
            property.value = property.value.toMutableList().also {
                it.add(toIndex.localToGlobal(), it.removeAt(fromIndex.localToGlobal()))
            }
        }

        override fun add(element: E): Boolean {
            property.value = property.value.toMutableList().also {
                it.add(endIndexExclusive, element)
            }
            endIndexExclusive++
            return true
        }

        override fun add(index: Int, element: E) {
            property.value = property.value.toMutableList().also {
                it.add(index.localToGlobal(), element)
            }
            endIndexExclusive++
        }

        override fun addAll(index: Int, elements: Collection<E>): Boolean {
            property.value = property.value.toMutableList().also {
                it.addAll(index.localToGlobal(), elements)
            }
            endIndexExclusive += elements.size
            return true
        }

        override fun addAll(elements: Collection<E>): Boolean {
            property.value = property.value.toMutableList().also {
                it.addAll(endIndexExclusive, elements)
            }
            endIndexExclusive += elements.size
            return true
        }

        override fun clear() {
            property.value = listOf()
        }

        override fun remove(element: E): Boolean {
            var result = false
            property.value = property.value.toMutableList().also {
                result = it.remove(element)
            }
            if(result) endIndexExclusive--
            return result
        }

        override fun removeAll(elements: Collection<E>): Boolean {
            var result = false
            property.value = property.value.toMutableList().also {
                result = it.removeAll(elements)
            }
            return result
        }

        override fun removeAt(index: Int): E {
            var result: E
            property.value = property.value.toMutableList().also {
                result = it.removeAt(index.localToGlobal())
            }
            endIndexExclusive--
            return result
        }

        override fun retainAll(elements: Collection<E>): Boolean {
            var result = false
            property.value = property.value.toMutableList().also {
                result = it.retainAll(elements)
            }
            return result
        }

        override fun set(index: Int, element: E): E {
            var result: E
            property.value = property.value.toMutableList().also {
                result = it.set(index.localToGlobal(), element)
            }
            return result
        }

        fun replace(collection: Collection<E>) = UnsupportedOperationException()

        override val size: Int
            get() = endIndexExclusive - startIndex

        override fun contains(element: E): Boolean = any { it == element }

        override fun containsAll(elements: Collection<E>): Boolean = elements.all { element -> any { it == element } }

        override fun get(index: Int): E = property.value.get(index.localToGlobal())

        override fun indexOf(element: E): Int = property.value.indexOf(element).globalToLocal()

        override fun isEmpty(): Boolean = startIndex == endIndexExclusive

        override fun iterator(): MutableIterator<E> = listIterator()

        override fun lastIndexOf(element: E): Int = property.value.lastIndexOf(element).globalToLocal()

        override fun listIterator(): MutableListIterator<E> = listIterator(0)

        override fun listIterator(index: Int): MutableListIterator<E> = object : MutableListIterator<E> {
            var currentIndex = index.localToGlobal() - 1
            override fun hasNext(): Boolean = (currentIndex + 1) < endIndexExclusive

            override fun next(): E = property.value[++currentIndex]

            override fun remove() {
                property.value = property.value.toMutableList().also { it.removeAt(currentIndex) }
                currentIndex--
            }

            override fun add(element: E) {
                property.value = property.value.toMutableList().also { it.add(currentIndex, element) }
                currentIndex++
            }

            override fun hasPrevious(): Boolean = currentIndex >= startIndex

            override fun nextIndex(): Int = currentIndex

            override fun previous(): E = property.value[currentIndex--]

            override fun previousIndex(): Int = currentIndex - 1

            override fun set(element: E) {
                property.value = property.value.toMutableList().also { it[currentIndex] = element }
            }

        }
        override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = Partial(property, fromIndex.localToGlobal(), toIndex.localToGlobal())
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = Partial(property, fromIndex, toIndex)
}