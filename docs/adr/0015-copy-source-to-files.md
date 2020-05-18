# Offer copying files from the source to the files folder

Forces:

- Source code needs to be versioned in Winery
- Support for scripting languages do not need any processing and only need to be copied as they are
- Support for compiled languages need to be processed before copying

## Considered Alternatives

* Copying the sources to files as they are
* Require external tooling to go from source to files

## Decision Outcome
* Chosen Alternative: Copying the sources as they are
* For supporting compiled languages, it is relied on an external IDE (see [ADR-0014]).
  This IDE stores the files in the "source" folder and manages the copying to the files folder.
  Thus, the only left support is for scripting languages.
  In that case, the source can be directly used as files ("binary") in an artifact template.

## Pros and Cons of the Alternatives

### Just copying the sources as they are
* `+` Easy to implement
* `+` Sufficient for scripts/files that do not need compilation
* `+` Intuitive for the user, since the source == target
* `-` More User interaction required if IDE does not support upload to files. That means, java files must first be downloaded than compiled and than uploaded to files.

### Require external tooling to go from source to files
* `+` With one click the user can copy and compile the files
* `-` Needs a runtime/compiler
* `-` See [ADR-0014]

[ADR-0014]: 0014-use-eclipse-orion-as-editor.md
