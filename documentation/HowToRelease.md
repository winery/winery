# How to do a release

As the maven release plugin does not yet support bower, the versions in `bower.json` have to be adapted manually.

- remove `-SNAPSHOT` in `/org.eclipse.repository/bower.json`
- remove `-SNAPSHOT` in `/org.eclipse.winery.topologymodeler/bower.json`
- execute `mvn -B release:prepare`
- increase version number in /org.eclipse.repository/bower.json and add `-SNAPSHOT`
- increase version number in /org.eclipse.winery.topologymodeler/bower.json and add `-SNAPSHOT`
- execute `mvn release:perform`

# License
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
