/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
export interface SubMenuItem {
    displayName: string;
    urlFragment: string;
}

export class SubMenuItems {
    static readonly accountability: SubMenuItem = { displayName: 'Accountability', urlFragment: 'accountability' };
    static readonly allDeclaredElementsLocalNames: SubMenuItem = {
        displayName: 'All Declared Elements Local Names', urlFragment: 'alldeclaredelementslocalnames'
    };
    static readonly allDefinedTypesLocalNames: SubMenuItem = {
        displayName: 'All Defined Types Local Names', urlFragment: 'alldefinedtypeslocalnames'
    };
    static readonly appearance: SubMenuItem = { displayName: 'Appearance', urlFragment: 'appearance' };
    static readonly appliesTo: SubMenuItem = { displayName: 'Applies To', urlFragment: 'appliesto' };
    static readonly attributeMappings: SubMenuItem = { displayName: 'Attribute Mappings', urlFragment: 'attributemappings' };
    static readonly artifacts: SubMenuItem = { displayName: 'Artifacts', urlFragment: 'artifacts' };
    static readonly boundaryDefinitions: SubMenuItem = { displayName: 'Boundary Definitions', urlFragment: 'boundarydefinitions' };
    static readonly capabilityDefinitions: SubMenuItem = { displayName: 'Capability Definitions', urlFragment: 'capabilitydefinitions' };
    static readonly configuration: SubMenuItem = { displayName: 'Configuration', urlFragment: 'configuration' };
    static readonly consistencyCheck: SubMenuItem = { displayName: 'Consistency Check', urlFragment: 'consistencycheck' };
    static readonly constraintChecking: SubMenuItem = { displayName: 'Constraint Checking', urlFragment: 'constraintchecking' };
    static readonly constraintTypes: SubMenuItem = { displayName: 'Constraint Types', urlFragment: 'constrainttypes' };
    static readonly deploymentArtifacts: SubMenuItem = { displayName: 'Deployment Artifacts', urlFragment: 'deploymentartifacts' };
    static readonly detector: SubMenuItem = { displayName: 'Detector', urlFragment: 'detector' };
    static readonly documentation: SubMenuItem = { displayName: 'Documentation', urlFragment: 'documentation' };
    static readonly edmmTypeMappings: SubMenuItem = { displayName: 'EDMM Type Mappings', urlFragment: 'edmmtypemappings' };
    static readonly files: SubMenuItem = { displayName: 'Files', urlFragment: 'files' };
    static readonly identifier: SubMenuItem = { displayName: 'Identifier', urlFragment: 'identifier' };
    static readonly implementationArtifacts: SubMenuItem = { displayName: 'Implementation Artifacts', urlFragment: 'implementationartifacts' };
    static readonly implementations: SubMenuItem = { displayName: 'Implementations', urlFragment: 'implementations' };
    static readonly inheritance: SubMenuItem = { displayName: 'Inheritance', urlFragment: 'inheritance' };
    static readonly instanceStates: SubMenuItem = { displayName: 'Instance States', urlFragment: 'instancestates' };
    static readonly interfaces: SubMenuItem = { displayName: 'Interfaces', urlFragment: 'interfaces' };
    static readonly language: SubMenuItem = { displayName: 'Language', urlFragment: 'language' };
    static readonly license: SubMenuItem = { displayName: 'LICENSE', urlFragment: 'license' };
    static readonly log: SubMenuItem = { displayName: 'Log', urlFragment: 'log' };
    static readonly namespaces: SubMenuItem = { displayName: 'Namespaces', urlFragment: 'namespaces' };
    static readonly oneToOneEDMMMappings: SubMenuItem = { displayName: '1 to 1 EDMM Mappings', urlFragment: '1to1edmmmappings' };
    static readonly planLanguages: SubMenuItem = { displayName: 'Plan Languages', urlFragment: 'planlanguages' };
    static readonly plans: SubMenuItem = { displayName: 'Plans', urlFragment: 'plans' };
    static readonly parameters: SubMenuItem = { displayName: 'Parameters', urlFragment: 'parameters' };
    static readonly planTypes: SubMenuItem = { displayName: 'Plan Types', urlFragment: 'plantypes' };
    static readonly properties: SubMenuItem = { displayName: 'Properties', urlFragment: 'properties' };
    static readonly propertiesDefinition: SubMenuItem = { displayName: 'Property Definitions', urlFragment: 'propertiesdefinition' };
    static readonly propertyConstraints: SubMenuItem = { displayName: 'Property Constraints', urlFragment: 'propertyconstraints' };
    static readonly readme: SubMenuItem = { displayName: 'README', urlFragment: 'readme' };
    static readonly refinementStructure: SubMenuItem = { displayName: 'Refinement Structure', urlFragment: 'refinementstructure' };
    static readonly relationMappings: SubMenuItem = { displayName: 'Relation Mappings', urlFragment: 'relationmappings' };
    static readonly repository: SubMenuItem = { displayName: 'Repository', urlFragment: 'repository' };
    static readonly requiredCapabilityType: SubMenuItem = { displayName: 'Required Capability Type', urlFragment: 'requiredcapabilitytype' };
    static readonly requiredStructure: SubMenuItem = { displayName: 'Required Structure', urlFragment: 'requiredstructure' };
    static readonly requirementDefinitions: SubMenuItem = { displayName: 'Requirement Definitions', urlFragment: 'requirementdefinitions' };
    static readonly requirementDefinitionsYaml: SubMenuItem = { displayName: 'Requirement Definitions', urlFragment: 'requirementdefinitionsyaml' };
    static readonly selfServicePortal: SubMenuItem = { displayName: 'Self-Service Portal', urlFragment: 'selfserviceportal' };
    static readonly source: SubMenuItem = { displayName: 'Source', urlFragment: 'source' };
    static readonly sourceInterfaces: SubMenuItem = { displayName: 'Source Interfaces', urlFragment: 'sourceinterfaces' };
    static readonly stayMappings: SubMenuItem = { displayName: 'Stay Mappings', urlFragment: 'staymappings' };
    static readonly tags: SubMenuItem = { displayName: 'Tags', urlFragment: 'tags' };
    static readonly targetInterfaces: SubMenuItem = { displayName: 'Target Interfaces', urlFragment: 'targetinterfaces' };
    static readonly templates: SubMenuItem = { displayName: 'Templates', urlFragment: 'templates' };
    static readonly testFragment: SubMenuItem = { displayName: 'Test Fragment', urlFragment: 'testfragment' };
    static readonly threatModeling: SubMenuItem = { displayName: 'Threat Modeling', urlFragment: 'threatmodeling' };
    static readonly topologyTemplate: SubMenuItem = { displayName: 'Topology Template', urlFragment: 'topologytemplate' };
    static readonly validSourcesAndTargets: SubMenuItem = { displayName: 'Valid Sources and Targets', urlFragment: 'validsourcesandtargets' };
    static readonly capabilityTypeConstraints: SubMenuItem = { displayName: 'Constraints', urlFragment: 'constraints' };
    static readonly supportedFiles: SubMenuItem = { displayName: 'Supported File Types', urlFragment: 'supportedfiles' };
    static readonly xml: SubMenuItem = { displayName: 'XML', urlFragment: 'xml' };
    static readonly attributes: SubMenuItem = { displayName: 'Attribute Definitions', urlFragment: 'attributes' };
    static readonly interfacedefinitions: SubMenuItem = { displayName: 'Interfaces', urlFragment: 'interfacedefinitions' };
}
