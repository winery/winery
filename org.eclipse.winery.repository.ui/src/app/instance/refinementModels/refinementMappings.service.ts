/*******************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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
import { NodeTemplate } from '../../model/wineryComponent';
import { SelectData } from '../../model/selectData';
import { PrmPropertyMapping } from './propertyMappings/prmPropertyMapping';
import { Utils } from '../../wineryUtils/utils';
import { PropertiesDefinitionsResourceApiData } from '../sharedComponents/propertiesDefinition/propertiesDefinitionsResourceApiData';
import { ToscaTypes } from '../../model/enums';

@Injectable()
export class RefinementMappingsService {

    private readonly path: string;

    constructor(private http: HttpClient, private sharedData: InstanceService) {
        this.path = backendBaseURL + this.sharedData.path;
    }

    public getDetectorNodeTemplates(): Observable<NodeTemplate[]> {
        return this.http.get<NodeTemplate[]>(this.path + '/detector/nodetemplates/');
    }

    public getRefinementTopologyNodeTemplates(): Observable<NodeTemplate[]> {
        let url = this.path;
        if (this.sharedData.toscaComponent.toscaType === ToscaTypes.PatternRefinementModel) {
            url += '/refinementstructure';
        } else if (this.sharedData.toscaComponent.toscaType === ToscaTypes.TestRefinementModel) {
            url += '/testfragment';
        }
        url += '/nodetemplates/';
        return this.http.get<NodeTemplate[]>(url);
    }

    public getRelationshipTypes(): Observable<SelectData[]> {
        return this.http.get<SelectData[]>(backendBaseURL + '/relationshiptypes/?grouped=angularSelect');
    }

    public getNodeTypes(): Observable<SelectData[]> {
        return this.http.get<SelectData[]>(backendBaseURL + '/nodetypes/?grouped=angularSelect');
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

    getPropertyMappings(): Observable<PrmPropertyMapping[]> {
        return this.http.get<PrmPropertyMapping[]>(this.path + '/propertymappings');
    }

    public addPrmPropertyMapping(element: PrmPropertyMapping): Observable<PrmPropertyMapping[]> {
        return this.http.put<PrmPropertyMapping[]>(this.path + '/propertymappings', element);
    }

    public deletePrmPropertyMapping(element: PrmPropertyMapping): Observable<PrmPropertyMapping[]> {
        return this.http.delete<PrmPropertyMapping[]>(this.path + '/propertymappings/' + element.id);
    }

    public getNodeTypeProperties(type: string): Observable<PropertiesDefinitionsResourceApiData> {
        const qName = Utils.getNamespaceAndLocalNameFromQName(type);
        const url = backendBaseURL + `/nodetypes/${encodeURIComponent(encodeURIComponent(qName.namespace))}/${qName.localName}/propertiesdefinition/`;
        return this.http.get<PropertiesDefinitionsResourceApiData>(url);
    }
}
