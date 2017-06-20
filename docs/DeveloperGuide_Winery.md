---
---

# Winery Developer Guide

<!-- toc -->

- [Overview](#overview)
- [General Compilation Remarks](#general-compilation-remarks)
- [Overview on Projects](#overview-on-projects)
  * [Project org.eclipse.winery.common](#project-orgeclipsewinerycommon)
  * [Project org.eclipse.winery.highlevelrestapi](#project-orgeclipsewineryhighlevelrestapi)
  * [Project org.eclipse.winery.generators.ia](#project-orgeclipsewinerygeneratorsia)
  * [Project org.eclipse.winery.model.csar.toscametafile](#project-orgeclipsewinerymodelcsartoscametafile)
  * [Project org.eclipse.winery.model.selfservice](#project-orgeclipsewinerymodelselfservice)
  * [Project org.eclipse.winery.model.tosca](#project-orgeclipsewinerymodeltosca)
  * [Project org.eclipse.winery.repository.client](#project-orgeclipsewineryrepositoryclient)
  * [Project org.eclipse.winery.repository](#project-orgeclipsewineryrepository)
  * [Project org.eclipse.winery.topologymodeler](#project-orgeclipsewinerytopologymodeler)
- [Winery's Id System](#winerys-id-system)
  * [AdminId](#adminid)
  * [TOSCAComponentId](#toscacomponentid)
  * [Filesystem Layout](#filesystem-layout)
  * [Rest Resources](#rest-resources)
  * [URL Schema](#url-schema)
  * [Collections of Components](#collections-of-components)
  * [Component Instances](#component-instances)
  * [AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal](#abstractcomponentinstanceresourcewithnamederivedfromabstractfinal)
  * [AbstractComponentInstanceWithReferencesResource](#abstractcomponentinstancewithreferencesresource)
    + [GenericImportResource](#genericimportresource)
    + [PolicyTemplateResource](#policytemplateresource)
- [Working with the Repository](#working-with-the-repository)
  * [IWineryRepositoryCommon](#iwineryrepositorycommon)
  * [IGenericRepository](#igenericrepository)
  * [IRepository](#irepository)
  * [IWineryRepository](#iwineryrepository)
- [Shared JSPs and TAGs](#shared-jsps-and-tags)
- [Type, Template, and Artifact Management](#type-template-and-artifact-management)
- [Topology Modeler](#topology-modeler)
- [Debugging Hints](#debugging-hints)
  * [Debugging JavaScript Code](#debugging-javascript-code)
    + [Chrome](#chrome)
    + [Firefox](#firefox)
  * [Automatic Browser Refresh](#automatic-browser-refresh)
  * [Faster Redeployment](#faster-redeployment)
- [Miscellaneous Hints](#miscellaneous-hints)
  * [Generating the Right Output](#generating-the-right-output)
  * [Other Troubleshootings](#other-troubleshootings)
  * ["name" vs. "id" at Entities](#name-vs-id-at-entities)
  * [Possible Attachments of Artifacts](#possible-attachments-of-artifacts)
- [Example Repository](#example-repository)
- [Abbreviations](#abbreviations)
- [References](#references)

<!-- tocstop -->

## Overview

This document provides the design ideas of Winery.

Winery is a Web based environment to graphically model TOSCA topologies and plans managing these topologies.
It is an Eclipse project and thus support is available through its project page <https://projects.eclipse.org/projects/soa.winery>. Winery is also part of the OpenTOSCA ecosystem where more information is available at <http://www.opentosca.org>.

![Winery Components](graphics/WineryComponents.png)  

Winery consists of four parts (1) the type and template management, (2) the topology modeler, (3) the BPMN4TOSCA plan modeler, and (4) the repository.

The type, template and artifact management enables managing all TOSCA types, templates and related artifacts.
This includes node types, relationship types, policy types, artifact types, artifact templates, and artifacts such as virtual machine images.

The topology modeler enables to create service templates.
Service templates consists of instances of node types (node templates) and instances of relationship types (relationship templates).
They can be annotated with requirements and capabilities, properties, and policies.

The BPMN4TOSCA plan modeler offers web based creation of BPMN models with the TOSCA extension BPMN4TOSCA.
That means the modeler supports the BPMN elements and structures required by TOSCA plans and not the full set of BPMN [KBBL12].
This part is currently in development and not part of the opensourced code of Winery.

The repository stores TOSCA models and allows managing their content. For instance, node types, policy types, and artifact templates are managed by the repository.
The repository is also responsible for importing and  exporting CSARs, the exchange format of TOSCA files and related artifacts.

## General Compilation Remarks

Winery is built using Maven.
The JavaScript libraries are fetched using bower.
More information is contained in the README.md

## Overview on Projects

This section provides a short overview on all projects Winery consists of.

### Project org.eclipse.winery.common

This project contains classes used by the repository and topology modeler projects. It contains constants, the id
system, interface definitions for repository access and the datatype to manage Winery's extended key/value properties.

### Project org.eclipse.winery.highlevelrestapi

This project contains an API to communicate with REST services. It provides an abstraction layer above Apache
Commons HTTPClient. It is used by projects generated by the IA generator to upload compiled 
implementations as implementation artifacts.

### Project org.eclipse.winery.generators.ia

This project contains the generator which generates a NodeType implementation based on a NodeType
interface.

### Project org.eclipse.winery.model.csar.toscametafile

This project contains the model for the file "Tosca.meta".

### Project org.eclipse.winery.model.selfservice

This project contains the model for the self service portal.  
It is used by the Vinothek [BBKL14] to display user defined data.

### Project org.eclipse.winery.model.tosca

This project contains a JAX B generated model of the XSD of OASIS TOSCA v1.0. The XSD hat to be modified to
enable proper referencing and use. An Implementation Artifactmay carry a "name" attribute. The contents of
properties of Boundary Definitions are processed in "lax" mode.

### Project org.eclipse.winery.repository.client

Whis project hosts a client using the REST API of the repository and offering a Java object based client to the
Winery repository.

### Project org.eclipse.winery.repository

This is the heart of Winery. This project hosts the repository, where all entities of TOSCA are stored and
managed. It realizes the components "Type, Template, and Artifact Management" and "Repository" (Figure 1).

### Project org.eclipse.winery.topologymodeler

This part of Winery enables modeling topologies graphically. It realizes the component "Topology Modeler"
(Figure 1).

## Winery's Id System

The general idea behind the storage of Winery is that each entity comes with an id. The id is either self
contained or references a parent id, where the element is nested in. All Ids inherit from GenericId.  
Figure 2 shows the inheritance hierarchy of GenericId. The child "AdminId" is used for all administrative elements
required for internal management. "DummyParentForGeneratedXSDRef" is required during the export of 
generated XML Schema Definitions due to the use of Winery's key/value properties. "TOSCAComponentId" is
the parent element for all TOSCA Elements which may be defined directly as child of a "Definitions" element.
All other elements have "TOSCAElementId" as parent.

<center>

![GenericId Hierarchy](graphics/GenericIdHierarchy.png)  
**Figure 2: Inheritance hierarchy of GenericId**

</center> 

### AdminId

Figure 3 shows the inheritance hierarchy of AdminId. "NamespacesId" is used as container for a mapping file
from namespace prefixes to namespaces. "TypesId" is the parent element of all types user can set. This are 
not node types etc., but ConstraintTypes (for Constraints), PlanLanguages (for plans), and PlanTypes (for plans).
The inclusion of "PlanLanguages" is due to reuse of the class AbstractTypesManager for plan languages. TOSCA
does not restrict these enumerations. Therefore, Winery manages all known types for itself.

<center>

![AdminId Hierarchy](graphics/AdminIdHierarchy.png)  
**Figure 3: Inheritance hierarchy of AdminId**

</center>

### TOSCAComponentId

This Id class is used for all entities directly nested in a TDefinitions element. They all have a namespace and an
id attribute. This is ensured by ToscaComponentId. Figure 4 shows the inheritance hierarchy for TOSCAComponentId.

<center>

![ComponentId Hierarchy](graphics/ComponentIdHierarchy.png)  
**Figure 4: inheritance hierarchy of ToscaComponentId**

</center>

"EntityTemplateId" collects all Entity Templates directly nested in a Definitions element. As a result, the ids of
NodeTemplates or RelationshipTemplates do not inherit from EntityTemplateId. They are contained in a Service Template
and not directly in the Definitions element. Thus, the only children of EntityTemplateId are ArtifactTemplateId,
PolicyTemplateId, and ServiceTemplateId.

"EntityTypeId" collects all Entity Types directly nested in a TDefinitions element.These are IDs for ArtifactTypes,
CapabilityTypes, PolicyTypes, RequirementTypes, NodeTypes and RelationshipTypes. Node Types and RelationshipTypes
have the direct parent "TopologyGraphElementTypeId" as these two Types form the types of components of the topology graph.

"EntityTypeImplementationId" is the parent id for NodeTypeImplementationId and RelationshipTypeImplementationId and thus
subsumes the two possible entities which can be implementations.

"GenericImportId" is an artificial entity. It is used to be able to store imports of an imported CSAR. These
imports might be XSD definitions, vut als WSDL files.

### Filesystem Layout

The general structure is ROOT/<componenttype>s/<encoded-namespace>/<encoded-id>/<resource-specific-part>.
Encoding is done following RFC 3986. This makes the structure to the URL structure (cf. Section 7).

The resource-specific part typically is a file named <componenttype>.tosca . It contains the Definitions
XML file where all the data is stored. Files may be added to artifact templates. Therefore, a subdirectory "files"
is created in ROOT/artifacttemplates/<encoded-namespace>/<encoded-id>/. There, the files are stored.

For instance, the NodeType "NT1" in the namespace "http://www.example.com/NodeTypes" is found behind the URL
"nodetypes/http%3A%2F%2Fexample.com%2FNodeTypes/NT1/". As the browser decodes the URL, the namespace and the
id are double encoded. The content of the Definitions is stored in "NodeType.tosca".

The URL encoding is necessary as some letter allowed in namespaces (e.g. ".", ":", ";", "/") and IDs are not allowed
on all operating systems. IDs are NCNames, which are based on XML 1.0 Names, which in turn allows nearly all
unicode characters. Therefore, each namespace and ID is URLencoded when written to the filesystem and URLdecoded
when read from the filesystem.

Figure 5 shows the root directory of the filesystem and the directory layout for the NodeType NT1.

<center>

![Filesystem Directory Layout](graphics/FilesystemDirectoryLayout.png)  
**Figure 5: Filesystem directory layout**


### Rest Resources

All resources are implemented in classes in the package org.eclipse.winery.repository.resources. We call all
elements directly nested in the definitions element "components". They are implemented using JAX RS 1.1 
using Jersey 1.17

The full set the API is used by the Type, Template, and Artifact Management UI (see Section 10). A subset of the
API is used at IWineryRepository (see Section 8.4).

### URL Schema

The idea behind the URL schema may shortly describes by ROOT/<componenttype>s/<double-encoded-namespace>/<double-encoded-id>/<resource-specific-part>,
which makes the structure similar to the file system (cf. Section 6). Encoding is done following RFC 3986. An online
URL-encoder may be found at: http://www.albinoresearch.com/misc/urlencode.php .

For instance, the NodeType "NT1" in the namespace "http://www.example.com/NodeTypes" is found behind the URL "nodetypes/http%253A%252F%252Fexample.com%252FNodeTypes/NT1/".
As the browser decodes the URL, the namespace and the id are double encoded. note the additional encoding of the symbol "%" in
comparison to the encoding at the filesystem (see Section 6).

The part until "<componenttype>s"is realized by "AbstractComponentsResource" and its subclasses (see Section 7.2).
The resource specific part is realized by subclasses of AbstractComponentInstanceResource (see Section 7.3).

### Collections of Components

<center>

![AbstractCompoenentResource Inheritance](graphics/InheritanceOfAbstractComponentResource.png)  
**Figure 6: Inheritance of AbstractComponentResource**

</center>

Figure 6 shows the inheritance of AbstractComponentsResource. It contains an intermediate class
"AbstractComponentsWithTypeReferenceResource" which handles a POST with an additional type. It is used at
all components which have a type associated. These are artifact templates, node type implementations,
relationship type implementations and policy templates.

All logic is implemented in AbstractComponentsRessource. it handles creation of resources (using POST) and
creation of AbstractComponentInstanceResources.

### Component Instances

<center>

![AbstractComponentInstanceResource Inheritance](graphics/InheritanceOfAbstractComponentInstanceResource.png)  
**Figure 7: Inheritance of AbstractComponentInstanceResource**

</center>

Figure 7 shows the inheritance of AbstractComponentInstanceResource. For each component, a class exists.
Using Intermediate classes, common properties are handled. These are explained in the following sections.

### AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal

Several component instances have the attributes "name", "dervidedFrom", "abstract", and "final". These are
handled in the class "AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal". In this group,
type implementations, namely node type implementations and relationship type implementations can be found.

Furthermore, type resources exist. These are grouped by the "EntityTypeResource". Within the context of this class,
"TopologyGraphElementEntityTypeResource" is introduced. This class groups together "NodeTypeResource" and "RelationshipTypeResource".

### AbstractComponentInstanceWithReferencesResource

This class groups together classes with file references. Artifact Templates may reference files and a Service
Template may reference plans. The user can copy files manually to the right place in the directory structure of
the repository. By calling the method "synchronizeReferences()", the referenced stores in the XML are
synchronized with the actually existing files. This is done whenever the XML is retrieved from the repository.

#### GenericImportResource

This class is used to handle different import types. Currently only CSD is supported. The class is
"XSDImportResource".

#### PolicyTemplateResource

This class implements the resource for a policy template. Since a policy template does not contain any external
file references, it is not modeled as child of "AbstractComponentInstanceWithReferencesResource".

## Working with the Repository

Figure 8 shows all interfaces related to interactions with the repository. The general idea is to seperate the
repository and the repository client. The repository itself is only accessed through the REST resources offered
by org.eclipse.winery.repository. The repository client uses these REST resources to access content of the repository.

<center>

![Repository Interfaces Inheritance](graphics/InheritanceOfInterfacesRelatedToTheRepository.png)  
**Figure 8: Inheritance of interfaces related to the repository**

</center>
### IWineryRepositoryCommon

IWineryRepositoryCommon collects all methods available both to the REST resources and the client. Currently,
only "forceDelete(GenericId)" is offered.

### IGenericRepository

IGenericRepository offers methods to access the content of the repository through the ids described in
Section 5. The idea is that the id points to a directory containing multiple files associated to the id.
Typically, a file with the extension "Definitions" is loaded. in the case of a NodeType, the file is called "NoteType.tosca".
To access that file, an instance of the class "RepositoryFileReference" is required. It is constructed by
"RepositoryFileReference(GenericId parent, String fileName)". The REST resources use then
"InputStream new InputStream(RepositoryFileReference ref) throws IOException" to access the content of a stored file.

Example:

```java

NodeTypeId id = new NodeTypeId("http://www.example.com/NodeTypes", "NT1", false);
RepositoryFileReference ref = new RepositoryFileReference(id, "NodeType.tosca");
try (InputStream is = Repository.Instance.newInputStream (ref)){
  // do something
}

```

In the real code, determining the reference for an Id is encapsulated in BackendUtils in the method
"getRefOfDefinitions".

### IRepository

IRepository additionally includes methods for working with configuration files. They are currently used in the
Admin part of Winery, where, for instance, namespaces are managed.

The implementation of IRepository is done in the classes Abstract Repository, FilebasedRepository, and 
GitBasedRepository. Both the FilebasedRepository and the GitBasedRepository work on the filesystem. The
GitBasedRepository additionally allows to set the revision to a given revision or to clean and reset the
repository.

Future extensions will allow a JClouds based file storage.

### IWineryRepository

This interface offers methods to get Java objects from a remote repository. For instance, the method
"<T extends TEntityType> T getType(QName qname, Class<T> type)" may be used to retrieve an EntityType
object out of a QName.

"IWineryRepositoryClient" offers methods to add repository URLs.

"WineryRepositoryClient" is a real client to the repository. It implements "IWineryRepositoryClient" using the
Jersey 1.1. web client. it uses a subset of the REST API to communicate with the repository. Currently, the client
has features required by the topology modeler.

## Shared JSPs and TAGs

In the "generate-sources" Maven phase of the repository, shared jsps and tags are copied from the topology modeler.

Figure 9 shows the shared jsps. Currently, it is only one JSP. the "dialog.jsp" is used for Yes/No dialogs.

Figure 10 shows the shared tags "orioneditor.tag" is a wrapper for an Orion based editing area. Orion
( https://www.eclipse.org/orion ) is a web-based IDE by the Eclipse Software Foundation. The tags in the
"policies" folder are used for creating and rendering policies. The tags in the "templates" folder implement
functionality for all entity templates such as artifact templates or node templates. Node templates may carry
requirements and capabilities. The respective tags are contained in the "reqscaps" folder.

<center>

![Shared JSP Files](graphics/SharedJSPFiles.png)  
**Figure 9: Shared JSP files**

</center>

<center>

![Shared Tags](graphics/SharedTags.png)  
**Figure 10: Shared Tags**

</center>

## Type, Template, and Artifact Management

The REST resources offer the method getHTML, which returns a HTML page, when *text/html* is requested.
It uses JSPs to generate the requested page. Figure 11 shows the structure of the available jsps. They are sorted
according the different entity types available in TOSCA.

<center>

![JSP Structure](graphics/JSPStructure.png)  
**Figure 11: JSP structure**

</center>

Figure 12 shows the rendered result for the instance states of a node type. The URL used is
nodetypes/http%253A%252F%252Fexample.com%252FNodeTypes/NT1/#instancestates. A GET with accept
text/html on the resource nodetypes/http%253A%252F%252Fexample.com%252FNodeTypes/NT1/instancestates
leads to a processing of the GET request at org.eclipse.winery.repository.resources.entitytypes.InstanceStateResource(getHTML()).
This renders/jsp/entitytypes/instancestates.jsp. A click on the "Add" button will result on a POST on the
InstanceStateResource. After a HTTP 304, the instance state is inserted in the table by the client side-
JavaScript.

The general idea is to have the content of the fragment identifier rendered by a separate resource. The switch
functionality is implemented in "hashloading.jsp". At each change of the fragment identifier, the respective
URL is constricted and the content of the div containing the tab content is replaced by the response of the server.

<center>

![Nodetypes Rendering](graphics/WinerysRenderingofNodetypes.png)  
**Figure 12 Winery's rendering of nodetypes/http%253A%252F%252Fexample.com%252FNodeTypes/NT1/#instancestates**

</center>

## Topology Modeler

The main file of the topology modeler is the "index.jsp". It uses embedded Java code to connect to the repository.
Thereby, the interface IWineryRepositoryClient (see Section 6) is used. Afterwards, the whole UI is rendered in
*div id="winery"*.

Figure 13 shows a screenshot of the topology modeler. the buttons in the first row are directly contained in
index.jsp. The palette on the left side is rendered by "palette.tag". The topology in the middle is rendered by a
for-loop in index JSP. This loop iterates over all node templates and uses "nodeTemplateRenderer.tag" to
render a single node template. The relationship templates are drawn using "tmpl:registerConnectionTypesAndConnectNodeTemplates",
whereby "tmpl" is bound to "/WEB-INF/tags/common/templates". the property on the right side is rendered by 
"propertiesOfOneNodeTemplate.tag" for node templates and by "propertiesOfOneRelationshipTemplate.tag" for relationship templates.

<center>

![Topology Modeler](graphics/WinerysTopologyModeler.png)  
**Figure 13: Winery's topology modeler**

</center>

## Debugging Hints

### Debugging JavaScript Code

#### Chrome

Press f12 to open the debug console

#### Firefox

Use Firebug ( https://getfirebug.com ) It offers more possibilities than the built in console.

### Automatic Browser Refresh

One can use the browser extension LiveReload ( http://www.livereload.com ) to enable reloading of pages after a change.
Set *workspaces\valesca\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\winery* as directory, use
http://localhost:8080/winery as URL, and enable LiveReload in the browser.

### Faster Redeployment

It takes a few seconds until the whole application is redeployed. You can use JRebel ( http://www.jrebel.com )
for hot code replacement in the Tomcat in Eclipse.

## Miscellaneous Hints

### Generating the Right Output

*	If necessary, set the content type of the JSP: <%@page contentType="image/svg+xml; charset=utf-8" %>
  *		Otherwise, answer is plain text (and not XML)

*	XML documents have to contain the header <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
  *		standalone=yes means that there is no external DTD
  *		eleminates parsing errors in firefox

### Other Troubleshootings

When “The superclass "javax.servlet.jsp.tagext.SimpleTagSupport" was not found on the Java Build Path.”
appears, right click on the project then **Properties**, **Project Facets** and finally **Runtime**.
There, select the Apache Tomcat Runtime. Click "**Apply**", then "**OK**".

When running in the jetty 9.0.5, there is always the error message "Request Entity Too Large" when uploading a file.
There is the maxFormContentSize set in jetty web.xml, but it currently does not help to solve this issue.

If the Prefs class is not found, something is wrong with the libraries, tomcat config, eclipse environment (check
the build path!), etc.

### "name" vs. "id" at Entities

Some entities carry a name, some an id and some both. A justification is available at TOSCA issue 47
( https://issues.oasis-open.org/browse/TOSCA-47 ).

### Possible Attachments of Artifacts

Implementation Artifacts (IAs) may be attached at

* NodeType/Interfaces/Interface/Operation (via IA's operation attribute)
* NodeType/Interfaces/Interface/Operation
* NodeTemplate

Deployment Artifacts (DAs) may be attached at
*NodeType
*NodeTemplate
	
## Example Repository

An example Repository is available at
https://github.com/OpenTOSCA/OpenTOSCA.github.io/blob/master/third-party/winery-repository.zip .
One can import the repository by *Administration*, then *Repository* and finally *Import repository*.

## Abbreviations

|       |                                                                       |
|-------|-----------------------------------------------------------------------|
| BPMN  | Buisness Process Model and Notation                                   |
| TOSCA | OASIS Topology and Orchestration Specification for Cloud Applications |

## References

[BBKL14] Breitenbücher, Uwe; Binz, Tobias; Kopp, Oliver; Leymann, Frank: Vinothek - A Self-Service Portal for TOSCA. In: Herzberg, Nico (Hrsg); Kunze, Matthias (Hrsg): Proceedings of the 6th Central-European Workshop on Services and their Composition (ZEUS 2014).

[KBBL12] Kopp, Oliver; Binz, Tobias; Breitenbücher, Uwe; Leymann, Frank: BPMN4TOSCA: A Domain-Specific Language to Model Management Plans for Composite Applications. In: Mendling, Jan (Hrsg); Weidlich, Matthias (Hrsg): 4th International Workshop on the Business Process Model and Notation, 2012
