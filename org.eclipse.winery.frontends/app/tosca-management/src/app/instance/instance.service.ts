/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../configuration';
import { WineryInstance, WineryTopologyTemplate } from '../model/wineryComponent';
import { ToscaComponent } from '../model/toscaComponent';
import { ToscaTypes } from '../model/enums';
import { WineryVersion } from '../model/wineryVersion';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { WineryRepositoryConfigurationService } from '../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { SubMenuItem, SubMenuItems } from '../model/subMenuItem';

export interface ToscaLightCompatibilityData {
    isToscaLightCompatible: boolean;
    errorList: Map<string, string[]>;
}

@Injectable()
export class InstanceService {

    toscaComponent: ToscaComponent;
    topologyTemplate: WineryTopologyTemplate = null;
    versions: WineryVersion[];
    currentVersion: WineryVersion;
    path: string;

    constructor(private http: HttpClient, private configurationService: WineryRepositoryConfigurationService) {
    }

    /**
     * Get the submenu for the given resource type for displaying a component instance.
     *
     * @returns SubMenuItem[] containing all menus for each resource type.
     */
    public getSubMenuByResource(): SubMenuItem[] {
        let subMenu: SubMenuItem[];
        switch (this.toscaComponent.toscaType) {
            case ToscaTypes.NodeType:
                if (this.configurationService.isYaml()) {
                    subMenu = [SubMenuItems.readme, SubMenuItems.documentation, SubMenuItems.license, SubMenuItems.appearance, SubMenuItems.inheritance,
                        SubMenuItems.artifacts, SubMenuItems.interfacedefinitions, SubMenuItems.requirementDefinitionsYaml, SubMenuItems.capabilityDefinitions,
                        SubMenuItems.propertiesDefinition, SubMenuItems.attributes];
                } else {
                    subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.appearance, SubMenuItems.instanceStates, SubMenuItems.interfaces,
                        SubMenuItems.implementations, SubMenuItems.inheritance, SubMenuItems.requirementDefinitions, SubMenuItems.capabilityDefinitions,
                        SubMenuItems.propertiesDefinition, SubMenuItems.tags, SubMenuItems.documentation, SubMenuItems.xml];
                }
                break;
            case ToscaTypes.ServiceTemplate:
                subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.topologyTemplate];
                if (this.configurationService.isYaml()) {
                    subMenu.push(SubMenuItems.parameters);
                }
                if (!this.configurationService.isYaml()) {
                    subMenu.push(SubMenuItems.documentation, SubMenuItems.plans, SubMenuItems.selfServicePortal,
                        SubMenuItems.boundaryDefinitions, SubMenuItems.tags, SubMenuItems.constraintChecking,
                        SubMenuItems.xml);
                    if (this.configurationService.configuration.features.nfv) {
                        subMenu.push(SubMenuItems.threatModeling);
                    }
                }
                break;
            case ToscaTypes.RelationshipType:
                if (this.configurationService.isYaml()) {
                    subMenu = [SubMenuItems.readme, SubMenuItems.documentation, SubMenuItems.license, SubMenuItems.appearance, SubMenuItems.instanceStates,
                        SubMenuItems.interfacedefinitions, SubMenuItems.validSourcesAndTargets, SubMenuItems.implementations, SubMenuItems.propertiesDefinition,
                        SubMenuItems.inheritance];
                } else {
                    subMenu = [SubMenuItems.readme, SubMenuItems.documentation, SubMenuItems.license, SubMenuItems.appearance, SubMenuItems.instanceStates,
                        SubMenuItems.sourceInterfaces, SubMenuItems.interfaces, SubMenuItems.targetInterfaces, SubMenuItems.validSourcesAndTargets,
                        SubMenuItems.implementations,
                        SubMenuItems.propertiesDefinition, SubMenuItems.inheritance, SubMenuItems.xml];
                }
                break;
            case ToscaTypes.ArtifactType:
                if (this.configurationService.isYaml()) {
                    subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.propertiesDefinition, SubMenuItems.inheritance,
                        SubMenuItems.supportedFiles, SubMenuItems.documentation];
                } else {
                    subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.propertiesDefinition, SubMenuItems.inheritance, SubMenuItems.templates,
                        SubMenuItems.documentation, SubMenuItems.xml];
                }
                break;
            case ToscaTypes.ArtifactTemplate:
                subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.files, SubMenuItems.source, SubMenuItems.properties,
                    SubMenuItems.propertyConstraints, SubMenuItems.documentation, SubMenuItems.xml];
                break;
            case ToscaTypes.RequirementType:
                subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.requiredCapabilityType, SubMenuItems.propertiesDefinition,
                    SubMenuItems.inheritance, SubMenuItems.documentation, SubMenuItems.xml];
                break;
            case ToscaTypes.CapabilityType:
                if (this.configurationService.isYaml()) {
                    subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.propertiesDefinition, SubMenuItems.inheritance,
                        SubMenuItems.capabilityTypeConstraints, SubMenuItems.documentation];
                } else {
                    subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.propertiesDefinition, SubMenuItems.inheritance,
                        SubMenuItems.documentation, SubMenuItems.xml];
                }
                break;
            case ToscaTypes.NodeTypeImplementation:
                subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.implementationArtifacts, SubMenuItems.deploymentArtifacts,
                    SubMenuItems.inheritance, SubMenuItems.documentation, SubMenuItems.xml];
                break;
            case ToscaTypes.RelationshipTypeImplementation:
                subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.implementationArtifacts, SubMenuItems.inheritance,
                    SubMenuItems.documentation, SubMenuItems.xml];
                break;
            case ToscaTypes.PolicyType:
                if (this.configurationService.isYaml()) {
                    subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.appliesTo, SubMenuItems.propertiesDefinition,
                        SubMenuItems.inheritance, SubMenuItems.appearance, SubMenuItems.documentation];
                } else {
                    subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.language, SubMenuItems.appliesTo, SubMenuItems.propertiesDefinition,
                        SubMenuItems.inheritance, SubMenuItems.templates, SubMenuItems.appearance, SubMenuItems.documentation, SubMenuItems.xml];
                }
                break;
            case ToscaTypes.PolicyTemplate:
                subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.properties, SubMenuItems.propertyConstraints, SubMenuItems.appearance,
                    SubMenuItems.documentation, SubMenuItems.xml];
                break;
            case ToscaTypes.Imports:
                subMenu = [SubMenuItems.allDeclaredElementsLocalNames, SubMenuItems.allDefinedTypesLocalNames];
                break;
            case ToscaTypes.ComplianceRule:
                subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.identifier, SubMenuItems.requiredStructure, SubMenuItems.tags,
                    SubMenuItems.documentation, SubMenuItems.xml];
                break;
            case ToscaTypes.PatternRefinementModel:
                subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.detector, SubMenuItems.refinementStructure, SubMenuItems.relationMappings,
                    SubMenuItems.attributeMappings, SubMenuItems.stayMappings, SubMenuItems.xml];
                break;
            case ToscaTypes.TestRefinementModel:
                subMenu = [SubMenuItems.readme, SubMenuItems.license, SubMenuItems.detector, SubMenuItems.testFragment, SubMenuItems.relationMappings,
                    SubMenuItems.xml];
                break;
            default: // assume Admin
                if (this.configurationService.isYaml()) {
                    subMenu = [SubMenuItems.namespaces, SubMenuItems.repository, SubMenuItems.log, SubMenuItems.configuration];
                } else {
                    // YAML 1.2 does not contain Constraint Types
                    subMenu = [SubMenuItems.namespaces, SubMenuItems.repository, SubMenuItems.planLanguages, SubMenuItems.planTypes,
                        SubMenuItems.constraintTypes, SubMenuItems.consistencyCheck, SubMenuItems.log, SubMenuItems.configuration];
                }
                if (this.configurationService.configuration.features.accountability) {
                    subMenu.push(SubMenuItems.accountability);
                }
                if (this.configurationService.configuration.features.edmmModeling) {
                    subMenu.push(SubMenuItems.oneToOneEDMMMappings, SubMenuItems.edmmTypeMappings);
                }
        }

        if (this.configurationService.isYaml()) {
            subMenu = subMenu.filter(item => item !== SubMenuItems.xml);
        }

        return subMenu;
    }

    /**
     * Set the shared data for the children. The path to the actual component is also generated.
     */
    public setSharedData(toscaComponent: ToscaComponent): void {
        this.toscaComponent = toscaComponent;
        // In order to have always the base path of this instance, create the path here
        // instead of getting it from the router, because there might be some child routes included.
        this.path = backendBaseURL + '/' + this.toscaComponent.toscaType + '/'
            + encodeURIComponent(encodeURIComponent(this.toscaComponent.namespace)) + '/'
            + this.toscaComponent.localName;

        if (this.toscaComponent.toscaType === ToscaTypes.ServiceTemplate) {
            this.getTopologyTemplate()
                .subscribe(
                    data => this.topologyTemplate = data,
                    () => this.topologyTemplate = null
                );
        }
    }

    public deleteComponent(): Observable<HttpResponse<string>> {
        return this.http.delete(
            this.path + '/',
            { observe: 'response', responseType: 'text' }
        );
    }

    public getComponentData(): Observable<WineryInstance> {
        return this.http.get<WineryInstance>(this.path + '/');
    }

    public getTopologyTemplate(): Observable<WineryTopologyTemplate> {
        return this.http.get<WineryTopologyTemplate>(this.path + '/topologytemplate/');
    }

    public getVersions(): Observable<WineryVersion[]> {
        return this.http.get<WineryVersion[]>(this.path + '/?versions');
    }

    public getToscaLightCompatibility(): Observable<ToscaLightCompatibilityData> {
        return this.http.get<ToscaLightCompatibilityData>(this.path + '/toscalight');
    }

    public exportToFilesystem(): Observable<HttpResponse<string>> {
        return this.http.post(this.path + '/exportToFilesystem', {},
            { observe: 'response', responseType: 'text' });
    }
}
