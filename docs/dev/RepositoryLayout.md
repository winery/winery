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

Related architectural decision records are
[ADR-0001](../adr/0001-use-filesystem-as-backend)
[ADR-0002](../adr/0002-filesystem-folder-structure-using-type-namespace-id-structure)
.

The general structure is `ROOT/<componenttype>s/<encoded-namespace>/<encoded-id>/<resource-specific-part>`.
Encoding is explained below.

Figure 1 shows the root directory of the filesystem and the directory layout for the NodeType NT1.

![Filesystem Directory Layout](graphics/FilesystemDirectoryLayout.png)
**Figure 1: Filesystem directory layout**

## Encoding

Encoding of directory and file names is done following [RFC 3986](https://tools.ietf.org/html/rfc3986#section-2.1).
This makes the structure consistent to the URL structure (cf. [URL structure in the developer guide](./#url-structure)).

The URL encoding is necessary as some letter allowed in namespaces (e.g. `.`, `:`, `;`, `/`) and IDs are not allowed on all operating systems.
IDs are [NCNames](https://www.w3.org/TR/xmlschema-2/#NCName), which are based on [XML 1.0 Names](https://www.w3.org/TR/2000/WD-xml-2e-20000814#NT-Name), which in turn allows [nearly all unicode characters](https://www.w3.org/TR/2000/WD-xml-2e-20000814#NT-Letter).
Therefore, each namespace and ID is URL encoded when written to the filesystem and URL decoded when read from the filesystem.

More information on encoding is given at [Encoding](Encoding).

## Typical layout

Typically, all TOSCA components have the path `componenttype/ns/id`.

The `component type` is nodetypes, relationshiptypes, servicetemplates, ...
`ns` is the namespace.
It is stored encoded (see above).
`id` is the XML id of the component.
It is stored encoded (see above).

For instance, the NodeType "NT1" in the namespace "http://www.example.com/NodeTypes" is found in the directory
"nodetypes/http%3A%2F%2Fexample.com%2FNodeTypes/NT1/".
The content of the Definitions is stored in `NodeType.tosca`.

The resource-specific part typically is a file named <componenttype>.tosca .
It contains the Definitions XML file where all the data is stored.
Files may be added to artifact templates.
Therefore, a subdirectory "files" is created in ROOT/artifacttemplates/<encoded-namespace>/<encoded-id>/.
There, the files are stored.

## Directory `imports`

This directory stores files belonging to a CSAR.
That means, when a definitions points to an external reference, the file has to be stored at the external location and not inside the repository

### Directory layout of the imports directory

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
Therefore, each folder `<enocoded namespace>` contains a file `import.properties`,
which provides a mapping of local names to id.
For instance, if `theElement`is defined in `myxmldefs.xsd` (being the human-readable id of the folder),
`index.properties` contains `theElement = myxmldefs.xsd`.
The local name is sufficient as the namespace is given by the parent directory.

## License

Copyright (c) 2013-2017 University of Stuttgart.

All rights reserved. Made available under the terms of the [Eclipse Public License v1.0] and the [Apache License v2.0] which both accompany this distribution.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
