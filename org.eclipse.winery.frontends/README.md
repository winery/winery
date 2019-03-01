# Winery Frontends

This project bundles all frontend components in one monorepo.
Currently, there are three components available: (i) the TOSCA Management, (ii) the Topologymodeler, (iii) the Workflowmodeler.

## TOSCA Management

The management UI to manage TOSCA Definitions and file artifacts.
It is the default UI which is started when `ng serve` or `ng serve tosca-management` is called.

However, to start the TOSCA Management UI, you can run `npm run start-tosca-management`.
The UI is then available at <localhost:4200>.

## Topologymodeler

The Topologymodeler is a graph-based modeling tool to graphically model TOSCA Topologies.
It can be run by executing the commands `npm run start-topologymodeler` or `ng serve topologymodeler`.

## Workflowmodeler

Similar to the Topologymodeler, the Workflowmodeler is a tool to graphically model BPMN4TOSCA workflows.
It can be run by executing the commands `npm run start-workflowmodeler` or `ng serve workflowmodeler`.
