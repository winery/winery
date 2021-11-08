# Reflection test for TOSCA YAML builder

The TOSCA YAML builder converts Java Objects to instances of TOSCA YAML classes. To get clean an good instances validation is needed. Reflection test are Junit5 test which take yaml service templates with metadata that describes what assertions should be made for the resulting TOSCA YAML class instances. 

```
...
metadata:
  assert: |
    repositories.rp1.url = http://github.com/kleinech
    node_types.ntp1.requirements.0.rqr1.capability = cbt1
...
```
Each assert line contains a keyname and a value.
*[context and problem statement]*
*[decision drivers | forces]* <!-- optional -->

## Considered Alternatives

* *reflection tests*
* *manual test*

## Decision Outcome

* Chosen Alternative: *reflection tests*
* Only alternative, which meets simplifies the effort to make complete tests 
## Pros and Cons of the Alternatives <!-- optional -->

### *reflection tests*
A reflection tests contains all information that is needed in the metadata of the test file.

Example:
```
tosca_definitions_version: tosca_simple_yaml_1_3

metadata:
  description: This test contains a valid service template (Not Complete)
  targetNamespace: http://www.example.org/ns/simple/yaml/1.3/test
  tosca.version: 1.1
  exception: None
  assert-typeof: |
      repositories.rp1 = TRepositoryDefinition 
  assert: |
    metadata.description = This test contains a valid service template (Not Complete)
    metadata.targetNamespace = http://www.example.org/ns/simple/yaml/1.3/test
    description = Description of service template
    repositories.rp1.url = http://github.com/kleinech

description: Description of service template

repositories:
  rp1: http://github.com/kleinech
```
The example above is converted from the yaml representation to the TOSCA YAML data model 
and the result is has one `typeof` assert and four `value` asserts. 

If one assert fails the test for this file fails but all other asserts are tested too.   


## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
