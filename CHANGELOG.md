# Changelog
All notable changes to this project will be documented in this file.
This project **does not** adhere to [Semantic Versioning](http://semver.org/) and [Eclipse plugin versioning](https://wiki.eclipse.org/Version_Numbering).
This file tries to follow the conventions proposed by [keepachangelog.com](http://keepachangelog.com/).
Here, the categories "Changed" for added and changed functionality,
"Fixed" for fixed functionality, and
"Removed" for removed functionality is used.

We refer to [GitHub issues](https://github.com/eclipse/winery/issues) by using `#NUM`.

## [unreleased]

* Initial support for BPMN4TOSCA implemented using Angular

## [v2.0.0-M1] - 2017-07-03

Intermediate milestone build for the OpenTOSCA eco system.

## [v1.0.0] - not yet released

### Changed

- Adaptions required by the IP check
- Bug fixes
- Enfore `LF` line endings in the repository
- **BREAKING**: in the tosca model `SourceElement` and `TargetElement` are combined into `SourceOrTargetElement` due to serialization issues with JSON
- Add support of [XaaS Packager](http://eclipse.github.io/winery/user/XaaSPackager)
- Add support of [Splitting](http://eclipse.github.io/winery/user/Splitting)

## Initial Code Contribution - 2014-03-27

This was the initial code contribution when handing over project governance to the Eclipse Software Foundation.
See also [CQ 7916](https://dev.eclipse.org/ipzilla/show_bug.cgi?id=7916).

[unreleased]: https://github.com/eclipse/winery/compare/v2.0.0-M1...master
[v2.0.0-M1]: https://github.com/eclipse/winery/compare/initial-code-contribution...v2.0.0-M1
[v1.0.0]: https://github.com/eclipse/winery/compare/initial-code-contribution...v1.0.0
