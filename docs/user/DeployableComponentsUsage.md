# Deployable Components from dockerfiles project

The Deployable Components projects enables the detection and extraction of 
deployment components and their topologies from dockerfiles.

## Functionality

### Crawler

The crawler can be used to gather dockerfiles from different services. Currently GitHub is supported.
In addition, all crawled dockerfiles are stored locally and can be used later. Additional crawler for 
other services, which offer dockerfiles, can be added by extending the crawler interface.

### Analyzer

The analyzer extracts installation commands of software components out of the dockerfiles. It uses
the syntax of installation packages like apt-get or pip. Therefore it is not generic, but needs information
about every supported command package. Seven command packages are currently supported:
apk, apt-get, chmod, npm, pip, pip3, yum. Additional installation packages can be added by extending
the CommandAnalyzer interface and adding it to the switch-statement in the Fileanalyzer.

## Usage

A DeployableComponents object needs to be created with a crawler type and corresponding authentication
data. In case of the GitHub crawler the account name and an api-token, which can be created on the GitHub
website, is needed. The crawled service may have an order of their content, for which a starting point
can be defined. In case of GitHub all repositories are numbered.

On the DeployableComponent object the extraction task can be started. This should be done in an
own thread since it never terminates by it's own. It must be stopped from outside. After stop is called,
the thread terminates after the next discovered dockerfile. That means, that the example below may
miss the last crawled dockerfile, because it immediately requests the results. Intermediate results can be
requested during the execution.

The results are in a hash map, which contains a base component and at least one top component, which
builds upon the base component in the component topology. A top component has a count of the occurrences.
The results can be converted into tosca nodes. They have capabilities and requirements, which define
the topology between components.

Two config values can be adjusted in the DeployableComponents class: CRAWL_AT_ONCE defines, how many
dockerfiles the crawler should crawl at once. A higher number slightly improves the performance,
but it increases the time until intermediate results are available and until the execution thread terminates
after stop is called. MAX_FAILED_CRAWLER_REQUESTS defines, how many retries are performed by the
crawler, before a request is cancelled.

### Example

    DeployableComponents comp = new DeployableComponents(CrawlerType.GITHUB, loginName, loginToken, localCopyPath);
    comp.setCrawlPoint(randomNumber);
    
    Runnable extractionTask = () -> {
        comp.start();
    };
    
    Thread extractionThread = new Thread(extractionTask);
    extractionThread.start();

    try {
        Thread.sleep(3600000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    comp.stop();

    HashMap<Component, List<Pair<Component, Integer>>> results = comp.getFoundComponents();
    List<TNodeType> resultsTosca = comp.asToscaModel();

## License

Copyright (c) 2019 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
