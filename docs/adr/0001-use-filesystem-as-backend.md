# Use filesystem as backend

Winery needs to store its contents.
These contents need to be shared.

## Considered Alternatives

* Filesystem
* Database

## Conclusion

* *Chosen Alternative: Filesystem*

## Comparison

### Filesystem

* `+` Easy to manually change values
* `+` No need to educate students on a certain database system
* `+` Allows to use git as distributed version control system
* `-` Consistency check is hard to implement
* `-` Not transaction safe (concurrency)

### Database

* `+` Transaction safety
* `+` Scalability
* `-` Not (directly) possible to use git as distributed version control system
* `-` Higher skills required

## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. Made available under the terms of the [Eclipse Public License v1.0] and the [Apache License v2.0] which both accompany this distribution.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
