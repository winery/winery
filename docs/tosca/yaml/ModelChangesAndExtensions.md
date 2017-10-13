# Model Changes compared to TOSCA Simple Profile in YAML Version 1.1
## Annotation
Changes to the model are annotated with `org.eclipse.winery.model.tosca.yaml.Annotations.StandardExtension`. The following list shows all occurrences of the annotation.
- TArtifactDefinition
  - `List<String> files`
  - `Map<String, TPropertyAssignment> properties`
- TOperationDefinition
  - `Map<String, TPropertyAssignmentOrDefinition> outputs`
  - `TImplementation implementation`
- TRequirementDefinition
  - `String description`
  
### TArtifactDefinition
#### More than one file per definition
The model limits the number of files that can be specified for one Artifact definition to one. This data model supports more than one file per Artifact Definition.

Old syntax (data model is backwards compatible):
```yaml
artifactDefinitionName:
  type: string
  file: string
  repository: string?
  description: string?
  deploy_path: string?
```
New syntax:
```yaml
artifactDefinitionName:
  type: string
  files: string+
  repository: string?
  description: string?
  deploy_path: string?
```

#### Property Assignment
All tosca entities which have property definitions specified for their types should also have property assignments for instances of those types.

This data model added the field `properties` for assigning properties to the property definition specified in Artifact types.

```yaml
artifactDefinitionName:
  ...
  properties: TPropertyAssignment*
```

### TOperationDefinition
#### Outputs
Operation definition of InterfaceDefinition or InterfaceTypes have an additional field `outputs` similar to TOSCA XML OperationDefinition.

The specification allows to use the output of operations by using `get_operation_output` for AttributeDefinition default values:

(example for NodeType.attributes)
```yaml
attributeName:
  default: { get_operation_ouput: [ SELF, interfaceName, operationName, outputName ] }
```

#### Implementation
The implementation field originally specifies the name of an implementation artifact which must be point to a file within the a TOSCA CSAR file.

Since the TOSCA XML data model always requires complete artifacts with types the value of the field `implementation` or `implementation.primary` and the values of the list `implementation.dependencies` are references to ArtifactDefinitions defined in the same entity.

Old Syntax
```yaml
implementation: "/path/file.sh"

implementation:
  primary: "/path/file.sh"
  dependencies:
    - "/path/dependencyFile1.sh"
    - "/path/dependencyFile1.sh"
```

New Sysntax
```yaml
artifact1:
  description: Artifact definition example
  type: Bash
  files: 
    - "/path/file.sh"
  repository: ~
  deploypath: ~
  
implementation: artifact1

implementation:
  primary: artifact1
  dependencies:
    - ...
```

### TRequirementDefinition
RequirementDefinitions in this model have the additional field description.

## References
Types references and other references are defined as `strings` in the TOSCA Simple Profile in YAML Specification. This model uses `QNames` instead of simple strings. Each reference has a namespace.

References to elements in the same ServiceTemplate have the `namespace_uri` of the ServiceTemplate, which is specified by the user when parsing the document or set to the default namespace (`http://www.eclipse.org/winery/ns/simple/yaml/1.1/default`).
  
References to elements specified in imported ServiceTemplate will use the namespace and prefix of the the ImportDefinition, if defined, or the namespace of the importing ServiceTemplate.

The serialization of references with namespaces depends on `namespace_uri` and `namespace_prefix`:
1. `namespace_prefix` is defined for a QName: (serialization) `prefix:localName`
1. Only `namespace_uri` is defined: (serialization) `{https://example.org/ns}localName`
 
### TOSCA YAML Simple Profile Normative Types
References to normative types MUST NOT have a prefix and the normative types will be imported automatically.

## List<Map<String, ?>>
All occurrences of List of Collections have a support java class that wraps the inner collection and implements the Map interface. 

This solves the problem of generating an xml schema for this model with JAXB. Because JAXB has trouble to convert List of Collections without specifying adapters for each occurrence.  

The support classes are `TMapObject.class`, `TMapImportDefinition`, `TMapPropertyFilterDefinition`, `TMapRequirementAssignment`, `TMapRequirementDefinition`.

_Explanation: YAML Map elements have no defined order therefor the specification uses a List of (single element) Maps to specify an order if necessary._

## Workflows
The specification supports the definition of `workflows` for TopologyTemplateDefinition. This data model does not support the field `workflows` and rather encourages the use of well known workflow languages like BPEL and BPMN.

## Builders
Java Builders defined for the data model classes do not change the model language and only simplify the work with this data model.
 
## Visitor Interface
Each model class has a generic visitor function.
```java
interface NodeVisitor<P, R> {
  R accept(NodeVisitor visitor, P parameter);
}
 ```
Those functions and the abstract class and interfaces defined in the `visitor` package allows to create visitor design pattern for traversing the ServiceTemplate.
 
The java classes AbstractVisitor, AbstractParameter and AbstractResult can be extended to write Visitors:
 
```java
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
/*...*/

public class VisitorExample extends AbstractVisitor<Result, Parameter> {
    public List<TNodeType> getNodeTypes(TServiceTemplate serviceTemplate) {
        return visit(serviceTemplate, new Parameter()).getNodeTypes();
    }	
    
    @Override
    public Result visit(TServiceTemplate node, Parameter parameter) {
        Result result = super.visit(node, parameter);
        if (result == null) {
            result = new Result();
        }
        
        return result;
    }
    
    @Override
    public Result visit(TNodeType node, Parameter parameter) {
        List<TNodeType> list = new ArrayList<>();
        list.add(node);
        return new Result().addNodeTypes(list);
    }
    
    public static class Result extends AbstractResult<Result> {
        private List<TNodeType> nodeTypes;
        
        public Result() {
            this.nodeTypes = new ArrayList<>();
        }
        
        public Result addNodeTypes(List<TNodeType> nodeTypes) {
            this.nodeTypes.addAll(nodeTypes);
        }
        
        public List<TNodeType> getNodeTypes() {
            return this.nodeTypes;
        }
        
        @Override
        public Result add(Result result) {
            return this.addNodeTypes(result.nodeTypes);
        }
    }
    
    public static class Parameter extends AbstractResult<Parameter> {
        @Override
        public Parameter copy() {
            return new Parameter();
        }
        
        @Override
        public Parameter self() {
            return this;
        }
    }
}
```
 
_The visitor design pattern functions and classes do not change the fields of the data model._ 


## License

Copyright (c) git 2017 University of Stuttgart.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v2.0]
and the [Apache License v2.0] which both accompany this distribution,
and are available at http://www.eclipse.org/legal/epl-v20.html
and http://www.apache.org/licenses/LICENSE-2.0

Contributors:
* Christoph Kleine - initial API and implementation

  [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
  [Eclipse Public License v2.0]: http://www.eclipse.org/legal/epl-v20.html
