# DA Specification for Driver Injection to Resolve abstract DAs

## DA Specification for Driver Injection Functionality

In addition to the Matching functionality with the DA Specification functionality DAs which come with the injected
Stacks can be used to resolve abstract DAs in the completed topology.
The idea is to enable the specification of concrete DAs depending on the injected stack, for example the middleware.
A concrete application is the injection of middleware which comes with its one communication drivers required by
applications which want to communicate with the respective middleware.
In this case the application (which are already part of the topology) get a DA of an abstract artifact type.
The middleware has a DA of a concrete artifact type which is derived from the abstract type.
The abstract type is recognized and in the stack to which the application is connected a suitable concrete DA is searched.

As a result, arbitrary middleware can be injected and the respective driver is used for the communication.

## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v1.0]
and the [Apache License v2.0] which both accompany this distribution.

  [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
  [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
