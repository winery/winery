# Eclipse Winery Toolchain

To contribute to Eclipse Winery development you need a GitHub account and access to <https://github.com/opentosca/winery>.
Email your supervisor your GitHub username.

- In case you did not choose an account name, use `flastname` as pattern:
  `f` is the lower-case first letter of your firstname and
  `lastname` is the lower-case lastname.
- Due to open source development, your email adress will get public.
  In case, you don't have a public email adress, we recommend to create one or use your student email adress.
  In case you want to create a longer-lasting one, please use the GitHub username.
  Example: `flastname@gmail.com`.
- Please enable the git-hooks by executing `git config core.hooksPath .git-hooks` in the root of the repository.

## Steps to get Apache Maven ready

Get [Apache Maven](https://maven.apache.org/) to run.

Windows:

1. Get [choclatey](https://chocolatey.org/)
1. Execute in an **Administor cmd.exe**: `@"%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe" -NoProfile -InputFormat None -ExecutionPolicy Bypass -Command "iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))" && SET "PATH=%PATH%;%ALLUSERSPROFILE%\chocolatey\bin"`
1. Execute `choco install maven`. This also installs the latest Java8 JDK.

## Steps to get write access to the code repositories

1. Email your supervisor your GitHub username and your development email address.
2. Your supervisor adds you to the team "developers" at <https://github.com/opentosca> and <https://github.com/winery>.
3. You will receive two emails from GitHub asking for your confirmation.
4. Open the link <https://github.com/orgs/winery/invitation>.
5. Open the link <https://github.com/orgs/OpenTOSCA/invitation>.

## Steps to initialize the code repository

1. Clone <https://github.com/opentosca/winery> (it automatically becomes the `origin`).
   - We recommend that git repositories reside in `c:\git-repositories`.
   - Use [ConEmu](https://conemu.github.io/) as program for all your shells: `choco install conemu`.
     Install [chocolatey](https://chocolatey.org/) to use the `choco` command.
   - Execute `git clone https://github.com/OpenTOSCA/winery.git` in `c:\git-repositories`.
2. Change into the newly created directory `winery`: `cd winery`.
3. Add `upstream` as second remote: `git remote add upstream https://github.com/eclipse/winery.git`
4. Fetch everything from `upstream`: `git fetch upstream`
5. Run `mvn package -DskipTests` to have `Version.java` generated

## Steps to initialize the TOSCA repository

Please go to the [quick start guide](../user/quickstart.md).

## Steps to initialize the IDE

Setup IntelliJ as described at [config/IntelliJ IDEA](config/IntelliJ%20IDEA).
Alternatively, you can you Eclipse as described at [config/Eclipse](config/Eclipse).
However, the latter is currently work-in-progress.

## License

Copyright (c) 2013-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
