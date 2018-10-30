package com.lightningkite.reacktive.collection

fun <T> StandardObservableCollection():WrapperObservableCollection<T> = WrapperObservableCollection(ArrayList())
fun <T> StandardObservableCollection(existing: Collection<T>):WrapperObservableCollection<T> = WrapperObservableCollection(existing.toMutableList())