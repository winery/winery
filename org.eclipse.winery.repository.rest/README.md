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
Copyright (c) 2012-2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

 [RequireJS]: http://requirejs.org/
