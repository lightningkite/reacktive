package com.lightningkite.reacktive

fun <A> MutableCollection<(A)->Unit>.addAndInvoke(value: A, lambda:(A)->Unit){
    lambda.invoke(value)
    add(lambda)
}
fun <A, B> MutableCollection<(A, B)->Unit>.addAndInvoke(valueA: A, valueB: B, lambda:(A, B)->Unit){
    lambda.invoke(valueA, valueB)
    add(lambda)
}
fun <A, B, C> MutableCollection<(A, B, C)->Unit>.addAndInvoke(valueA: A, valueB: B, valueC: C, lambda:(A, B, C)->Unit){
    lambda.invoke(valueA, valueB, valueC)
    add(lambda)
}