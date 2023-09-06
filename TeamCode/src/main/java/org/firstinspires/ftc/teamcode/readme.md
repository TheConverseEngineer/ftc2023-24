## Conventions
Here are some simple (but very important!) Java conventions to help keep code straightforward.

## Javadocs and Comments
Make sure to write a javadoc for every class and method (including private methods). Whenever possible,
link referenced classes/methods/parameters, like this:
```
/** This method takes an {@link Action} and runs the method {@link Action#bar} */
public void foo(Action a) {
    action.bar();
}
```

For a class, always include an @version tag. Incomplete classes must use a version 0.x, and must lists which methods
are/are not complete

Consider including a single-line comment after variable declarations, as follows:
```
public int numberOfSensors = 0; // This variable counts how many sensors have been registered so far
```
Longer descriptions can use a multi-line (non-Javadoc) comment instead

## Misc. Java Conventions
Use private properties whenever possible (along with getters/setters if needed)

Whenever possible, mark properties as final.