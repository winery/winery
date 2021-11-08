/*******************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RelationMapping } from './relationshipMappings/relationMapping';
import { backendBaseURL } from '../../configuration';
import { Injectable } from '@angular/core';
import { InstanceService } from '../instance.service';
import { NodeTemplate, RelationshipTemplate } from '../../model/wineryComponent';
import { SelectData } from '../../model/selectData';
import { Utils } from '../../wineryUtils/utils';
import { PropertiesDefinitionsResourceApiData } from '../sharedComponents/propertiesDefinition/propertiesDefinitionsResourceApiData';
import { ToscaTypes } from '../../model/enums';
import { StayMapping } from './stayMappings/stayMapping';
import { AttributeMapping } from './attributeMappings/attributeMapping';
import { DeploymentArtifactMapping } from './deploymentArtifactsMappings/deploymentArtifactMapping';
import { RefinementMappings } from './RefinementMappings';
import { PermutationMapping } from './permutationMappings/permutationMapping';
import { BehaviorPatternMapping } from './behavior-pattern-mappings/types';

@Injectable()
export class RefinementMappingsService {

    private readonly path: string;

    constructor(private http: HttpClient, private sharedData: InstanceService) {
        this.path = this.sharedData.path;
    }

    public getDetectorNodeTemplates(): Observable<NodeTemplate[]> {
        return this.http.get<NodeTemplate[]>(this.path + '/detector/nodetemplates/');
    }

    public getDetectorRelationshipTemplates(): Observable<RelationshipTemplate[]> {
        return this.http.get<RelationshipTemplate[]>(this.path + '/detector/relationshiptemplates/');
    }

    public getRefinementTopologyNodeTemplates(): Observable<NodeTemplate[]> {
        return this.http.get<NodeTemplate[]>(this.getRefinementStructureUrl() + '/nodetemplates/');
    }

    public getRefinementTopologyRelationshipTemplates(): Observable<RelationshipTemplate[]> {
        return this.http.get<RelationshipTemplate[]>(this.getRefinementStructureUrl() + '/relationshiptemplates/');
    }

    public getRelationshipTypes(): Observable<SelectData[]> {
        return this.http.get<SelectData[]>(backendBaseURL + '/relationshiptypes/?grouped=angularSelect');
    }

    public getNodeTypes(): Observable<SelectData[]> {
        return this.http.get<SelectData[]>(backendBaseURL + '/nodetypes/?grouped=angularSelect');
    }

    public getArtifactTypes(): Observable<SelectData[]> {
        return this.http.get<SelectData[]>(backendBaseURL + '/artifacttypes/?grouped=angularSelect');
    }

    public getRelationshipMappings(): Observable<RelationMapping[]> {
        return this.http.get<RelationMapping[]>(this.path + '/relationmappings');
    }

    public addRelationMapping(element: RelationMapping): Observable<RelationMapping[]> {
        return this.http.put<RelationMapping[]>(this.path + '/relationmappings', element);
    }

    public deleteRelationMapping(mapping: RelationMapping): Observable<RelationMapping[]> {
        return this.http.delete<RelationMapping[]>(this.path + '/relationmappings/' + mapping.id);
    }

    public getPropertyMappings(): Observable<AttributeMapping[]> {
        return this.http.get<AttributeMapping[]>(this.path + '/attributemappings');
    }

    public addPrmPropertyMapping(element: AttributeMapping): Observable<AttributeMapping[]> {
        return this.http.put<AttributeMapping[]>(this.path + '/attributemappings', element);
    }

    public deletePrmPropertyMapping(element: AttributeMapping): Observable<AttributeMapping[]> {
        return this.http.delete<AttributeMapping[]>(this.path + '/attributemappings/' + element.id);
    }

    public getTypeProperties(type: string, nodeTemplate: boolean): Observable<PropertiesDefinitionsResourceApiData> {
        const qName = Utils.getNamespaceAndLocalNameFromQName(type);
        const toscaType = nodeTemplate ? 'nodetypes' : 'relationshiptypes';
        const url = backendBaseURL + `/${toscaType}/${encodeURIComponent(encodeURIComponent(qName.namespace))}/${qName.localName}/propertiesdefinition/`;
        return this.http.get<PropertiesDefinitionsResourceApiData>(url);
    }

    public getStayMappings(): Observable<StayMapping[]> {
        return this.http.get<StayMapping[]>(this.path + '/staymappings');
    }

    public addStayMapping(element: StayMapping): Observable<StayMapping[]> {
        return this.http.put<StayMapping[]>(this.path + '/staymappings', element);
    }

    public deleteStayMapping(mapping: StayMapping): Observable<StayMapping[]> {
        return this.http.delete<StayMapping[]>(this.path + '/staymappings/' + mapping.id);
    }

    public addDeploymentArtifactMappings(element: DeploymentArtifactMapping): Observable<DeploymentArtifactMapping[]> {
        return this.http.put<DeploymentArtifactMapping[]>(this.path + '/deploymentartifactmappings', element);
    }

    public deleteDeploymentArtifactMappings(mapping: DeploymentArtifactMapping): Observable<DeploymentArtifactMapping[]> {
        return this.http.delete<DeploymentArtifactMapping[]>(this.path + '/deploymentartifactmappings/' + mapping.id);
    }

    public getDeploymentArtifactMappings(): Observable<DeploymentArtifactMapping[]> {
        return this.http.get<DeploymentArtifactMapping[]>(this.path + '/deploymentartifactmappings');
    }

    public addPermutationMappings(mapping: PermutationMapping): Observable<PermutationMapping[]> {
        return this.http.put<PermutationMapping[]>(this.path + '/permutationmappings', mapping);
    }

    public deletePermutationMappings(mapping: PermutationMapping): Observable<PermutationMapping[]> {
        return this.http.delete<PermutationMapping[]>(this.path + '/permutationmappings/' + mapping.id);
    }

    public getPermutationMappings(): Observable<PermutationMapping[]> {
        return this.http.get<PermutationMapping[]>(this.path + '/permutationmappings');
    }

    public addBehaviorPatternMapping(mapping: BehaviorPatternMapping): Observable<BehaviorPatternMapping[]> {
        return this.http.put<BehaviorPatternMapping[]>(this.path + '/behaviorpatternmappings', mapping);
    }

    public deleteBehaviorPatternMapping(mapping: BehaviorPatternMapping): Observable<BehaviorPatternMapping[]> {
        return this.http.delete<BehaviorPatternMapping[]>(this.path + '/behaviorpatternmappings/' + mapping.id);
    }

    public getBehaviorPatternMappings(): Observable<BehaviorPatternMapping[]> {
        return this.http.get<BehaviorPatternMapping[]>(this.path + '/behaviorpatternmappings');
    }

    public getNewMappingsId(mappings: RefinementMappings[], prefix: string): number {
        let id = 0;
        mappings.forEach(value => {
            const number = Number(value.id.split(prefix)[1]);
            if (!isNaN(number) && number >= id) {
                id = number;
                if (number === id) {
                    id++;
                }
            }
        });

        return id;
    }

    private getRefinementStructureUrl(): string {
        let url = this.path;
        if (this.sharedData.toscaComponent.toscaType === ToscaTypes.PatternRefinementModel
            || this.sharedData.toscaComponent.toscaType === ToscaTypes.TopologyFragmentRefinementModel) {
            url += '/refinementstructure';
        } else if (this.sharedData.toscaComponent.toscaType === ToscaTypes.TestRefinementModel) {
            url += '/testfragment';
        }
        return url;
    }
}
