## Generated Object API

Each of the examples provided thus far have focused on interaction with the Hollow data set via the generated Hollow Objects API.  

_Hollow Objects_ are instantiated at the time they are requested.  Each Hollow Object references a single record of a specific type, and holds two things:

* A reference to the Hollow data store for the type
* An [ordinal](diving-deeper.md#ordinals)

A Hollow Object contains methods to retrieve each of its type's fields in the data model from which the API was generated.  Each time a field retrieval method is called on a Hollow Object, the data is retrieved directly from the Hollow data store.

!!! note "Hollow Objects are 'hollow'"
    The name Hollow is derived from the fact that these objects are 'hollow' -- they _appear_ to contain field accessors, but those are just facades which access the underlying data store.

## Generated Type API

At times, using the Hollow Object API can result in a high rate of Object allocation.  All generated Hollow APIs also provide a way to interact with the data without creating Objects.  This is accomplished by using record [ordinals](diving-deeper.md#ordinals) to query the data store directly.  For example:
```java
MovieTypeAPI movieTypeAPI = movieAPI.getMovieTypeAPI();
ListOfActorTypeAPI listOfActorTypeAPI = movieAPI.getListOfActorTypeAPI();
ActorTypeAPI actorTypeAPI = movieAPI.getActorTypeAPI();
StringTypeAPI stringTypeAPI = movieAPI.getStringTypeAPI();

int movieOrdinal = movieIdx.getMatchingOrdinal(6);
int listOfActorsOrdinal = movieTypeAPI.getActors(movieOrdinal);

int numActors = listOfActorTypeAPI.size(listOfActorsOrdinal);

for(int i=0; i<numActors; i++) {
   int actorOrdinal = 
                 listOfActorTypeAPI.getElementOrdinal(listOfActorsOrdinal, i);
   int stringOrdinal = actorTypeAPI.getActorNameOrdinal(actorOrdinal);
   System.out.println("Starring " + stringAPI.getValue(stringOrdinal));
}
```

In extremely tight loops, it may be more efficient to use the Type API rather than the Object API.

!!! warning "Avoid Premature Optimization"
    In all but the tightest, most frequently executed loops, usage of the Type API will be unnecessary.  Its usage should be applied judiciously, since the pattern can be more difficult to maintain.

## Generic Object API

Hollow also includes a generic Hollow Object API which, if sufficient for consumers, obviates the need to provide generated code:
```java
int movieOrdinal = movieIdx.getMatchingOrdinal(1);

GenericHollowObject movie = new GenericHollowObject(readEngine, "Movie", movieOrdinal);

String title = movie.getObject("title").getString("value");

for(GenericHollowObject actor : movie.getList("actors").objects()) {
    String actorName = actor.getObject("actorName").getString("value");
    System.out.println("Starring " + actorName);
}
```

Working with the Generic Object API can become cumbersome — unlike a generated Hollow API, the IDE type assist cannot provide a guide to the data model.  However, for simple data models and explorational tasks the Generic Object API can be useful.
