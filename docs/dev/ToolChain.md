# Eclipse Winery Toolchain

<!-- toc -->

- [GitHub - Start](#github---start)
  * [First steps](#first-steps)
  * [Steps for working on a topic](#steps-for-working-on-a-topic)
- [GitHub - Preparation First Pull Request to offical Eclipse Winery repository](#github---preparation-first-pull-request-to-offical-eclipse-winery-repository)
- [GitHub - Prepare Pull Request](#github---prepare-pull-request)
- [GitHub - Create Pull Request](#github---create-pull-request)
- [GitHub - Change Pull Request](#github---change-pull-request)
- [GitHub - After Pull Request Got Merged](#github---after-pull-request-got-merged)
- [Excursus: Git](#excursus-git)
- [License](#license)

<!-- tocstop -->

This presents the tool chain used for creating and updating a pull request on GitHub.

For setup the IDE, please go to the [DevGuide](./).

## GitHub - Start

To contribute to Eclipse Winery development you need a GitHub account and access to <https://github.com/opentosca/winery>.
Email your supervisor your GitHub username.

- In case you did not choose an account name, use `flastname` as pattern:
  `f` is the lower-case first letter of your firstname and
  `lastname` is the lower-case lastname.
- Due to open source development, your email adress will get public.
  In case, you don't have a public email adress, we recommend to create one or use your student email adress.
  In case you want to create a longer-lasting one, please use the GitHub username.
  Example: `flastname@gmail.com`.

### First steps

  1. Clone <https://github.com/opentosca/winery> (automatically it becomes the `origin`).
     - We recommend that git repositories reside in `c:\git-repositories`.
     - Use [ConEmu](https://conemu.github.io/) as program for all your shells: `choco install conemu`.
       Install [chocolatey](https://chocolatey.org/) to use the `choco` command.
     - Execute `git clone https://github.com/OpenTOSCA/winery.git` in `c:\git-repositories`.
  2. Change into the newly created directory `winery`.
  2. Add `upstream` as second remote: `git remote add upstream https://github.com/eclipse/winery.git`

### Steps for working on a topic

  1. Create a new branch for each topic (fix a bug, add functionality) and name it accordingly.
  2. Sync with latest changes on origin (especially master branch): `git fetch origin`.
  3. Create branch based on `origin/master` and make it known publicly:
     - `git checkout origin/master`
     - `git checkout -b [name]`
     - `git push --set-upstream origin [name]`
  4. Work on the branch with the specific name
  5. Commit. Don't forget to sign the commit (<kbd>Ctrl</kbd>+<kbd>S</kbd> in Git Gui).
  6. Push the changes to origin: `git push`.

You keep working and discuss with your supervisor how things go.
For that, create a pull request to https://github.com/opentosca/winery to enable internal reviewing.

## GitHub - Preparation First Pull Request to offical Eclipse Winery repository

* Check [CONTRIBUTING.md](https://github.com/eclipse/winery/blob/master/CONTRIBUTING.md) and carefully read the instructions
* <http://wiki.eclipse.org/Development_Resources/Contributing_via_Git> 🡒 Create an account **WITH THE SAME EMAIL THEN USED FOR THE COMMITS** (can also be checked in [gitk])
* Sign the Contributor Agreement electronically

## GitHub - Prepare Pull Request

The aim of these steps to have a **single commit**.
This is required by the Eclipse process for checking for intellectual property (IP process for short).

* Check [CONTRIBUTING.md](https://github.com/eclipse/winery/blob/master/CONTRIBUTING.md).
* Steps to prepare Pull Request:
  1. `git fetch upstream` - fetches all updates from https://github.com/eclipse/winery ("upstream") to the local git storage
  2. `git merge upstream/master` - merges all updates from upstream to the local branch
  3. (Resolve merge conflicts) - required if there are conflicting changes
  4. Commit & Push with signed commit message (<kbd>Ctrl</kbd>+<kbd>S</kbd> in Git Gui) - this ensures that you have the changes backuped in case something goes wrong at the next steps 
  5. `git reset upstream/master` - this prepares that all commits can be squashed together:
     The local checkout ("working tree") is left untouched, but the "pointer" of the current branch is reset to `upstream/master`.
     Now, Git Gui shows the difference between `upstream/master` and your changes.
  6. Check changes in Git Gui:
     - Each change you wanted: Is it recognized?
     - At each file: Is the copyright information in the header OK?
     - Are there too much changed lines? 🡒 Do not stage spurious lines to the commit (e.g., tab to spaces, ...)
     - Are there too much changed files? 🡒 Do not stage files you did not intend to change (e.g., `build.gradle` if you did not touch `build.gradle` at all)
     - Check again the style (!)
     - (Don't forget RESCAN to see the current changes)
  7. Add Changes/Fixed to `CHANGELOG.md` and add description to `docs/index.md` (if helpful)
  8. Press "Stage to Commit" 🡒 all changes are staged to Commit
  9. Sign the Commit Message (<kbd>Ctrl</kbd>+<kbd>S</kbd> in Git Gui)
  10. Commit & Push with "force overwrite" since you changed the branch: `git push -f`

## GitHub - Create Pull Request

**Attention: Commits on the same branch done after the Pull Request is sent are still part of the Pull Request (!)**

* Go to https://github.com/eclipse/winery 🡒 Pull Request
* Fill in the title of the Pull Request and give a more detailed description of the changes or added functionality
* In case of UI changes: Add screenshots
* Add `[x]` to the items listed in the write field
* Check the description in the Preview and send the Pull Request

## GitHub - Change Pull Request

* There are automatic checks in place

![GitAutoCheck](graphics/autoCheckGit.png)

* If there is a red cross, click in repective "Details" and fix them

* In case of missing code quality, ... changes are requested by a committer (person controlling the pull request process)
* FOR WINERY THE FOLLOWING APPLIES:
  - Open Git Gui
  - Make requested changes in your code (don't forget to RESCAN)
  - Commit
  - Push
  - Wait for a second review
  - In case everything is fine, squash the commits into one.
    See [GitHub - Prepare Pull Request](#github---prepare-pull-request).
    Then, do a force push (`git push -f`).


## GitHub - After Pull Request Got Merged

* Delete the branch locally.
  The branch on origin (<https://github.com/OpenTOSCA/winery>) is deleted by the maintainer having done the merge.


## Excursus: Git

![ExcursusGit](graphics/ExcursusGit.png)

Please see also [use gitk to understand git](https://lostechies.com/joshuaflanagan/2010/09/03/use-gitk-to-understand-git/) to understand the settings in git.

## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v2.0]
and the [Apache License v2.0] which both accompany this distribution.

  [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
  [Eclipse Public License v2.0]: http://www.eclipse.org/legal/epl-v20.html
  [gitk]: https://lostechies.com/joshuaflanagan/2010/09/03/use-gitk-to-understand-git/
