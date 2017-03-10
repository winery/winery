# Layout of the filebased repository

## Typical layout

Typically, all TOSCA components have the path `componenttype/ns/id`.

## Directory `imports`

This directory stores files belonging to a CSAR.
That means, when a definitions points to an external reference, the file has to be stored at the external location and not inside the repository

### Directory layout

`imports/<encoded importtype>/<encoded namespace>/<id>/`

In case no namespace is specified, then `__NONE__` is used as namespace.
Handling of that is currently not supported.

`id` is a randomly generated id reflecting a single imported file.

Inside the directory, a `.tosca` is stored containing the import element only.
In future, this can be used to contain the extensibility attributes, which are currently unsupported.

`location` points to
i) the local file or
ii) to some external definition (absolute URL!)

Currently, ii is not implemented and the storage is used as mirror only to be able to
a) offer choice of known XML Schema definitions
b) generate a UI for known XML Schemas (current idea: use http://sourceforge.net/projects/xsd2gui/)

Typically, all TOSCA components have the path `componenttype/ns/id`.
We add `imports` before to group the imports.
The chosen ordering allows to present all available imports for a given import type
by just querying the contents of `<encoded importtype>`.

### Handling of the extensibility parts

Handling of the extensible part of `tImport` is not supported.
A first idea is to store the Import XML Element as file in the respective directory.

### Special treatment of XSD definitions

#### Knowing the definitions for a QName

Currently, all contained XSDs are queried for their defined local names and this set is aggregated.

The following is an implementation idea:

Each namespace may contain multiple definitions.
Therefore, each folder `<enocded namespace>` contains a file `import.properties`,
which provides a mapping of local names to id.
For instance, if `theElement`is defined in `myxmldefs.xsd` (being the human-readable id of the folder),
`index.properties` contains `theElement = myxmldefs.xsd`.
The local name is sufficient as the namespace is given by the parent directory.

## License
Copyright (c) 2013-2014 University of Stuttgart.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v1.0]
and the [Apache License v2.0] which both accompany this distribution,
and are available at http://www.eclipse.org/legal/epl-v10.html
and http://www.apache.org/licenses/LICENSE-2.0

Contributors:
* Oliver Kopp - initial API and implementation


 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
