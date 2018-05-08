# Modeling Guide

This guide presents how to model TOSCA Service Templates using Winery.

## Miscellaneous notes

Properties of a Template can be either full XML or key/value based.
If key/value based, a wrapper XML element is required.
Since QNames have to be unique, Winery proposes as namespace the namespace of the template appended by `propertiesdefinition/winery`.
The name of the wrapper element is `properties`.

<!--
Implementation hint: This is implemented in `PropertiesDefinitionComponent.onCustomKeyValuePairSelected` (TS) and `org.eclipse.winery.model.tosca.TEntityType.getWinerysPropertiesDefinition` (Java).
-->

### Uniqueness of QNames

Intentionally, a QName should be unique within the repository.
We did not follow this assumption, but only require that QNames are unique within a type.
That means, the repository allows `{http://www.example.org}id` for both a service template and a node type.
We introduced DefinitionsChildId uniquely identifying a TOSCA element.
Future versions might redesign the backend to use a QName as the unique key.


## License

Copyright (c) 2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
