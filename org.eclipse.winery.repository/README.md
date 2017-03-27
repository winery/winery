# Winery Repository

Setup, usage, and implementation hints are given in the [Eclipse Wiki]

## REST API
Winery offers a REST API to communicate with the backend.
A documentation is not yet available.

## About the code
The code tries to make use of EL and JSP tags wherever possible. All data is accessible via a REST API.
The REST API does **not** follow the HATEOAS approach.
The URLs follow the pattern `/<type>/<encoded namespace>/<encoded id>`, where `type` is `servicetemplate`, `nodetype`, ...
Below a concrete instance, subresources such as `name` exist.

Definitions are not modeled as explicit element. Only the nested elements are handled by Winery.
That means, it is not possible to specify custom definitions bundling a customized subset of available elements.

Intentionally, a QName should be unique within the repository.
We did not follow this assumption, but only require that QNames are unique within a type.
That means, the repository allows `{http://www.example.org}id` for both a service template and a node type.
We introduced TOSCAcomponentId uniquely identifying a TOSCA element.
Future versions might redesign the backend to use a QName as the unique key.

Currently, Winery is switching from plain Javascript library loading to [RequireJS].

The file `src/main/webapp/WEB-INF/common-functions.tld` and the files in `src/main/webapp/WEB-INF/tags/common` are copied from the sister project `org.eclipse.winery.topologymodler` at `mvn generate-sources`.

### Trouble shooting
In case, `Version.java` is not found, then run `mvn compile`, which should trigger a regeneration of Version.java.

The error message
`HTTP Status 500 - com.sun.jersey.api.container.ContainerException: org.apache.jasper.JasperException: The absolute uri: http://www.eclipse.org/winery/functions cannot be resolved in either web.xml or the jar files deployed with this application` indicates that `mvn generate-sources` was not run.

In case `javax.servlet.jsp.PageContext` cannot be found:
Project -> right click -> Properties -> Project Facets -> Dynamic Web Module -> "Runtimes" -> "New..."

When running in jetty 9.0.5, there is always the error message "Request Entity Too Large" when uploading a file.
There is the `maxFormContentSize` set in `jetty-web.xml`, but it currently does not help to solve this issue.

When doing a copy-libs-to-tomcat hack, possibly "W3C_XML_SCHEMA_NS_URI cannot be resolved or is not a field" appears.
Remove `stax-api-1.0.1.jar` out of `tomcat7/lib`: Java's `rt.jar` should be used instead for `javax.xml.XMLConstants`.

## License
Copyright (c) 2012-2014 University of Stuttgart.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v1.0]
and the [Apache License v2.0] which both accompany this distribution,
and are available at http://www.eclipse.org/legal/epl-v10.html
and http://www.apache.org/licenses/LICENSE-2.0

Contributors:
* Oliver Kopp - initial API and implementation


 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Wiki]: http://wiki.eclipse.org/winery
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
 [RequireJS]: http://requirejs.org/
