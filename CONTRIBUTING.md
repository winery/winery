(This is based on the [Jetty Contributing Patches] documentation)

# Contributing Patches

<!-- toc -->

- [Sign a Eclipse Contributor Agreement (ECA)](#sign-a-eclipse-contributor-agreement-eca)
- [Configuring Git](#configuring-git)
- [Configuring GitHub](#configuring-github)
- [Making the Commit](#making-the-commit)
- [Contributing via GitHub PullRequests](#contributing-via-github-pullrequests)
- [Background Information](#background-information)

<!-- tocstop -->

We love seeing people contribute patches to the Winery project and the process is relatively simple.
In general, we follow [GitHub's fork & pull request model](https://help.github.com/articles/fork-a-repo/).
Since we are an Eclipse project, we have requirements on commits.
These requirements are modest but very important to the Eclipse Foundation and the intellectual property of the open source project.
The following is the general process by which we operate:

* You must have a signed Eclipse Contributor Agreement.
* This agreement must be under the **same email address** as the Git pull request originates from.
* The commit must be signed.

* When the pull request is made, a git-hook will validate the email address.
  * If the result is a green checkbox then the [Winery committers](http://projects.eclipse.org/projects/soa.winery/who) can review the pull request.
  * If the result is a red X then there is absolutely nothing the Winery committers can do to accept the commit at this point.

* This may not be the final form a commit will take, there may be some back and forth and you may be asked to re-issue a pull request.

Not everything is specifically relevant since we are at GitHub but the crux of things are detailed there.
The CLA is critically important to the process.

## Sign a Eclipse Contributor Agreement (ECA)

The Eclipse Foundation has a strong Intellectual Property policy which tracks contributions in detail to ensure that:

* Did the contributor author 100% of the content?
* Does the contributor have the rights to contribute this content to Eclipse?
* Is the contribution under the project's license(s) (i.e., [EPL] and [ASL] in the case of Winery)

Thus a contributor needs to e-sign a [Contributor Agreement] regardless of how their contribution patch is provided.
For more explanation see the [Eclipse ECA FAQ].
You can familiarize yourself with the Eclipse wiki page on [contributing via Git].
In order to have a pull request accepted by any Eclipse project you must complete this agreement.

### Signing an ECA

Log into the [Eclipse projects forge] and complete the form.
Be sure to use the same email address when you create any Git commit records.
You will need to create an account with the Eclipse Foundation if you have not already done so); click on "Eclipse Contributor Agreement".

## Configuring Git

GitHub has copious amounts of quality documentation on how to interact with the system and you will minimally need to configure the `user.email` property.
Check out the [guide on GitHub](https://help.github.com/articles/setting-your-email-in-git) for more information.

Please follow <http://eclipse.github.io/winery/> to setup a git hook, which ensures that each commit contains a `Signed-off-by:` line.

## Configuring GitHub

Please ensure that the email address you use at Eclipse is the same as the "Public email" configured at https://github.com/settings/profile.

## Making the Commit

When making the commit for the pull request it is vital that you "sign-off" on the commit using `git commit -s` option.
Without this sign-off, your patch cannot be applied to the Winery repository because it will be rejected.

You can check out [the guide at GitHub](https://help.github.com/articles/signing-tags-using-gpg) for more information.

One way to think of this is that when you sign the CLA you are indicating that you are free to contribute to eclipse, but that does not mean everything you ever do can be contributed.
Using the commit signing mechanism indicates that your commit is under the auspices of your agreement.

In the case of multiple authors, plese add `Also-by: Some Bodyelse <somebodyelse@nowhere.com>` for each additional author.
For more information, see <https://www.eclipse.org/projects/handbook/#resources-commit>.

## Contributing via GitHub PullRequests

Pull requests are very much a GitHub process so best [explained by GitHub](https://help.github.com/articles/creating-a-pull-request-from-a-fork/).
See also <https://help.github.com/articles/configuring-a-remote-for-a-fork/>.

## Background Information

See the [Eclipse Legal Process Poster](http://eclipse.org/legal/EclipseLegalProcessPoster.pdf) and
[Project Code Contributions](https://www.eclipse.org/projects/handbook/#ip-project-code) in the [Eclipse Project Handbook](https://www.eclipse.org/projects/handbook/).

 [Eclipse ECA FAQ]: http://www.eclipse.org/legal/ecafaq.php
 [Contributor Agreement]: http://www.eclipse.org/legal/ECA.php
 [contributing via Git]: http://wiki.eclipse.org/Development_Resources/Contributing_via_Git
 [Eclipse projects forge]: https://projects.eclipse.org/user/login/sso
 [ASL]: http://www.apache.org/licenses/LICENSE-2.0
 [EPL]: https://www.eclipse.org/legal/epl-v10.html
 [Jetty Contributing Patches]: https://www.eclipse.org/jetty/documentation/current/contributing-patches.html
