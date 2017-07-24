# Eclipse Winery's Layout of Filebased Repository

<!-- toc -->

- [Typical layout](#typical-layout)
- [Directory `imports`](#directory-imports)
  * [Directory layout](#directory-layout)
  * [Handling of the extensibility parts](#handling-of-the-extensibility-parts)
  * [Special treatment of XSD definitions](#special-treatment-of-xsd-definitions)
    + [Knowing the definitions for a QName](#knowing-the-definitions-for-a-qname)
- [License](#license)

<!-- tocstop -->

The general structure is ROOT/<componenttype>s/<encoded-namespace>/<encoded-id>/<resource-specific-part>.
Encoding is done following RFC 3986. This makes the structure to the URL structure (cf. Section 7).

The resource-specific part typically is a file named <componenttype>.tosca . It contains the Definitions
XML file where all the data is stored. Files may be added to artifact templates. Therefore, a subdirectory "files"
is created in ROOT/artifacttemplates/<encoded-namespace>/<encoded-id>/. There, the files are stored.

For instance, the NodeType "NT1" in the namespace "http://www.example.com/NodeTypes" is found behind the URL
"nodetypes/http%3A%2F%2Fexample.com%2FNodeTypes/NT1/". As the browser decodes the URL, the namespace and the
id are double encoded. The content of the Definitions is stored in "NodeType.tosca".

The URL encoding is necessary as some letter allowed in namespaces (e.g. ".", ":", ";", "/") and IDs are not allowed
on all operating systems. IDs are NCNames, which are based on XML 1.0 Names, which in turn allows nearly all
unicode characters. Therefore, each namespace and ID is URLencoded when written to the filesystem and URLdecoded
when read from the filesystem.

Figure 5 shows the root directory of the filesystem and the directory layout for the NodeType NT1.

![Filesystem Directory Layout](graphics/FilesystemDirectoryLayout.png)
**Figure 5: Filesystem directory layout**

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

Copyright (c) 2013-2017 University of Stuttgart.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v1.0]
and the [Apache License v2.0] which both accompany this distribution,
and are available at http://www.eclipse.org/legal/epl-v10.html
and http://www.apache.org/licenses/LICENSE-2.0

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
