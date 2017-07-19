# XaaS Packager

## Motivation

The goal of this feature is to provide an easy way to deploy a new Deployment Artifact (e.g. web application) by reusing an existing Service Template and replacing the Deployment Artifact in the specified Node Type with the new Deployment Artifact.

## Enabling a ServiceTemplate to be reused with the Xaas Packager
To enable a Service Template to be reusable by the Xaas Packager you can either:

1. create a new Service Template according to your requirements and specifications.

2. or use an already existing Service Template that satisfies your needs.

However, in both cases you have to add the following Tags to the ServiceTemplate so the XaaS Packager can work with them:

- **xaasPackageArtifactType** indicates the type of the artifact to be wrapped.
- **xaasPackageNode** indicates the nodeTemplate INSIDE a TAGGED ServiceTemplate to determine the destination where to inject the artifact into.
- **xaasPackageDeploymentArtifact** indicates a Deployment Artifact declared at the target Node (xaasPackageNode) which will be replaced by the given artefact.

To add the necessary Tags to a ServiceTemplate the following steps have to be completed:

1. In the Service Template view (click on Service Templates on the top  of the page), choose the Service Template to be enabled to be used by the XaaS Packager.
2. Click on the *Tags* tab then click on the *add* button above the table to add new Tags to the Service Template.
3. In the add dialog that shows the following inputs:
    - **Name** specifying the name of the tag (xaasPackageArtifactType, xaasPackageNode, xaasPackageDeploymentArtifact).
    - **Value** the corresponding value for each tag.
4. After entering the desired name and value for a tag, click the add button that generates and saves the tag to the ServiceTemplate.

The steps 2-4 have to be completed for each of the XaaS Packager specific tags specified above.



For example, the input of:
- **name:** xaasPackageArtifactType, **value:** {http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}
- **name:** xaasPackageDeploymentArtifact, **value:** HelloWorldDA
- **name:** xaasPackageNode, **value:** PythonApp_2_7


 would generate the xml:

 ``` xml
<tosca:Tags> <tosca:Tag name="xaasPackageArtifactType" value="{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}ScriptArtifact"/> <tosca:Tag name="xaasPackageDeploymentArtifact" value="HelloWorldDA"/> <tosca:Tag name="xaasPackageNode" value="PythonApp_2_7"/> </tosca:Tags>
```
## XaaS Packger with Winery
The functionality of the XaaS Packager is available in the Winery application and can be used as follows:


1. click on the tab *ServiceTemplates* at the top of the page.
2. then click on the button *create from artifact* from the menu to the right.  A dialog will show up contaning the following inputs:
- **Type**: the user has to select the appropriate Artifact Type out of the available *xaasPackagerArtifactTypes*.
- **Select Artifact**: here u can upload the Deployment Artifact that will replace the existing Artifact in the specified Node Type.
- **Tags**: additional tags can be added to the Service Template.
- **NodeTypes**(optional): allows the selection of additional Node Types that MUST be used inside the selected topology.

After setting all required inputs, a click on the *add* button trigger a search for a suitable cloud topology into which the artifact can be wrapped.
