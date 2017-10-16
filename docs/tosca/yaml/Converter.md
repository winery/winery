# TOSCA YAML Converter
## DataTypes and Property Definition Conversions
### From YAML to XML
TOSCA YAML defines `TDataTypes` and `TPropertyDefinition`
```yaml
DataTypeName:
  derived_from: DataTypeName2
  version: 1.0.0
  metadata:
    description: Examlpe Data Type
    author: kleinech
  properties: 
    name:
      type: string
    ageSpecial:
      type: otherDataType
  constraints: <ConstraintClause>*
  
PropertyDefinition:
  type: typeName (e.g. string, other DataTypeName)
  required: true|false
  default: object
  status: experimental
  constraints: <ConstraintClause>*
  entry_schema: <EntrySchema>
```
Each data type is converted to an XML schema with complex types, each PropertyDefinition for other EntityType (excluding DataType) is converted to an XML schema with an element containing a complex type and importing if necessary other schemas. 

Simple Example:
```yaml
...
node_types:
  DockerEngine:
    properties:
      DockerEngineURL:
        type: string
      DockerEngineCertificate:
        type: string
```
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" attributeFormDefault="unqualified" elementFormDefault="qualified" targetNamespace="http://www.example.com/NodeTypes" xmlns="http://www.w3.org/2001/XMLSchema">
    <element name="DockerEngine_Properties">
        <complexType>
            <sequence xmlns:pfx0="http://www.example.com/NodeTypes">
                <element name="DockerEngineURL" type="xsd:string"/>
                <element name="DockerEngineCertificate" type="xsd:string"/>
            </sequence>
        </complexType>
    </element>
</schema>
```

Complex Example
```yaml
...
imports:
  - data_types:
      file: /data_types.yml
      namespace_uri: http://www.example.com/DataTypesTest
      namespace_prefix: imp1

node_types:
  example.NodeType:
    properties:
      example.p1:
        type: string
      example.p2:
        type: imp1:example.com.Compose
...
################ data_types.yml #################
tosca_definitions_version: tosca_simple_yaml_1_1

data_types:
  example.com.Number:
    properties:
      prefix:
        type: integer
      suffix:
        type: integer
  example.com.Compose:
    properties:
      compose:
        type: example.com.Number
      addition:
        type: string
```
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<schema xmlns:imp1="http://www.example.com/DataTypesTest" xmlns:xsd="http://www.w3.org/2001/XMLSchema" attributeFormDefault="unqualified" elementFormDefault="qualified" targetNamespace="http://www.example.com/NodeTemplateUsingDataType" xmlns="http://www.w3.org/2001/XMLSchema">
    <import namespace="http://www.example.com/DataTypesTest" schemaLocation="http%3A%2F%2Fwww.example.com%2FDataTypesTest.xsd"/>
    <element name="example.NodeType_Properties">
        <complexType>
            <sequence xmlns:pfx0="http://www.example.com/NodeTemplateUsingDataType">
                <element name="example.p1" type="xsd:string"/>
                <element name="example.p2" type="imp1:example.com.Compose"/>
            </sequence>
        </complexType>
    </element>
</schema>

<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" attributeFormDefault="unqualified" elementFormDefault="qualified" targetNamespace="http://www.example.com/DataTypesTest" xmlns="http://www.w3.org/2001/XMLSchema">
    <complexType name="example.com.Number">
        <sequence xmlns:pfx0="http://www.example.com/DataTypesTest">
            <element name="prefix" type="xsd:decimal"/>
            <element name="suffix" type="xsd:decimal"/>
        </sequence>
    </complexType>
    <complexType name="example.com.Compose">
        <sequence xmlns:pfx0="http://www.example.com/DataTypesTest">
            <element name="compose" type="pfx0:example.com.Number"/>
            <element name="addition" type="xsd:string"/>
        </sequence>
    </complexType>
</schema>

```
### From XML to YAML
Only Winery PropertiesKV are converted to TOSCA YAML Properties. XML schemas are ignored so far.
 
## TOSCA XML ServiceTemplate Boundaries and TOSCA YAML TopologyTemplate definition SubstitutionMappings
  
### From YAML to XML
### From XML to YAML
```xml
<BoundaryDefinitions>
  <Properties>
    <PropertyMappings>
      <serviceTemplatePropertyRef>XPath</serviceTemplatePropertyRef>
      <targetObjectRef>
        NodeTemplate |
        Requirement of NodeTemplate | 
        Capability of NodeTemplate | 
        RelationshipTemplate
      </targetObjectRef>
      <targetPropertyRef>XPath</targetPropertyRef>
    </PropertyMappings>
  </Properties>
  <PropertyConstraints>
    <PropertyConstraint>
      <property>XPath</property>
      <constraintType>URI</constraintType>
      <body>any</body>
    </PropertyConstraint>
  </PropertyConstraints>
  <Reuquirements>
    <Requirement name="NAME" ref="Requirement element of NodeTemplate within ServiceTemplate"></Requirement>
  </Reuquirements>
  <Capabilities> 
    <Capability name="NAME" ref="Capability element of NodeTemplate within ServiceTemplate"></Capability>
  </Capabilities>
  <Policies>
    <Policy name="NAME" policyType="type of Policy" policyRef="QName of PolicyTemplate"></Policy>
  </Policies>
  <Interfaces>
    <Interface name="NAME">
      <Opertaion name="NAME">
        <NodeOperation nodeRef="NodeTemplate" interfaceName="name of Interface" operationName="name of Operation"></NodeOperation>
        <RelationshippOperation interfaceName="" operationName=""></RelationshippOperation>
        <Plan planRef=""></Plan>
      </Opertaion>  
    </Interface>
  </Interfaces>
</BoundaryDefinitions>
```

