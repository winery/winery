# No support for local git source clones

A user wants to edit source files locally in his favourite IDE.
Therefore, he wants to use the usual ways to retrieve source files.
Typically, this is a `git clone` from a git repository having the respective source files.

A user does not want to clone the whole winery repository, as this might a) be too large b) not focused enough.
It would be beneficial to have the source of an artifact template available as git checkout.

The source files of an artifact implementation are currently directly editable in the winery once they are uploaded. 
The only way to edit sources locally is to download and upload them again.
The solution for the user should be:
- easy to use
- scalable in terms of storage required in Winery's repository


## Considered Alternatives

* No support for local clones
* Git repositories as submodules
* Using filter-branch (https://help.github.com/articles/splitting-a-subfolder-out-into-a-new-repository/)
* Using git sparse-checkout to create a local clone (https://gist.github.com/sumardi/5559896)

## Conclusion

* Chosen Alternative: no support for local edit

Since all alternatives require either too many additional git repositories or are very inconvenient to apply for the user,
we decided to not support any clone/push functionality.


## Pros and Cons of the Alternatives 

### No support for local edit
* `+` no changes needed
* `-` no local edit support

### Git repositories as submodules
* `+` simple git cloning possible
* `+` additional repositories can be cloned into winery-repository as submodules
* `+` separate version history
* `-` one repository for each implementation
* `-` each separate repository has to be created on the git remote of winery (e.g., GitHub)

### Using filter-branch on sever's side
* `+` no changes needed in the existing repositories
* `+` `git filter-branch --prune-empty --subdirectory-filter` allows to skip any subdirectory
* `-` server needs to execute very large filter commands for each user for each requested artifact template
* `-` the mapping back from the filtered repository to the full repository is cumbersome.
* `-` merge conflicts are not resolved by git tooling automatically

### Using filter-branch on user's side
* `+` no changes needed in the existing repositories
* `+` `git filter-branch --prune-empty --subdirectory-filter` allows to skip any subdirectory
* `-` user needs to execute very large filter commands
* `-` the mapping back from the filtered repository to the full repository is cumbersome.
* `-` requires the user to type the commands manually

### git sparse checkout
* `+` no changes needed in the existing repositories
* `-` requires the user to type the commands manually


## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. Made available under the terms of the [Eclipse Public License v2.0] and the [Apache License v2.0] which both accompany this distribution.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v2.0]: http://www.eclipse.org/legal/epl-v20.html
