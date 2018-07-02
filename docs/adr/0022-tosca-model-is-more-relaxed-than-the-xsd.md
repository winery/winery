# tosca.model is more relaxed than the XSD

* Status: Accepted
* Date: 2018-06-06

## Context and Problem Statement

There is a data model for a) serializing/deserializing the XML contents, b) internal backend handling, c) working with algorithms, d) communicating with the REST service.
Currently, this is the same model.
The UI might generate non-valid XML files (in the sence of not passing the XSD validation).
For instance, if a user creates a service template, that service template does not contain a topology template.
Furthermore, a topolgoy template needs to have at least one node template.

## Considered Options

* Keep one model and allow non-XSD validating models in `org.eclipse.winery.model.tosca`
* Only allow (XSD-) validating models
* Develop two models

## Decision Outcome

Chosen option: "Keep one model and allow non-XSD validating models in `org.eclipse.winery.model.tosca`", because 

- XSD is meant for "executable" TOSCA definitions, not for intermediate modeling results
- currently too much effort to develop two models  

## License

Copyright (c) 2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
