# <Short Title of the Issue>

URLs should be human-readable, slashes are not allowed.
Encoded slahes are not enabled as default due to security reasons.

More information about encoding is available at [dev/Encoding.md](dev/Encoding).

## Considered Alternatives

* Using namespace prefixes in the URLs
* Single-encoded URLs and forcing the environment to be reconfigered
* Double-encoded URLs

## Conclusion

* *Chosen Alternative: Double-encoded URLs*

## Comparison (Optional)

### Using namespace prefixes in the URLs

* `+` No encoding issues
* `-` Not globally unique: The URLs will change if the user reconfigures the namespace prefix

### Single-encoded URLs

* `+` Nice URLs
* `-` All hosting environments have to be configured accordingly. This can lead to security issues when running other applications in parallel.

### Double-encoded URLs

* `+` Nearly-nice URLs
* `+` Hosting-environments do not have to be reconfigured
* `-` Double-encoding might cause headaches during the implementation

## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. Made available under the terms of the [Eclipse Public License v1.0] and the [Apache License v2.0] which both accompany this distribution.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
