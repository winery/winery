/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';

import { NodeTemplate } from '../model/nodetemplate';
import { PageParameter } from '../model/page-parameter';
import { Node } from '../model/workflow/node';
import { HttpService } from '../util/http.service';
import { BroadcastService } from './broadcast.service';
import { HttpHeaders } from '@angular/common/http';
import { ToscaInterface } from '../model/toscaInterface';
import { map } from 'rxjs/internal/operators';

/**
 * WineryService
 * provides operation about winery. It can load and save data from winery.
 */
@Injectable()
export class WineryService {
    private repositoryURL: string;
    private namespace: string;
    private serviceTemplateId: string;
    private plan: string;

    constructor(private broadcastService: BroadcastService,
                private httpService: HttpService) {
        this.broadcastService.saveEvent$.subscribe(data => this.save(data));
    }

    public setRequestParam(queryParams: PageParameter) {
        this.repositoryURL = queryParams.repositoryURL;
        this.namespace = queryParams.namespace;
        this.serviceTemplateId = queryParams.id;
        this.plan = queryParams.plan;

        if (this.repositoryURL) {
            this.loadPlan();
        }
    }

    public loadNodeTemplates(): Observable<NodeTemplate[]> {
        const url = 'servicetemplates/' + this.encode(this.namespace)
            + '/' + this.encode(this.serviceTemplateId) + '/topologytemplate/';

        return this.httpService.get(this.getFullUrl(url))
            .pipe(map(this.transferResponse2NodeTemplate));
    }

    private transferResponse2NodeTemplate(response) {
        const nodeTemplates: NodeTemplate[] = [];
        for (const key in response.nodeTemplates) {
            if (response.nodeTemplates.hasOwnProperty(key)) {
                const nodeTemplate = response.nodeTemplates[key];
                nodeTemplates.push(new NodeTemplate(
                    nodeTemplate.id,
                    nodeTemplate.name,
                    nodeTemplate.type,
                    nodeTemplate.type.replace(/^\{(.+)\}(.+)/, '$1')));
            }
        }
        return nodeTemplates;
    }

    public loadTopologyProperties(nodeTemplate: NodeTemplate) {
        const url = 'nodetypes/' + this.encode(nodeTemplate.namespace)
            + '/' + this.encode(nodeTemplate.id) + '/propertiesdefinition/winery/list/';

        this.httpService.get(this.getFullUrl(url)).subscribe(properties => {
            properties.forEach(property => nodeTemplate.properties.push(property.key));
        });
    }

    public loadNodeTemplateInterfaces(namespace: string, nodeType: string): Observable<ToscaInterface[]> {
        const url = 'nodetypes/' + this.encode(namespace)
            + '/' + this.encode(nodeType) + '/interfaces/';

        return this.httpService.get(this.getFullUrl(url));
    }

    public save(data: string) {
        const url = 'servicetemplates/' + this.encode(this.namespace)
            + '/' + this.encode(this.serviceTemplateId) + '/plans/' + this.encode(this.plan) + '/file';

        const requestData = '-----------------------------7da24f2e50046\r\n'
            + 'Content-Disposition: form-data; name=\"file\"; filename=\"file.json\"\r\n'
            + 'Content-type: plain/text\r\n\r\n'
            + data + '\r\n-----------------------------7da24f2e50046--\r\n';

        const headers = new HttpHeaders({ 'Content-Type': 'multipart/form-data; boundary=---------------------------7da24f2e50046' });

        this.httpService.put(this.getFullUrl(url), requestData, { headers: headers })
            .subscribe(response => console.log('save date success'));
    }

    public loadPlan() {
        const url = 'servicetemplates/' + this.encode(this.namespace)
            + '/' + this.encode(this.serviceTemplateId) + '/plans/' + this.encode(this.plan) + '/file';
        this.httpService.get(this.getFullUrl(url)).subscribe(response => {
            const nodes = JSON.stringify(response) === '{}' ? [] : <Node[]>response;
            this.broadcastService.broadcast(this.broadcastService.planModel, nodes);
        });
    }

    private decode(param: string): string {
        return decodeURIComponent(decodeURIComponent(param));
    }

    private encode(param: string): string {
        return encodeURIComponent(encodeURIComponent(param));
    }

    private getFullUrl(relativePath: string) {
        return this.repositoryURL + relativePath;
    }
}
