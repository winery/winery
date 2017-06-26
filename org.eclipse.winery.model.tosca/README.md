# org.eclipse.winery.model.tosca

This implements [Topology and Orchestration Specification for Cloud Applications Version 1.0](http://docs.oasis-open.org/tosca/TOSCA/v1.0/os/TOSCA-v1.0-os.html) with extensions by the University of Stuttgart and others.

The java classes were generated out of TOSCA-v1.0.xsd.

## Breaking changes in the XSD

TOSCA-v1.0.xsd was adapted to carry a "name" at implementation artifacts.
This is necessary to identify a single implementation artifact in a list of implementation artifacts.

See https://github.com/eclipse/winery/issues/71 for a discussion.
