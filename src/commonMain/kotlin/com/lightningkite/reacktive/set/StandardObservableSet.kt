package com.lightningkite.reacktive.set

fun <T> StandardObservableSet(): WrapperObservableSet<T> = WrapperObservableSet(HashSet())
fun <T> StandardObservableSet(existing: Collection<T>): WrapperObservableSet<T> =
    WrapperObservableSet(existing.toMutableSet())