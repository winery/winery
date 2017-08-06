/**
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     ZTE - initial API and implementation and/or initial documentation
 */

import { Injectable } from '@angular/core';
import { Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { isNullOrUndefined } from 'util';

import { NodeTemplate } from '../model/nodetemplate';
import { Operation } from '../model/operation';
import { PageParameter } from '../model/page-parameter';
import { Node } from '../model/workflow/node';
import { HttpService } from '../util/http.service';
import { BroadcastService } from './broadcast.service';

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

        if (!isNullOrUndefined(this.repositoryURL)) {
            this.loadPlan();
        }
    }

    public loadNodeTemplates(): Observable<NodeTemplate[]> {
        const url = 'servicetemplates/' + this.encode(this.namespace)
            + '/' + this.encode(this.serviceTemplateId) + '/topologytemplate/';

        return this.httpService.get(this.getFullUrl(url))
            .map(this.transferResponse2NodeTemplate);
    }

    private transferResponse2NodeTemplate(response) {
        const nodeTemplates = [];
        for (const key in response.nodeTemplates) {
            if (response.nodeTemplates.hasOwnProperty(key)) {
                const nodeTemplate = response.nodeTemplates[key];
                nodeTemplates.push(new NodeTemplate(
                    nodeTemplate.id,
                    nodeTemplate.name,
                    nodeTemplate.type.replace(/^\{(.+)\}(.+)/, '$2'),
                    nodeTemplate.type.replace(/^\{(.+)\}(.+)/, '$1')));
            }
        }
        return nodeTemplates;
    };

    public loadTopologyProperties(nodeTemplate: NodeTemplate) {
        const url = 'nodetypes/' + this.encode(nodeTemplate.namespace)
            + '/' + this.encode(nodeTemplate.id) + '/propertiesdefinition/winery/list/';

        this.httpService.get(this.getFullUrl(url)).subscribe(properties => {
            properties.forEach(property => nodeTemplate.properties.push(property.key));
        });
    }

    public loadNodeTemplateInterfaces(namespace: string, nodeType: string): Observable<string[]> {
        const url = 'nodetypes/' + this.encode(namespace)
            + '/' + this.encode(nodeType) + '/interfaces/';

        return this.httpService.get(this.getFullUrl(url));
    }

    public loadNodeTemplateOperations(namespace: string,
                                      nodeType: string,
                                      interfaceName: string): Observable<string[]> {
        const url = 'nodetypes/' + this.encode(namespace)
            + '/' + this.encode(nodeType) + '/interfaces/' + this.encode(interfaceName) + '/operations/';

        return this.httpService.get(this.getFullUrl(url));
    }

    public loadNodeTemplateOperationParameter(namespace: string,
                                              nodeType: string,
                                              interfaceName: string,
                                              operation: string): Promise<{input:string[], output:string[]}> {
        const relativePath = 'nodetypes/' + this.encode(namespace) + '/' + this.encode(nodeType)
            + '/interfaces/' + this.encode(interfaceName) + '/operations/' + this.encode(operation) + '/';

        // input parameters
        const inputPromise: Promise<string[]> = this.httpService
            .get(this.getFullUrl(relativePath + 'inputparameters')).toPromise();

        // output parameters
        const outputPromise: Promise<string[]> = this.httpService
            .get(this.getFullUrl(relativePath + 'outputparameters')).toPromise();

        return Promise.all([inputPromise, outputPromise]).then(params => {

            return { input: params[0], output: params[1] };
        });
    }

    public save(data: string) {
        const url = 'servicetemplates/' + this.encode(this.namespace)
            + '/' + this.encode(this.serviceTemplateId) + '/plans/' + this.encode(this.plan) + '/file';

        const requestData = '-----------------------------7da24f2e50046\r\n'
            + 'Content-Disposition: form-data; name=\"file\"; filename=\"file.json\"\r\n'
            + 'Content-type: plain/text\r\n\r\n'
            + data + '\r\n-----------------------------7da24f2e50046--\r\n';

        const headers = new Headers({ 'Content-Type': 'multipart/form-data; boundary=---------------------------7da24f2e50046' });
        const options = new RequestOptions({ headers });

        this.httpService.put(this.getFullUrl(url), requestData, options)
            .subscribe(response => console.log('save date success'));
    }

    public loadPlan() {
        const url = 'servicetemplates/' + this.encode(this.namespace)
            + '/' + this.encode(this.serviceTemplateId) + '/plans/' + this.encode(this.plan) + '/file';
        this.httpService.get(this.getFullUrl(url)).subscribe(response => {
            const nodes = JSON.stringify(response) === '{}' ? [] : <Node[]>response;
            console.log('load plan success');
            console.log(nodes);
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
