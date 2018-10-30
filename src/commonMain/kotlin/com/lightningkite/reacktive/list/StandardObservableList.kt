package com.lightningkite.reacktive.list

fun <T> StandardObservableList(): WrapperObservableList<T> = WrapperObservableList(ArrayList())
fun <T> StandardObservableList(existing: Collection<T>): WrapperObservableList<T> =
    WrapperObservableList(existing.toMutableList())