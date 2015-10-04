(This is based on the [Jetty Contributing Patches] documentation)

# Contributing Patches
This file describes how to contribute a patch to the Winery project.
You should first familiarize yourself with the Eclipse wiki page on [contributing via Git].

## Sign a CLA
The Eclipse Foundation has a strong Intellectual Property policy which tracks contributions in detail to ensure that:

* Did the contributor author 100% of the content?
* Does the contributor have the rights to contribute this content to Eclipse?
* Is the contribution under the project's license(s) (e.g. [EPL])

Thus a contributor needs to e-sign a [Contributor License Agreement] (for more explanation see the Eclipse [CLA FAQ]) regardless of how their contribution patch is provided.

### Signing an Eclipse CLA
Log into the [Eclipse projects forge] (you will need to create an account with the Eclipse Foundation if you have not already done so); click on "Contributor License Agreement"; and Complete the form. Be sure to use the same email address when you create any Git commit records.

## Use Bugzilla
Once a CLA has been signed, then patches should always be contributed with an associated [project bugzilla].
The CLA symbol next to the contributors name in the bugzilla should be green to indicate the CLA is on record.
This will allow the authors contribution to both be tracked and acknowledged.

## Git Diff
The simplest way to contribute a patch is to make a modification to a cloned copy of Winery and then generate a diff between the two versions.
We don't really like this approach, but it is difficult to ignore how easy it is for the contributer.
Just remember, you still need to create a CLA as mentioned above.

From the top level of the cloned project:

    $ git diff > ######.patch

The hash marks should be the bugzilla issue that you will be attaching the issue to.
All patches coming into Winery must come in through bugzilla for IP tracking purposes.
Depending on the size of the patch the patch itself may be flagged as `+iplog` where it is subject to lawyer review and inclusion with our iplog from here to eternity.
We are sorry we are unable to apply patches that we receive via email.
So if you have the bugzilla issue created already just attach the issue.
If there is no bugzilla issue yet, create one, make sure the patch is named appropriately and attach it.

When the developer reviews the patch and goes to apply it they will use:

    $ git apply < ######.patch

If you want to be a nice person, test your patch on a clean clone to ensure that it applies cleanly. Nothing frustrates a developer quite like a patch that doesn't apply.

## RECOMMENDED - Git Format Patch
Another approach if you want your name in shiny lights in our commit logs is to use the format patch option.
With this approach you commit into your cloned copy of Winery and use the git format patch option to generate what looks like an email message containing all of the commit information.
This applies as a commit directly when we apply it so it should be obvious that as with the normal diff we must accept these sorts of patches only via bugzilla.
Make sure your commit is using the email that you registered in your CLA or no amount of pushing the in world from us will get past the eclipse git commit hooks.
When you do your commit to your local repo it is also vital that you "sign-off" on the commit using `git commit -s`.
Without the sign-off, your patch cannot be applied to the jetty repo because it will be rejected by the eclipse git commit hooks.

From the top level of the cloned project:

Make your changes and commit them locally using `git commit -s`:
      
    $ git commit -s

Then use `git log` to identify the commit(s) you want to include in your patch:

    commit 70e29326fe904675f772b88a67128c0b3529565e
    Author: John Doe <john.doe@who.com>
    Date: Tue Aug 2 14:36:50 2011 +0200 353563:
    HttpDestinationQueueTest too slow

Use `git format-patch` to create the patch:

    $ git format-patch -M -B 70e29326fe904675f772b88a67128c0b3529565e

This will create a single patch file for each commit since the specified commit.
The names will start with `0001-[commitmessage].patch`.
See http://www.kernel.org/pub/software/scm/git/docs/git-format-patch.html for details.

When a developer goes to apply this sort of patch then we must assume responsibility for applying it to our codebase from the IP perspective.
So we must be comfortable with the providence of the patch and that it is clear of potential issues.
This is not like a diff where you get to edit it and clean up issues before it gets applied.
The commit is recorded locally and the developer will then have a chance to make additional commits to address any lingering issues.
It is critically important that developers applying these sorts of patches are fully aware of what is being committed and what they are accepting.

To apply the patch the developer will use a command like:

    $ git am 0001-353563-HttpDestinationQueueTest-too-slow.patch

Providing it applies cleanly there will now be a commit in their local copy and they can either make additional commits or push it out.

### Note
It is intended that developers are also able to counter-sign the patch by using the `-s` option with the `git am` command.
However as the git hook that processes the commit currently has a bug it is recommended that developers do NOT use the `-s` option.
See https://bugs.eclipse.org/bugs/show_bug.cgi?id=415307

## Git Amend
If a committer is having trouble applying the patch cleanly with git am, they can use `git commit --amend` to modify the author and signoff the commit. For example:

    $ git checkout -b patch
    $ git apply john-doe.patch
    $ git commit -a -m "<Original commit message from John Doe>"

At this point the patch is committed with the committer's name on a local branch

    $ git commit --amend --author "John Doe <john.doe@who.com>" --signoff

Now the patch has the right author and it has been signed off

    $ git checkout master
    $ git merge patch

Now the local branch has been merged into master with the right author

    $ git branch -d patch
    $ git push

## Contributing via Gerrit
Winery currently has no Gerrit infrastructure in place.
In case, we will receive a lot of patches, we will enable [the Eclipse Gerrit workflow](https://wiki.eclipse.org/Gerrit).

## Contributing via Github PullRequests
The Winery eclipse git repository is mirrored to github at http://github.com/winery/winery.
Github has a suite of collaboration tools for submitting and reviewing contributions, but unfortunately the Eclipse Foundations IP policy prevents direct merging of github pull requests.
However, if a contributor makes a pull request and references that in a bugzilla with a signed CLA, then a Winery committer should be able to fetch, merge and commit the pull requests without the need to create a separate patch.

## Github pull requests for Committers
A committer can prepare their repository for accepting Github pull requests as follows:

    $ git remote add github https://github.com/winery/winery.git
    $ git config --add remote.github.fetch "+refs/pull/*/head:refs/remotes/origin/pr/*"

A committer can then fetch the latest pull request and check them out as follows (for pull request #123):

    $ git fetch github
    $ git checkout pr/123

The committer can then use normal git commands to merge the contribution back to the master branch. The commits may need to be signed off so they can be pushed using the git amend technique above.

 [CLA FAQ]: https://www.eclipse.org/legal/clafaq.php
 [Contributor License Agreement]: https://www.eclipse.org/legal/CLA.php
 [contributing via Git]: http://wiki.eclipse.org/Development_Resources/Contributing_via_Git
 [Eclipse projects forge]: https://projects.eclipse.org/user/login/sso
 [EPL]: https://www.eclipse.org/legal/epl-v10.html
 [Jetty Contributing Patches]: https://www.eclipse.org/jetty/documentation/current/contributing-patches.html
 [project bugzilla]: https://bugs.eclipse.org/bugs/describecomponents.cgi?product=Winery
