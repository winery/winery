# Eclipse Winery Topology Modeler

This project is an implementation of a topology modeler for creating and editing [TOSCA] Topology Templates.

## Installation

See [README.md of Winery](../README.md).

## Project Structure

```
org.eclipse.winery.topologymodeler.ui/      // Maven submodule = root folder of this project
├── src/                                    // The codebase
├── TmDependencyTestApp/                    // Angular app that tests the NPM package
│   └── src/
└── lib/                                    // Project for building the NPM package from source
```

## NPM Package

There is an NPM package of this application that can render Topology Templates from alternative data sources.
Please refer to the [NPM repository] for further documentation on how to use it.
Always use the latest version on NPM, unless you know what you are doing.
Early versions (<= 0.6.0) of the NPM package are __not__ complete and __not__ supported.

## Development Mode

You can start the Angular-CLI development server with `ng serve`.
The application runs on `localhost:4201` in development mode by default.

## License
Copyright (c) 2017-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v2.0]: http://www.eclipse.org/legal/epl-v20.html
 [TOSCA]: https://www.oasis-open.org/committees/tosca/
 [NPM repository]: https://www.npmjs.com/package/@winery/topologymodeler