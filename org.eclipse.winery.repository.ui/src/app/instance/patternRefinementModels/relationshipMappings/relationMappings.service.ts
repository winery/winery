/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
import { RelationMappings } from './relationMappings';
import { backendBaseURL } from '../../../configuration';
import { Injectable } from '@angular/core';
import { InstanceService } from '../../instance.service';
import { NodeTemplate } from '../../../model/wineryComponent';
import { SelectData } from '../../../model/selectData';

@Injectable()
export class RelationMappingsService {

    private readonly path: string;

    constructor(private http: HttpClient, private sharedData: InstanceService) {
        this.path = backendBaseURL + this.sharedData.path;
    }

    public getRelationshipMappings(): Observable<RelationMappings[]> {
        return this.http.get<RelationMappings[]>(this.path + '/relationmappings');
    }

    public getDetectorNodeTemplates(): Observable<NodeTemplate[]> {
        return this.http.get<NodeTemplate[]>(this.path + '/detector/nodetemplates/');
    }

    public getRefinementStructureNodeTemplates(): Observable<NodeTemplate[]> {
        return this.http.get<NodeTemplate[]>(this.path + '/refinementstructure/nodetemplates/');
    }

    public getRelationshipTypes(): Observable<SelectData[]> {
        return this.http.get<SelectData[]>(backendBaseURL + '/relationshiptypes/?grouped=angularSelect');
    }

    public getNodeTypes(): Observable<SelectData[]> {
        return this.http.get<SelectData[]>(backendBaseURL + '/nodetypes/?grouped=angularSelect');
    }

    public addRelationMapping(element: RelationMappings): Observable<RelationMappings[]> {
        return this.http.put<RelationMappings[]>(this.path + '/relationmappings', element);
    }

    deleteRelationMapping(mapping: RelationMappings): Observable<RelationMappings[]> {
        return this.http.delete<RelationMappings[]>(this.path + '/relationmappings/' + mapping.id);
    }
}
