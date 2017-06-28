# Changelog
All notable changes to this project will be documented in this file.
This project **does not** adhere to [Semantic Versioning](http://semver.org/).
This file tries to follow the conventions proposed by [keepachangelog.com](http://keepachangelog.com/).
Here, the categories "Changed" for added and changed functionality,
"Fixed" for fixed functionality, and
"Removed" for removed functionality is used.

We refer to [GitHub issues](https://github.com/eclipse/winery/issues) by using `#NUM`.

## Unreleased

### Changed

* SELFSERVICE-Metadata is now additionally contained under the servicetemplates path in the CSAR
* `csarName`, `version`, and `authors` are now contained in `data.xml` and `data.json`, too.
* Enfore `LF` line endings in the repository
* Add splitting functionality based on target labels
* BREAKING: in the tosca model `SourceElement` and `TargetElement` are combined into `SourceOrTargetElement` due to serialization issues with JSON

### Fixed
* Bounary definitions can be browsed for exported operations again
