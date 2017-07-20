# Encoding of folder names, namespaces, and ids in Winery

## Example to understand double encoding of URIs in the winery

In file systems, characteers `/` and `:` are not allowed [source](https://stackoverflow.com/a/31976060/873282).
The [design decision](../adr/0002-filesystem-folder-structure-using-type-namespace-id-structure) is that all files (node types, imports in CSARs, ...) are stored and structured using directories with human readable names.
Therefore, the namespace URI must be used in a encoded form, otherwise the name would not be valid (`http://www...`). 
An example for such a directory name is `http%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema`.
Since (1) namespaces are URIs and (2) [percent-encoded](https://tools.ietf.org/html/rfc3986#section-2.1) URIs form valid directory names, URL encoding is used.

For a better understanding we distinguish two cases: 

(1) calling a URL and 
(2) linking to the location of an tosca import in a tosca XML file contained in the CSAR.

### First case: Calling a URL

When using the Winery UI to show a Node Type the following URL is used by the browser: http://localhost:8080/winery/nodetypes/http%253A%252F%252Fopentosca.org%252Fnodetypes/Java8/
The convention for URLs in the winery are: `winery/type/namespace/id` (see [DevGuide](./#url-structure)).

 - winery:    `http://localhost:8080/winery/`
 - type:      `nodetypes`
 - namespace: `http%253A%252F%252Fopentosca.org%252Fnodetypes`
 - id:        `Java8`

We have a closer look to the namespace part `http%253A%252F%252Fopentosca.org%252Fnodetypes`:
The '/' and ':' of the URI are double [percent-encoded](https://tools.ietf.org/html/rfc3986#section-2.1), i.e.,
`:` is encoded --> `%3A` is encoded --> `%253A` (`%` is encoded --> `%25`).
 
The convention for URLs is that URLs have always be decoded [when dereferencing](https://tools.ietf.org/html/rfc3986#section-2.4).
That means, that when calling the above URL the result received by the winery backend is:
`http://localhost:8080/winery/nodetypes/http%3A%2F%2Fopentosca.org%2Fnodetypes/Java8/`
An automated decoding happened by the browser for the GET.
If no double encoding whould be used http://localhost:8080/winery/nodetypes/http%3A%2F%2Fopentosca.org%252Fnodetypes/Java8/ whould be the used URL.
This would leads to a GET on http://localhost:8080/winery/nodetypes/http://opentosca.org/nodetypes/Java8/
This is an invalid URL because of the part 'http://'
2. Case:
As already mentioned URLs used in directory names must be encoded.
In XMl files the 'location' of an tosca:import is, for example given as: '../imports/http%253A%252F%252Fwww.w3.org%252F2001%252FXMLSchema/http%253A%252F%252Fopentosca.org%252Fproperties/TopicProperties/TopicProperties.xsd' which represents the path of the file.
The attribute 'location' is of type anyURI, i.e., this part has to be decoded.
If only single encouding is used like '../imports/http%3A%2F%2Fwww.w3.org...' the decoding whould leady to '../imports/http://www.w3.org...' which is not a valid URI and also not a valid file name.
Summary: Because the namespace is part of the used URIs and the ns itself is a URI too this double encoding is required.

## Background

- nginx, [Apache, and JBoss](https://stackoverflow.com/a/5628325/873282) forbid `%2F` as part of the URL for security reasons.
- [Apache can be reconfigured](https://stackoverflow.com/a/3235361/873282)
- [Tomcat can be reconfigured](https://stackoverflow.com/a/41559969/873282)
- `%2F` and `/` are not the same in an URI: See Example 2 in [W3C's URI recommendations](https://www.w3.org/Addressing/URL/4_URI_Recommentations.html).


`ROOT/<componenttype>s/<double-encoded-namespace>/<double-encoded-id>/<resource-specific-part>`, which makes the structure similar to the file system (cf. Section 6).
Encoding is done following [RFC 3986](https://tools.ietf.org/html/rfc3986#section-2.1). 
An online URL-encoder may be found at: <http://www.albinoresearch.com/misc/urlencode.php>.

For instance, the NodeType "NT1" in the namespace `http://www.example.com/NodeTypes` is found at the URL `nodetypes/http%253A%252F%252Fexample.com%252FNodeTypes/NT1/`.
As the browser decodes the URL, the namespace and the id are double encoded. 
Note the additional encoding of the symbol `%` in comparison to the encoding at the filesystem.

Encoding of directory and file names is done following [RFC 3986](https://tools.ietf.org/html/rfc3986#section-2.1).
This makes the structure consistent to the URL structure (cf. [URL structure in the developer guide](./#url-structure)).

The URL encoding is necessary as some letter allowed in namespaces (e.g. `.`, `:`, `;`, `/`) and IDs are not allowed on all operating systems.
IDs are [NCNames](https://www.w3.org/TR/xmlschema-2/#NCName), which are based on [XML 1.0 Names](https://www.w3.org/TR/2000/WD-xml-2e-20000814#NT-Name), which in turn allows [nearly all unicode characters](https://www.w3.org/TR/2000/WD-xml-2e-20000814#NT-Letter).
Therefore, each namespace and ID is URL encoded when written to the filesystem and URL decoded when read from the filesystem.

## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. Made available under the terms of the [Eclipse Public License v1.0] and the [Apache License v2.0] which both accompany this distribution.


 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
