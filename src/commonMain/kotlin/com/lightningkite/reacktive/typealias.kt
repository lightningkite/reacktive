package com.lightningkite.reacktive

import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.property.ObservableProperty

@Deprecated("Use the one from the event package instead", ReplaceWith("Event<A>", "com.lightningkite.reacktive.event.Event"))
typealias Event<A> = Event<A>
typealias Lifecycle = ObservableProperty<Boolean>