# XML editor does not enforce validation

**UserStory:** Winery offers editing the stored XML of the TOSCA definitions. What to do with validation?

## Considered Alternatives

* Winery never creates an non-schema-conforming XML. For instance, the user has to create a topology template first before he is allowed to save the service template
* Winery generate random data to gain schema-conforming XML
* Winery generates non-schema-conforming XML, but assumes that the user makes it eventually valid. In casea the user uses the XML tab, the user knows what he does. Winery forces the user to generate schema-conforming in the XML editor.
* Winery generates non-schema-conforming XML and warns the user when the user uses the XML editor. Winery does NOT force the user to generate schema-conforming XML in the XML editor.

## Decision Outcome

* *Chosen Alternative: D*
* This is in line with other editors: They allow to save, but warn if the file has compile errors, validation errors, ...

## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
