# Quickstart

On Windows:

1. `mkdir c:\winery-repository`
2. `cd c:\winery-repository`
2. `git config --global core.longpaths true` to enable long paths. Works perfectly on Windows.
3. `git clone https://github.com/winery/test-repository.git .` to clone the [test repository](https://github.com/winery/test-repository).
4. Uni Stuttgart developers: `git remote add tosca-definitions-internal https://github.com/OpenTOSCA/tosca-definitions-internal/` to make the [tosca-definitions-ustutt](https://github.com/OpenTOSCA/tosca-definitions-internal/) known.
5. `git fetch tosca-definitions` - to fetch the tosca-definitions repository
6. `git checkout black` - to switch to the main branch of the test repository

Now you are at the [test repository](https://github.com/winery/test-repository) containing testing types.
If you do `git checkout master`, you are seeing the [OpenTOSCA TOSCA Definitions repository](https://github.com/OpenTOSCA/tosca-definitions/).

## License

Copyright (c) 2017-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
