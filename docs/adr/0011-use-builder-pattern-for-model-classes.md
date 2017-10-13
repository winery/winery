# Use builder pattern for model classes

Model classes should be instantiable simple without using large constructors.

## Considered Alternatives

* [Builders]
* Setters, getters and default constructor 
* Large constructors
* Factories

## Decision Outcome

* Chosen Alternative: *Builders*
* Flexible
* Simple for complex objects
* Extensions cause problems (solved with generic builders) 

### Generic Builders

Generic Builders are used to enable safe method chaining for Builders with extend other Builders.
Another discussion is made at [stackoverflow].

The method `self()` is necessary because all setter methods should return the Builder used for instantiation and not the builder that is extended. `self()` can not be replace by `this` because the expected type is `<T>` and casting to `<T>` results in warnings.

Builders which are not abstract and are extended by other builders are generic and implement the `self()` method by casting `this` to `<T>`. To reduce warnings this casting is only used in this case.

Example:
```java
// part of ExtensibleElements.Builder
public abstract static class Builder<T extends Builder<T>> {
    private List<TDocumentation> documentation;
    
    // setter returns generic <T> 
    public T setDocumentation(List<TDocumentation> documentation) {
        this.documentation = documentation;
        // return this; => IncompatibleType exception either cast with warnings or use self() method
        return self();
    }
    
    // overwritten method
    public abstract T self();
}

// part of TEntityType.Builder
public abstract static class Builder<T extends Builder<T>> extends TExtensibleElements.Builder<T> {
	
}

// part of TNodeType.Builder
public static class Builder extends TEntityType.Builder<Builder> {
    @Override
    public Builder self() {
        return this;
    }
}
```



[Builders]:(https://en.wikipedia.org/wiki/Builder_pattern)
[stackoverflow]: https://stackoverflow.com/a/5818701/8235252
