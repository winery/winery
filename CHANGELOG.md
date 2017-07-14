# Changelog
All notable changes to this project will be documented in this file.
This project **does not** adhere to [Semantic Versioning](http://semver.org/).
This file tries to follow the conventions proposed by [keepachangelog.com](http://keepachangelog.com/).
Here, the categories "Changed" for added and changed functionality,
"Fixed" for fixed functionality, and
"Removed" for removed functionality is used.

We refer to [GitHub issues](https://github.com/eclipse/winery/issues) by using `#NUM`.

## [unreleased]

### Removed
* `csarname` is empty at all CSAR exports
* `data.json` is not exported any more

## [v2.0.0-M1] - 2017-07-03

### Changed

* SELFSERVICE-Metadata is now additionally contained under the servicetemplates path in the CSAR
* `csarName`, `version`, and `authors` are now contained in `data.xml` and `data.json`, too.
* Enfore `LF` line endings in the repository
* Add splitting functionality based on target labels
* **BREAKING**: in the tosca model `SourceElement` and `TargetElement` are combined into `SourceOrTargetElement` due to serialization issues with JSON
* Fix: If there are only XaaS packages without an infrastructure node defined the XaasPackager dialog  sends an undefined QName, got fixed by adding a check

### Fixed

* Boundary definitions can be browsed for exported operations again
* Relationship Type -> Visual Appearance  "Arrow" tab can opened again
* Boundary definitions -> interfaces
	*	interfaces selection is properly reloaded if new interface is added
	*	operations selection is properly reloaded if new operation is added

## Initial Code Contribution - 2014-03-27

This was the initial code contribution when handing over project governance to the Eclipse Software Foundation.
See also [CQ 7916](https://dev.eclipse.org/ipzilla/show_bug.cgi?id=7916).

[unreleased]: https://github.com/eclipse/winery/compare/v2.0.0-M1...master
[v2.0.0-M1]: https://github.com/eclipse/winery/compare/initial-code-contribution...v2.0.0-M1
