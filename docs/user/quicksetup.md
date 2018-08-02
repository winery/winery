# Quick Setup

These steps ensure that

- You have a set of TOSCA definitions (node types, relationship types, etc)
- Winery sees that TOSCA definitions when you start it.

## Windows

Winery has built-in "magic" to check for existance of `C:\winery-repository`.
If that directory exists, this is used as repository location.
If that directory does not exists, it uses the home directory, which is `%HOME%` defaulting to `C:\Users\USERNAME\winery-repository`.

1. Ensure that git and [git-lfs](https://git-lfs.github.com/) are installed.
    1. Installation using [chocolatey](https://chocolatey.org/): `choco install git git-lfs`
    1. Manual installation:
        - Download git installer form <https://git-scm.com/downloads> and execute it.
        - Download git-lfs installer from <https://git-lfs.github.com/> and execute it.
1. Make repository available.
    1. `mkdir c:\winery-repository`. The name `winery-repository` is important, because of Winery's magic (see above)
    2. `cd c:\winery-repository`
    2. `git config --global core.longpaths true` to enable long paths. Works perfectly on Windows.
    3. `git clone https://github.com/winery/test-repository.git .` to clone the [test repository](https://github.com/winery/test-repository).
    3. Execute `git lfs install`
    4. Uni Stuttgart developers:
        1. `git remote add tosca-definitions-internal https://github.com/OpenTOSCA/tosca-definitions-internal/` to make the [tosca-definitions-internal](https://github.com/OpenTOSCA/tosca-definitions-internal/) known.
        1. `git fetch tosca-definitions-internal` - to fetch the tosca-definitions repository
    6. `git checkout black` - to switch to the main branch of the test repository
    7. Result: Now you are at the [test repository](https://github.com/winery/test-repository) containing testing types.
       If you do `git checkout master`, you are seeing the [OpenTOSCA TOSCA Definitions repository](https://github.com/OpenTOSCA/tosca-definitions/).

## Mac OS X

Winery stores its repository at `~/winery-repository`.

This howto is currently incomplete.

1. Ensure that git and [git-lfs](https://git-lfs.github.com/) are installed.
    1. `brew install git-lfs`


## License

Copyright (c) 2017-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
