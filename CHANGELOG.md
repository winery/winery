# Changelog

All notable changes to this project will be documented in this file.
This project **does not** adhere to [Semantic Versioning](http://semver.org/) and [Eclipse plugin versioning](https://wiki.eclipse.org/Version_Numbering).
This file tries to follow the conventions proposed by [keepachangelog.com](http://keepachangelog.com/).
Here, the categories "Changed" for added and changed functionality,
"Fixed" for fixed functionality, and
"Removed" for removed functionality is used.

We refer to [GitHub issues](https://github.com/eclipse/winery/issues) by using `#NUM`.

The ordering of v1.x and v2.x versions is sequential by time.
This is similar to [Angular's CHANGELOG.md](https://github.com/angular/angular/blob/master/CHANGELOG.md), where v4.x and v5.x is mixed.

## [unreleased]

### Changed
- Add versioning for TOSCA definitions
- Fix maven tests for junit5
- Add a subtype in the inheritance view
- Add consistency check in the UI
- Add files in the self-service for Servicetemplates
- Add error message if entered namespace contains a whitespace
- Add filtering to namespaces to filter non allowed namespaces 
- Add possibility to work with sub directories in artifact templates
- Add overwrite option in dialog for importing CSARs
- Add error notification in case of backend is not available
- Add edit properties as XML
- Support intelligent naming for IA-generation of nodetypes
- Fix href from IA generation so that it points to the artifact template in the frontend
- Fix serviceTemplate headers and set readme-component as entryPoint
- Fix lifecycle interface is now selected after click on "generate lifecycle interface"
- Add available/not available indicator for LFS in git log component
- Add ZIP-button for artifactemplate-files
- Add license and readme support for all components
- Fix delete dialog message text
- Fix popup text of upload message
- Add Git Log View to track/discard changes and create commits
- Add select boxes to select templates and target properties when adding a property mapping. Radio buttons are used to select the required template type.
- Add grouping of Nodetypes by namespace at `Nodetype->Inheritance`. The Dropdown provides a search for the wanted Nodetype.
- Fixed GroupByNamespace issue. Each tosca type has its own namespace state.
- Add templates tab to policy types and artifact types. It shows the templates of the current artifact or policy type.
- Add artifact source editor to create/upload and edit source files 
- Initial support for BPMN4TOSCA implemented using Angular
- Added initial CLI. Current funtionality: Consistency check of the repository.
- Rewrote the Backend UI using Angular
- org.eclipse.winery.model.tosca was extended with builders and some helper classes
- Add template start of namespace for the creation of tosca components
- Fixed wrong output of "CSAR Export mode. Putting XSD into CSAR" if in CSAR export mode
- New project `org.eclipse.winery.repository.rest` for separating REST resources from the backend
- Add support of [Splitting](http://eclipse.github.io/winery/user/Splitting)
- Add support of [DASpecification](http://eclipse.github.io/winery/user/DASpecification)
- Add support of configuration of git autocommit: `repository.git.autocommit=true` in `winery.properties`
- **BREAKING**: in the tosca model `SourceElement` and `TargetElement` are combined into `SourceOrTargetElement` due to serialization issues with JSON
- Add support of pattern detection for TOSCA topologies in winery 

## [v1.0.0] - not yet released

- Remove autocompletion for namespaces  

## [v2.0.0-M1] - 2017-07-03

Intermediate milestone build for the OpenTOSCA eco system.

### Changed

- Adaptions required by the IP check
- Enfore `LF` line endings in the repository
- Add support of [XaaS Packager](http://eclipse.github.io/winery/user/XaaSPackager)

## Initial Code Contribution - 2014-03-27

This was the initial code contribution when handing over project governance to the Eclipse Software Foundation.
See also [CQ 7916](https://dev.eclipse.org/ipzilla/show_bug.cgi?id=7916).

[unreleased]: https://github.com/eclipse/winery/compare/v2.0.0-M1...master
[v2.0.0-M1]: https://github.com/eclipse/winery/compare/initial-code-contribution...v2.0.0-M1
[v1.0.0]: https://github.com/eclipse/winery/compare/initial-code-contribution...v1.0.0
