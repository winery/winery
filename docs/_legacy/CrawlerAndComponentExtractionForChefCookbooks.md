# Extract Deployable Components from Chef Cookbooks

The crawler project for chef cookbooks enables the detection and extraction of 
deployment components and their topologies from chef cookbooks.

## Functionality

### Crawler

The crawler can be used to gather chef cookbooks from different services. Currently the Chef Supermarket
is supported. In addition, all crawled chef cookbooks are stored locally and can be used later. The crawler
can crawl the cookbooks by series or in parallel.

### Analyzer

The analyzer extracts installation commands of software components out of the chef cookbooks. It uses
the syntax of installation packages package or windows_package. Currently there are supported all chef
resources for installing software components. Software components which are installed by embedded scripts
are not supported at the moment. The required additional logic can be added by extending the Chef-Dsl Parser
The extracted components from the chef cookbooks are mapped to TOSCA nodes with its requirements and capabilities.

## License

Copyright (c) 2019 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