```yaml
TSubstitutionMappings:
  nodeType: QName
  capabilities: 
    capability:*
      key: String
      value: String*
  requirements:
    requirement:*
      key: String
      value: String
```

## TOSCA YAML Relationship Templates
Compared to TOSCA XML Relationship Templates TOSCA YAML Relationship Templates have no field for `sourceElement` and `targetElement`.

TOSCA XML Relationship Template
```yaml
RelationshipTemplate:
  id: string
  name: string
  type: relationshipTypeQName
  Properties: any
  PropertyConstraints: ..
  SourceElement:
    ref: idref
  TargetElement:
    ref: idref
  RelationshipConstraints: ..
```

TOSCA YAML Relationship Template
```yaml
relationshipTemplateName:
  type: relationshipTypeQName
  description: string
  metadata: string*
  properties: propertyAssignment*
  attributes: attributeAssignment*
  interfaces: interfaceDefinition*
  copy: relationshipTemplateName
```

TOSCA YAML NodeType.Requirement
```yaml
requirementDefinitionName:
  capability: capabilityTypeQName
  node: nodeTypeQName
  relationship: relationshipTypeQName
```

TOSCA YAML specifies the source target relation not in Relationship Templates and not for Templates rather for Types.

TOSCA YAML Node Templates specify requirements and capability. If the requirement of one Node Template is matched by the capability of an other Node Template and the requirement specifies a relationship then a Relationship Type is constructed.


## Artifacts in TOSCA YAML
   
TOSCA XML and TOSCA YAML define artifacts at different TOSCA components. The following sections explain how artifacts are converted between TOSCA XML and TOSCA YAML.
   
### TOSCA XML NodeTypeImplementation.ImplementationArtifact
TOSCA YAML has no component NodeTypeImplementation and TOSCA YAML NodeTypes do not define ArtifactsDefinitions which have a property interface and operation.
    
TOSCA XML pseudo definition
```yaml
NodeTypeImplementation:
 ...
 ImplementationArtifacts:
   ImplementationArtifact: *
     interfaceName: InterfaceeName
     operationName: OperationName
     artifactType: ArtifactTypeName?
     artifactRef: ArtifactTemplateName

ArtifactTemplate:
 ...
 ArtifactReferences:
   ArtifactReference: *
     reference: ArtifactFileName
```
Mapped to TOSCA YAML
```yaml
NodeType:
 ...
 artifacts:
   ArtifactDefinitionName: * 
     type: ArtifactTypeName
     file: 
       - File URI|Name 
     repository: RepositoryDefinitionName
     deploy_path: Path
 interfaces:
   InterfaceDefinitionName:
     type: InterfaceTypeName?
     ...
     OperationDefinitionName: *
       ...
       implementation:
         primary: ArtifactDefinitionName
         dependencies: ArtifactDefinitionName*
         
InterfaceType:
 ...
 OperationDefinitionName: *
```

To build NodeTypeImplementation.ImplementationArtifacts from TOSCA YAML NodeTypes the NodeType interface operations must be visited and Operation.Implementation is converted to an ArtifactTemplate an ImplementationArtifact. Those ImplementationArtifacts are collected into a NodeTypeImplementation for the NodeType.

Additionally the NodeType.ArtifactDefinitions are converted to ImplementationArtifacts for all operation and interfaces. There is no decision mechanism in place that decides if an YAML ArtifactDefinition contains an ImplementationArtifact or an DeploymentArtifact.

ArtifactDefinitions with an ArtifactType deriving from TOSCA YAML normative types `tosca.artifacts.Implementation` or `tosca.artifacts.Deployment` are considered as either deployment or implementation artifacts. 

ArtifactDefinitions with an ArtifactType not deriving from these TOSCA YAML normative types are converted as deployment and implementation artifacts.

#### Restrictions
YAML ArtifactDefinitions which are referenced in operation implementations will get an interfaceName property and an operationName property.

If ArtifactDefinitions are referenced in multiple operation implementation or in different interfaces, the converter will change the properties to make the TOSCA XML ImplementationArtifact available for all operations.

If the user the an ImplementationArtifact should be available for all interfaces or operations either don't reference it in any operation implementation or reference it in all operation implementations.

