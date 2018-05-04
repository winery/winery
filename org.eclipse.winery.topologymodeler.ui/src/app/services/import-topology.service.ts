/*******************************************************************************
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

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import { backendBaseURL, hostURL } from '../models/configuration';
import { urlElement } from '../models/enums';
import { Headers, Http, RequestOptions } from '@angular/http';
import { NodeRelationshipTemplatesGeneratorService } from './node-relationship-templates-generator.service';
import { TNodeTemplate, TRelationshipTemplate, Visuals } from '../models/ttopology-template';
import { NgRedux } from '@angular-redux/store';
import { WineryActions } from '../redux/actions/winery.actions';
import { IWineryState } from '../redux/store/winery.store';
import { QName } from '../models/qname';

@Injectable()
export class ImportTopologyService {

    nodeTemplates: Array<TNodeTemplate> = [];
    relationshipTemplates: Array<TRelationshipTemplate> = [];

    readonly headers = new Headers({ 'Accept': 'application/json' });
    readonly options = new RequestOptions({ headers: this.headers });

    constructor(private http: Http,
                private nodeRelationshipTemplatesGeneratorService: NodeRelationshipTemplatesGeneratorService,
                private ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions) {
    }

    /**
     * Requests data from the server
     * @param serviceTemplate   the selected service template to fetch data from
     * @returns data  The JSON from the server
     */
    requestServiceTemplate(serviceTemplate: any): Observable<Object> {
        const qName = new QName(serviceTemplate.qName);
        const url = backendBaseURL + urlElement.ServiceTemplates + encodeURIComponent(encodeURIComponent(qName.nameSpace))
            + '/' + qName.localName + urlElement.TopologyTemplate;
        // This is required because the information has to be returned together
        return this.http.get(url, this.options).map(res => res.json());
    }

    /**
     * Subscribes to the response of the server and pushes the node and relationship templates
     * into the redux store and reloads the page
     * @param serviceTemplate   the selected service template to fetch data from
     * @param nodeVisuals   the node visuals
     * @param allNodeTemplates   the node templates already present in the service template
     */
    importTopologyTemplate(serviceTemplate: any, nodeVisuals: Visuals[], allNodeTemplates: Array<TNodeTemplate>,
    allRelationshipTemplates: Array<TRelationshipTemplate>): void {
        // ServiceTemplate / TopologyTemplate
        this.requestServiceTemplate(serviceTemplate).subscribe(data => {
            // add JSON to Promise, WineryComponent will subscribe to its Observable
            const nodeAndRelationshipTemplates = this.nodeRelationshipTemplatesGeneratorService.generateNodeAndRelationshipTemplates(
                data['nodeTemplates'], data['relationshipTemplates'], nodeVisuals, allRelationshipTemplates);
            this.assignUniqueIds(allNodeTemplates, nodeAndRelationshipTemplates[0], nodeAndRelationshipTemplates[1]);
            nodeAndRelationshipTemplates[0].forEach(nodeTemplate => {
                this.ngRedux.dispatch(this.actions.saveNodeTemplate(nodeTemplate));
            });
            nodeAndRelationshipTemplates[1].forEach(relationshipTemplate => {
                this.ngRedux.dispatch(this.actions.saveRelationship(relationshipTemplate));
            });
        });
    }

    /**
     * Assigns unique node and relationship template ids, required
     * @param allNodeTemplates   the node templates already present in the service template
     * @param importedNodeTemplates   the imported node templates
     * @param importedRelationshipTemplates   the imported relationship templates
     */
    private assignUniqueIds(allNodeTemplates: Array<TNodeTemplate>, importedNodeTemplates: Array<TNodeTemplate>,
                            importedRelationshipTemplates: Array<TRelationshipTemplate>): void {
        if (allNodeTemplates.length > 0) {
            const allCheckedNodeTypes = [];
            // iterate from back to front because only the last added instance of a node type is important
            // e.g. Node_8 so to increase to Node_9 only the 8 is important which is in the end of the array
            for (let i = allNodeTemplates.length - 1; i >= 0; i--) {
                // get type of node Template
                // eliminate whitespaces from both strings, important for string comparison
                const nodeTemplateType = allNodeTemplates[i].type.split('}').pop().replace(/\s+/g, '');
                if (!allCheckedNodeTypes.find(nodeType => nodeType === nodeTemplateType)) {
                    let newNumberOfNodeIdOffset = 1;
                    importedNodeTemplates.forEach(importedNodeTemplate => {
                        const importedNodeTemplateType = importedNodeTemplate.type.split('}').pop().replace(/\s+/g, '');
                        if (nodeTemplateType === importedNodeTemplateType) {
                            const idOfCurrentNode = allNodeTemplates[i].id;
                            const numberOfNodeId = parseInt(idOfCurrentNode.substring(nodeTemplateType.length + 1),
                                10) + newNumberOfNodeIdOffset;
                            let newNodeId;
                            if (numberOfNodeId) {
                                newNodeId = nodeTemplateType.concat('_', numberOfNodeId.toString());
                            } else {
                                newNodeId = nodeTemplateType.concat('_', '2');
                            }
                            // Adjusting the node template id in the relationship templates
                            importedRelationshipTemplates.forEach(relTemplate => {
                                if (importedNodeTemplate.id === relTemplate.sourceElement.ref) {
                                    relTemplate.sourceElement.ref = newNodeId;
                                } else if (importedNodeTemplate.id === relTemplate.targetElement.ref) {
                                    relTemplate.targetElement.ref = newNodeId;
                                }
                            });
                            importedNodeTemplate.id = newNodeId;
                            newNumberOfNodeIdOffset += 1;
                        }
                    });
                    allCheckedNodeTypes.push(nodeTemplateType);
                }
            }
        }
    }

    /**
     * Requests all topology template ids
     * @returns {Observable<string>}
     */
    requestAllTopologyTemplates(): Observable<any> {
        const url = hostURL + urlElement.Winery + urlElement.ServiceTemplates;
        return this.http.get(url, this.options)
            .map(res => res.json());
    }

}
