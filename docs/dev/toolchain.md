<!---~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright (c) 2020 Contributors to the Eclipse Foundation
  ~
  ~ See the NOTICE file(s) distributed with this work for additional
  ~ information regarding copyright ownership.
  ~
  ~ This program and the accompanying materials are made available under the
  ~ terms of the Eclipse Public License 2.0 which is available at
  ~ http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
  ~ which is available at https://www.apache.org/licenses/LICENSE-2.0.
  ~
  ~ SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->


# Eclipse Winery Toolchain

To contribute to Eclipse Winery development you need a GitHub account and access to <https://github.com/opentosca/winery>.
Email your supervisor your GitHub username.

- In case you did not choose an account name, use `flastname` as pattern:
  `f` is the lower-case first letter of your first name and
  `lastname` is the lower-case last name.
- Due to open source development, your email address will get public.
  In case, you don't have a public email address, we recommend creating one or use your student email address.
  In case you want to create a longer-lasting one, please use the GitHub username.
  Example: `flastname@gmail.com`.
- Please enable the git-hooks by executing `git config core.hooksPath .git-hooks` in the root of the repository.

## Install Apache Maven

Get [Apache Maven](https://maven.apache.org/) to run.

## Get write access to the code repositories

1. Email your supervisor your GitHub username and your development email address.
2. Your supervisor adds you to the team "developers" at <https://github.com/opentosca> and <https://github.com/winery>.
3. You will receive two emails from GitHub asking for your confirmation.
4. Open the link <https://github.com/orgs/winery/invitation>.
5. Open the link <https://github.com/orgs/OpenTOSCA/invitation>.

## Steps to initialize the code repository

1. Clone <https://github.com/opentosca/winery> (it automatically becomes the `origin`).
   - We recommend that git repositories reside in `c:\git-repositories`.
   - Use [ConEmu](https://conemu.github.io) as a program for all your shells: `choco install conemu`.
     Install [chocolatey](https://chocolatey.org/) to use the `choco` command.
   - Execute `git clone https://github.com/OpenTOSCA/winery.git` in `c:\git-repositories`.
2. Change into the newly created directory `winery`: `cd winery`.
3. Add `upstream` as second remote: `git remote add upstream https://github.com/eclipse/winery.git`
4. Fetch everything from `upstream`: `git fetch upstream` or `git fetch --all`
5. Run `mvn clean install -DskipTests` to build the whole project

## Steps to initialize a TOSCA repository

Winery has built-in *magic* to check for existence of `c:/winery-repository` on Windows.
If that directory exists, this is used as repository location.
If that directory does not exists, it uses the home directory, which is `%HOME%` defaulting to `c:/Users/<USERNAME>/winery-repository`
(or `~/winery-repository` on Linux-based operating systems).

However, you can start Winery based on an existing repository.

XML Repo: <https://github.com/OpenTOSCA/tosca-definitions-internal>
YAML Repo: https://github.com/radon-h2020/radon-particles

1. Clone one the repositories from above
2. Start the Winery backend to initialize the configuration files
3. Go to `C:\Users\<your user>\.winery` on your computer and open the file `winery.yml`
4. Adjust the value of the `provider` and `repositoryRoot` attribute:

   ```
   provider: file
   repositoryRoot: c:\git-repositories\tosca-definitions-internal
   ```
   or
   ```
   provider: yaml
   repositoryRoot: c:\git-repositories\radon-particles
   ```
5. Restart your Winery backend

## Steps to initialize the IDE

Setup IntelliJ IDEA as described at [config/IntelliJ IDEA](../config/IntelliJ%20IDEA/index.md).
Alternatively, you can you Eclipse as described at [config/Eclipse](../config/Eclipse/index.md).
However, the latter is currently work-in-progress.
