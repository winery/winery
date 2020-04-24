## Working with the Repository

![Repository Interfaces Inheritance](graphics/InheritanceOfInterfacesRelatedToTheRepository.png)  

The general idea is to seperate the repository and the repository client.
The repository itself is only accessed through the REST resources offered by `org.eclipse.winery.repository.rest`.
The repository client uses these REST resources to access content of the repository.

Programmatic access in the backend itself can be done by `RepositoryFactory.getRepository()`.
This implements `IRepository`.

### IWineryRepositoryCommon

IWineryRepositoryCommon collects all methods available both to the REST resources and the client.
Currently, deletion, renaming, retrieving a definitions and elements is offerd.

For instance, when seraching for an TArtifactTemplate, one uses following line:

    TArtifactTemplate artifactTemplate = RepositoryFactory.getRepository().getElement(id);

Thereby, `id` is the Id of the artifact template.  

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
try (InputStream is = RepositoryFactory.getRepository().newInputStream (ref)){
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

"WineryRepositoryClient" is a real client to the repository.
It implements "IWineryRepositoryClient" using the Jersey web client.
It uses a subset of the REST API to communicate with the repository.
Currently, the client has features required by the topology modeler.

## License

Copyright (c) 2013-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
