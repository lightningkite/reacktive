package com.lightningkite.reacktive.event

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda

@Deprecated("Use `map` instead", ReplaceWith("this.map(transform)", "com.lightningkite.reacktive.event.map"))
fun <A, B> Event<A>.transform(transform:(A)->B): Event<B> = map(transform)
fun <A, B> Event<A>.map(transform:(A)->B): Event<B> = object : Event<B> {
    override fun listen(listener: (B) -> Unit): Closeable = this@map.listen { listener(transform(it)) }
}
fun <A, B> Event<A>.mapCached(transform:(A)->B): Event<B> = object : Event<B> {
    var cachedFrom: A? = null
    var cached: B? = null
    fun transformIfNeeded(value: A): B {
        @Suppress("UNCHECKED_CAST")
        if(cachedFrom == value) return cached as B
        val transformed = transform(value)
        cachedFrom = value
        cached = transformed
        return transformed
    }
    override fun listen(listener: (B) -> Unit): Closeable = this@mapCached.listen { listener(transformIfNeeded(it)) }
}
fun <A, B: Any> Event<A>.mapNotNull(transform:(A)->B?): Event<B> = object : Event<B> {
    override fun listen(listener: (B) -> Unit): Closeable = this@mapNotNull.listen { transform(it)?.let(listener) }
}
fun <A> Event<A>.filter(allow:(A)->Boolean): Event<A> = object : Event<A> {
    override fun listen(listener: (A) -> Unit): Closeable = this@filter.listen { if(allow(it)) listener(it) }
}
fun <A: Any> Event<A?>.filterNotNull(): Event<A> = object : Event<A> {
    override fun listen(listener: (A) -> Unit): Closeable = this@filterNotNull.listen { it?.let(listener) }
}
fun <A, B> Event<A>.multiMap(transform:(A)->Iterable<B>): Event<B> = object : Event<B> {
    override fun listen(listener: (B) -> Unit): Closeable = this@multiMap.listen {
        transform(it).forEach(listener)
    }
}
fun <A> Event<A>.withPrevious(initial: A): Event<Pair<A, A>> = object : Event<Pair<A, A>> {
    var previous = initial
    var current = initial
    override fun listen(listener: (Pair<A, A>) -> Unit): Closeable = this@withPrevious.listen {
        if(it == current) previous to current
        else {
            previous = current
            current = it
            previous to it
        }
    }
}

fun <A, B, Z> combine(
        a: Event<A>,
        fromA: (A)->Z,
        b: Event<B>,
        fromB: (B)->Z
): Event<Z> {
    return object: Event<Z> {
        override fun listen(listener: (Z) -> Unit): Closeable {
            val closeA = a.listen { listener(fromA(it)) }
            val closeB = b.listen { listener(fromB(it)) }
            return CloseableLambda {
                closeA.close()
                closeB.close()
            }
        }
    }
}

fun <A, B, C, Z> combine(
        a: Event<A>,
        fromA: (A)->Z,
        b: Event<B>,
        fromB: (B)->Z,
        c: Event<C>,
        fromC: (C)->Z
): Event<Z> {
    return object: Event<Z> {
        override fun listen(listener: (Z) -> Unit): Closeable {
            val closeA = a.listen { listener(fromA(it)) }
            val closeB = b.listen { listener(fromB(it)) }
            val closeC = c.listen { listener(fromC(it)) }
            return CloseableLambda {
                closeA.close()
                closeB.close()
                closeC.close()
            }
        }
    }
}

fun combineUntyped(vararg events: Event<*>): Event<Unit> {
    return object: Event<Unit> {
        override fun listen(listener: (Unit) -> Unit): Closeable {
            val closers = events.map { it.listen { listener(Unit)} }
            return CloseableLambda {
                closers.forEach {it.close()}
            }
        }
    }
}

fun <T> combine(vararg events: Event<T>): Event<T> {
    return object: Event<T> {
        override fun listen(listener: (T) -> Unit): Closeable {
            val closers = events.map { it.listen(listener) }
            return CloseableLambda {
                closers.forEach {it.close()}
            }
        }
    }
}