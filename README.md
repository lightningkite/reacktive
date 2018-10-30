# ReacKtive

By [Lightning Kite](https://lightningkite.com)

A cross-platform library for reactive programming.

Designed around using Kotlin's built-in types as much as possible to maximize compatibility with other projects.

## Features

- `Event<T>`, which is actually just a type alias for `MutableCollection<(T)->Unit>`
- `invokeAll()`, which can be used to run all of the lambdas in a collection conveniently
- `ObservableProperty<T>`, which is an event that has a value that is retrievable at all times by getting the `value`.
- `ObservableCollection<T>`, which is an interface for managing a collection whose changes are observed.
- `ObservableList<T>`
- `ObservableSet<T>`
- `ObservableMap<T>`
- Tons of functions for transforming observables
    - `transform`, which maps it to another type
    - `combine`, which combines multiple properties together
    - `sorting`, which creates an actively-updating view of an observable collection
    - `mapping`, which creates an actively-updating view of an observable collection
    - `groupingBy`, which creates an actively-updating view of an observable collection
    - `flatMapping`, which creates an actively-updating view of an observable collection
    - `subObs`, which takes an observable property and shows an observable descendant of it
    
## Concepts

Instead of making some kind of special interface for an event that we wish to observe, we just use a mutable collection of lambdas.

To listen to an event, we simply add our callback to it.

To stop listening an event, we simply remove our callback from it.

What if we want to observe changes in a particular value?  Well, we've created an interface that does just that for you.  It's still a mutable collection of lambdas, but it also includes a spot to hold a value.  When updated, it will call all of the listeners for you.  The one you probably want to use is called `StandardObservableProperty<T>`.

Sometimes we also want to observe changes in collections.  We've got that handled too.  The interface is called `ObservableList<T>` and the implementation you'll probably want to use is `WrapperObservableList<T>`.  There's a mutable variant of the interface as well.

Also, handling when you want to listen can be painful and leak-prone, so we've made a solution.  Just use `Lifecycle`, which is a type alias for `ObservableProperty<Boolean>`, and you can use functions like `Lifecycle.listen(anEvent){}` to listen to the events only during the lifecycle, where the observable is on!  Now you just have to implement a lifecycle for your particular platform.


## Tutorial

Use these tests (well commented, meant for learning) to get some ideas on how it works.

- [Events](src/commonTest/kotlin/com/lightningkite/reacktive/EventsExampleTest.kt)
- [Observable Properties](src/commonTest/kotlin/com/lightningkite/reacktive/property/ExampleTest.kt)
- [Lifecycles](src/commonTest/kotlin/com/lightningkite/reacktive/property/bind/ExampleTest.kt)
- [Observable Lists](src/commonTest/kotlin/com/lightningkite/reacktive/list/ExampleTest.kt)