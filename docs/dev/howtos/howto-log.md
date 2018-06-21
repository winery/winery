# How to log

The `logback-test.xml` is the same for each project.
A change, however, needs to be synced manually.
Use following commands at git bash:

    find . -maxdepth 1 -type d -name "org.eclipse.*" -exec  cp org.eclipse.winery.repository.rest/src/test/resources/logback-test.xml \{\}/src/test/resources/ \;

## License

Copyright (c) 2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.
