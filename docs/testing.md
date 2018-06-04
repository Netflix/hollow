# Unit testing with Hollow objects

Hollow provides a `hollow-test` jar that contains some testing utilities to facilitate unit testing with Hollow objects.

### HollowReadStateEngineBuilder

This class allows easy creation of `HollowReadStateEngine` objects for use in unit tests.

```java
HollowReadStateEngine engine = new HollowReadStateEngineBuilder()
    .add(new Movie("foo"))
    .add(new Actor("bar"))
    .build()
```
