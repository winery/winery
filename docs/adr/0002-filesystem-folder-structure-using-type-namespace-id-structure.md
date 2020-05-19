# File system folder structure using type-namespace-id structure

Winery's data is stored in a a file system [ADR-0001](0001-use-filesystem-as-backend.md).
The contents of the repository should be

- human readable
- machine processable

## Considered Alternatives

* Folder structure using type-namespace-id
* Everything in one folder. Hash-based storing similar to git.

## Decision Outcome

*Chosen Alternative: Folders subdivided in type-namespace-id*

The final file system layout itself is documented at [RepositoryLayout](../dev/repository-layout.md).

### human readable

Everything in one directory causes many files listed and thus humans will have difficulties to find the right file.

The folders in the *top level* are the TOSCA "components", e.g., Node Type, Relationship Type, Service Template, ...

The *second structuring element* are namespaces.
[Namespaces are an established method to avoid naming conflicts](https://www.w3schools.com/xml/xml_namespaces.asp) and are a structuring element.
TOSCA is an open system and everyone can create Node Types.
One has no global control which names are given to Node Types.
Thus, there might be two different Node Types with the same name.
The namespaces provide a natural structuring and Winery reuses this idea.

The *third structuring element* are the ids of the respective definitions child (type, template, ...):
Each element has an id contained in the respective namespace.
This id can be directly used as folder name.

Within each folder, the component-specific information is stored.

### machine processable

Windows cannot create directories named `http://www.example.com`.
Therefore, the names have to be [encoded](https://en.wikipedia.org/wiki/Character_encoding) so that an appropriate folder can be generated.


## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

