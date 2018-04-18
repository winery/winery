# Versions of TOSCA elements in the name

In order to enable the versioning of TOSCA elements, the version corresponding to one element
must be saved in a TOSCA compliant way. 

Forces:
- TOSCA compliant


## Considered Alternatives

* *Version in the name*
* *Version in the namespace*
* *Save Version externally*
* *[...]* <!-- numbers of alternatives can vary -->

## Decision Outcome

* Chosen Alternative: *versions in the name*
* Easiest and best fit regarding compliance

## Pros and Cons of the Alternatives <!-- optional -->

### *Version in the name*

* `+` *Consistent to the TOSCA specification*
* `+` *Even from outside of the winery, definitions can be detected in the specific version on first sight*
* `o` *Requires deep copy of all files and definitions on creating a new version*
* `-` *Introduces naming conventions to the naming of components: '_' are not allowed anymore*

### *Version in the namespace*

* `+` Easy and well established method in XML
* `+` Definition’s name/id stays intact
* `-` Implies that all elements in the corresponding namespace have the same version
* `-` Used to specify the version of the XML’s vocabulary only

### *Save Version externally*

* `+` Less disk space required
* `-` Version is not detectable in the XML


## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
