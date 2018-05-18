# Branches

The `master` branch is always compiling and all tests should go through.
It contains the most recent improvements.
All other branches are real development branches and might event not compile.

There are no explicit branches for stable versions as winery is currently in development-only mode.

We try to follow following naming conventions:

  - Bugfix: `fix/issue-NNN` or `fix/SHORT-TITLE` if fixing an issue with a number or give it a title
  - Feature: `feature/issue-NNN` or `feature/SHORT-TITLE`
  - WIP: `wip/SHORT-TITLE` for work in progress without issue and you know won't be finished soon
  - Thesis: `thesis/SHORT-THESIS-TITLE`. Replace `SHORT-THESIS-TITLE` with something meaningful
  - EnPro: prefix `fix`, `feature`, `wip` (see below) with `enpro/`
  - StuPro: prefix `fix`, `feature`, `wip` (see below) with `stupro/`

See <https://gist.github.com/revett/88ee5abf5a9a097b4c88> for a discussion and other ideas.

## Editing the gh-pages branch of the test repository

It is beneficial to have the [gh-pages branch](https://github.com/winery/test-repository/tree/gh-pages) of the test repository checked out in a second directory to be able to work in Winery and on the homepage in parallel.
Git supports that by the [worktree command](https://git-scm.com/docs/git-worktree):

1. Change directory to `C:\winery-repository`
2. Execute `git worktree add ..\git-repositories\winery-repository-gh-pages gh-pages` to check out the `gh-pages` branch into `C:\git-repositories\winery-repository-gh-pages`.

Full output:

```
$ C:\winery-repository
> git worktree add ..\git-repositories\winery-repository-gh-pages gh-pages
Preparing ../git-repositories/winery/winery-repository-gh-pages (identifier winery-repository-gh-pages)
HEAD is now at 5017713 Initial documentation
```

## License

Copyright (c) 2013-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

 [Apache Maven]: https://maven.apache.org/
 [bower]: https://bower.io/
 [jsPlumb#165]: https://github.com/jsplumb/jsPlumb/issues/165
