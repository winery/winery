# Building Winery

Winery uses [Apache Maven] for Java depedency management.
[bower] is used for fetching JavaScript dependencies.
Bower is installed automatically using the [frontend-maven-plugin].
We recommend installing JDK8 by using `choco install jdk8` to be able to update it via `choco upgrade all`. See at the homepage of [chocolatey] for more information.
Please follow the the next step "Making the wars" before importing the project into an IDE.

### Making the wars

Run `mvn package`.
In case [bower] fails, try to investigate using `mvn package -X`.
You can start bower manually in `org.eclipse.winery.repository` and `org.eclipse.winery.topologymodeler` by issuing `bower install`.

There are four WARs generated:

* `org.eclipse.winery.repository.rest/target/winery.war` - the REST interface to the repository
* `org.eclipse.winery.repository.ui/target/winery-ui.war` - the UI for the repository
* `org.eclipse.winery.topologymodeler/target/winery-topologymodeler.war` - the topology modeler
* `org.eclipse.winery.workflowmodeler/target/winery-workflowmodeler.war` - the workflow modeler

They can be deployed on a Apache Tomcat runtime environment.

 [Apache Maven]: https://maven.apache.org/
 [bower]: https://bower.io/

## License

Copyright (c) 2013-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
