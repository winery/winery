# *Copying files from the source to the files folder*
 - *support for scripting languages do not need any processing and only need to be copied as they are*
 - *support for compiled languages need to be processed before copying*

## Considered Alternatives
* *Copying the sources as they are*
* *Require external tooling to go from source to files*

## Decision Outcome
* Chosen Alternative: *Just copying the sources as they are*
* For supporting compiled languages, it is relied on an external IDE (see [ADR-0014](0014-use-eclipse-orion-as-editor.md)).
 Thus, the only left support is for scripting languages.
 In that case, the source can be directly used as files ("binary") in an artifact template.

## Pros and Cons of the Alternatives

### Just copying the sources as they are
* `+` *Easy to implement*
* `+` *Sufficient for scripts/files that don't need compilation*
* `+` *Intuitive for the user, since the source == target*
* `-` *More User interaction required, e.g. java files must first be downloaded than compiled and than uploaded again*

### Require external tooling to go from source to files
* `+` *With one click the user can copy and compile the files*
* `-` *Needs a runtime/compiler*
* `-` *See [0014-use-eclipse-orion-as-editor](0014-use-eclipse-orion-as-editor.md)*
