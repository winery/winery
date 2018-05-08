# Versions of TOSCA elements in the name

In order to enable the versioning of TOSCA elements, the version corresponding to one element
must be saved in a TOSCA compliant way. 

Forces:
- TOSCA compliant
- The version identifier must be detectable in the XML file


## Considered Options

* Version in the name
* Version in the namespace
* Save version externally

## Decision Outcome

* Chosen Option: version in the name/id because it is compliant to the TOSCA specification and shows the version 
  directly in the XML file.
* Easiest and best fit regarding compliance

## Pros and Cons of the Options

### Version in the name

* Good, because it is consistent to the TOSCA specification
* Good, because even from outside of the winery, definitions can be detected in the specific version on first sight
* Good and bad, because it requires a deep copy of all files and definitions on creating a new version*
* Bad, because Introduces naming conventions to the naming of components: '_' are not allowed anymore*

### Version in the namespace

* Good, because it is easy and well established method in XML
* Good, because the definition’s name/id stays intact
* Bad, because it implies that all elements in the corresponding namespace have the same version
* Bad, because it is usually used to specify the version of the XML’s vocabulary only

### Save version externally

* Good, because it requires less disk space than
* Bad, because the version is not detectable in the XML


## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
