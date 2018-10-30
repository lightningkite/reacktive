package com.lightningkite.reacktive

inline fun once(crossinline lambda: (() -> Unit)): (() -> Unit) {
    var occurred = false
    return label@{
        if (occurred) return@label
        occurred = true
        lambda()
    }
}

inline fun <A> once(crossinline lambda: ((A) -> Unit)): ((A) -> Unit) {
    var occurred = false
    return label@{ a ->
        if (occurred) return@label
        occurred = true
        lambda(a)
    }
}

inline fun <A, B> once(crossinline lambda: ((A, B) -> Unit)): ((A, B) -> Unit) {
    var occurred = false
    return label@{ a, b ->
        if (occurred) return@label
        occurred = true
        lambda(a, b)
    }
}

inline fun <A, B, C> once(crossinline lambda: ((A, B, C) -> Unit)): ((A, B, C) -> Unit) {
    var occurred = false
    return label@{ a, b, c ->
        if (occurred) return@label
        occurred = true
        lambda(a, b, c)
    }
}
