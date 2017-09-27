# Custom URI for lifecycle interface

Winery can generate a lifecycle interface.
That interface has to take a URI for a name

## Considered Alternatives

* `http://opentosca.org/interfaces/lifecycle`
* `http://www.example.com/interfaces/lifecycle` (from http://docs.oasis-open.org/tosca/tosca-primer/v1.0/tosca-primer-v1.0.html)
* `tosca.interfaces.node.lifecycle.Standard` (from http://docs.oasis-open.org/tosca/TOSCA-Simple-Profile-YAML/v1.1/TOSCA-Simple-Profile-YAML-v1.1.html)  

## Decision Outcome

* Chosen Alternative: *`http://opentosca.org/interfaces/lifecycle`*
* There is no standardized name and we can show that this is the lifecycle, we support in the OpenTOSCA eco system
