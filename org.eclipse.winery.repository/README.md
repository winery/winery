# Winery Repository

## REST API
Winery offers a REST API to communicate with the backend.
A documentation is not yet available.

## About the code
The code tries to make use of EL and JSP tags wherever possible. All data is accessible via a REST API.
The REST API does **not** follow the HATEOAS approach.
The URLs follow the pattern `/<type>/<encoded namespace>/<encoded id>`, where `type` is `servicetemplate`, `nodetype`, ...
Below a concrete instance, subresources such as `name` exist.

Currently, Winery is switching from plain Javascript library loading to [RequireJS].

The file `src/main/webapp/WEB-INF/common-functions.tld` and the files in `src/main/webapp/WEB-INF/tags/common` are copied from the sister project `org.eclipse.winery.topologymodler` at `mvn generate-sources`.

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
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
 [RequireJS]: http://requirejs.org/
