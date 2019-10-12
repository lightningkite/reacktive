package com.lightningkite.reacktive.property.lifecycle

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.Lifecycle
import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.combine

infix fun Lifecycle.and(other: Lifecycle): Lifecycle = combine(this, other) { a, b -> a && b }
infix fun Lifecycle.or(other: Lifecycle): Lifecycle = combine(this, other) { a, b -> a || b }