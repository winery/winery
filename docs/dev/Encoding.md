# Encoding of folder names, namespaces, and ids in Winery

**Outdated** Although the information about double-encoding is correct, the UI has been re-implemented using Angular, which introduces an additional layer of URLs. 

## Example to understand double encoding of URIs in Winery

In file systems, characters `/` and `:` are not allowed [source](https://stackoverflow.com/a/31976060/873282).
The [design decision](../adr/0002-filesystem-folder-structure-using-type-namespace-id-structure.md) is that all files (node types, imports in CSARs, ...) are stored and structured using directories with human readable names.
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

If no double encoding whould be used http://localhost:8080/winery/nodetypes/http%3A%2F%2Fopentosca.org%252Fnodetypes/Java8/ whould be the used URL.
Web servers such as
Apache Tomcat [[1](https://stackoverflow.com/a/14600740/873282), [2](https://stackoverflow.com/a/41559969/873282)],
Apache [[3](https://stackoverflow.com/a/9933890/873282),[4](https://stackoverflow.com/a/3235361/873282)],
nginx [[5](https://stackoverflow.com/a/37584637/873282)], and
JBoss [[6](https://stackoverflow.com/a/5628325/873282)]
forbit this due to security issues. See
- http://en.wikipedia.org/wiki/Directory_traversal_attack
- http://www.tomcatexpert.com/blog/2011/11/02/best-practices-securing-apache-tomcat-7
- https://stackoverflow.com/a/28113090/873282

Note that `%2F` and `/` are not the same in an URI: See Example 2 in [W3C's URI recommendations](https://www.w3.org/Addressing/URL/4_URI_Recommentations.html).

 
### Second Case: Encoding of directory names

As already mentioned, URLs used in directory names must be encoded.
In XML files the `location` of an `tosca:import` is, for instance given as: `../imports/http%253A%252F%252Fwww.w3.org%252F2001%252FXMLSchema/http%253A%252F%252Fopentosca.org%252Fproperties/TopicProperties/TopicProperties.xsd`, which represents the path of the file.
The attribute `location` is of type anyURI, i.e., this part [has to be decoded](https://tools.ietf.org/html/rfc3986#section-2.4).
If only single encouding is used like `../imports/http%3A%2F%2Fwww.w3.org...` the decoding whould lead to `../imports/http://www.w3.org...`, which is not a valid URI and also not a valid file name.
Summary: Because the namespace is part of the used URIs and the ns itself is a URI too this double encoding is required.


## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
