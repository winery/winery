# Winery Toolchain

<!-- toc -->

- [GitHub - Start](#github---start)
- [GitHub - Preparation First Pull Request](#github---preparation-first-pull-request)
- [GitHub - Prepare Pull Request](#github---prepare-pull-request)
- [GitHub - Create Pull Request](#github---create-pull-request)
- [GitHub - Change Pull Request](#github---change-pull-request)
- [Excursus: Git](#excursus-git)

<!-- tocstop -->

This presents the tool chain used for creating and updating a pull request on GitHub.

For setup the IDE, please go to the [DevGuide](./).

## GitHub - Start

* To contribute to the winery you need a github account and access to https://github.com/opentosca/winery
* First steps:
  1. Clone opentosca/winery (automatically it becomes the origin)
  2. git remote add upstream [https://github.com/eclipse/winery.git]
* Steps for working on a topic
  1. Create a new branch for each topic (fix a bug, add functionality) and name it accordingly.
  2. Sync with latest upstream/master: `git fetch upstream`
  3. Create branch based on `upstream/master` and make it known publicly:
     - `git checkout upstream/master`
	 - `git checkout -b [name]`
	 - `git push --set-upstream origin [name]`
  4. Work on the branch with the specific name
  5. Commit. Don't forget to sign the commit (<kbd>Ctrl</kbd>+<kbd>S</kbd> in Git Gui)
  6. Push the changes to origin: `git push`

## GitHub - Preparation First Pull Request

* Check winery/CONTRIBUTING.md and carefully read the instruction
* http://wiki.eclipse.org/Development_Resources/Contributing_via_Git --> Create an account WITH THE SAME EMAIL THEN USED FOR THE COMMITS (can also be checked in Gitk)
* Sign the Contributor Agreement electronically

## GitHub - Prepare Pull Request

* Check [CONTRIBUTING.MD](https://github.com/eclipse/winery/blob/master/CONTRIBUTING.md)
* Steps to prepare Pull Request:
  1. `git fetch upstream`
  2. `git merge upstream/master`
  3. (Resolve merge conflicts)
  4. Commit & Push with signed commit message (<kbd>Ctrl</kbd>+<kbd>S</kbd> in Git Gui)
  5. `git reset upstream/master` (to achieve that all commits are squashed together)
  6. Check changes in Git Gui & adapt the Copyright information in the changed files & Check again the style (!) (Don't forget RESCAN to see the current changes)
  7. Add Changes/Fixed to the CHANGELOG.md and add description to docs/index (if helpful)
  8. Stage To Commit --> All Changes are staged to Commit
  9. Sign the Commit Message (<kbd>Ctrl</kbd>+<kbd>S</kbd>)
  10. Commit & Push with "force overwrite" since you changed the branch: `git push -f`

## GitHub - Create Pull Request

Attention: Commits on the same branch done after the Pull Request is sent are still part of the Pull Request (!)

* Go to eclipse/winery --> Pull Request
* Fill in the title of the Pull Request and give a more detailed description of the changes or added functionality
* In case of UI changes: Add screenshots
* Add `[x]` to the items listed in the write field
* Check the description in the Preview and send the Pull Request

## GitHub - Change Pull Request

* There are automatic checks in place

![GitAutoCheck](graphics/autoCheckGit.png)

* If there is a red cross, click in repective "Details" and fix them (see next slide)

* In case of missing code quality,...
* Changes are requested by the Commiter (person controlling the pull request process)
* FOR WINERY THE FOLLOWING APPLY:
  - Open Git Gui
  - Make requested changes in your code (don't forget to RESCAN)
  - Commit
  - Push

## Excursus: Git

![ExcursusGit](graphics/ExcursusGit.png)
