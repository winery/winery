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
import { Component, OnInit } from '@angular/core';
import { VisualAppearanceService } from './visualAppearance.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { isNullOrUndefined } from 'util';
import { RelationshipTypesVisualsApiData } from './relationshipTypesVisualsApiData';
import { NodeTypesVisualsApiData } from './nodeTypesVisualsApiData';
import { InstanceService } from '../../instance.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ToscaTypes } from '../../../model/enums';

@Component({
    templateUrl: 'visualAppearance.component.html',
    styleUrls: [
        'visualAppearance.component.css'
    ],
    providers: [VisualAppearanceService]
})
export class VisualAppearanceComponent implements OnInit {

    relationshipData: RelationshipTypesVisualsApiData;
    nodeTypeData: NodeTypesVisualsApiData;
    loading = true;
    img16Path: string;
    img50Path: string;
    isRelationshipType = false;
    isNodeType = false;

    constructor(public sharedData: InstanceService,
                private service: VisualAppearanceService,
                private notify: WineryNotificationService) {
    }

    ngOnInit() {
        this.loading = true;
        this.img16Path = this.service.getImg16x16Path();
        this.img50Path = this.service.getImg50x50Path();

        this.isRelationshipType = this.sharedData.toscaComponent.toscaType === ToscaTypes.RelationshipType;
        this.isNodeType = this.sharedData.toscaComponent.toscaType === ToscaTypes.NodeType;

        if (this.isRelationshipType) {
            this.getRelationshipData();
        } else {
            this.getNodeTypeData();
        }
    }

    /**
     * @param type the part of the arrow that should be changed<p>
     *             should be one of
     *             <ul>
     *                 <li>dash
     *                 <li>sourceArrowHead
     *                 <li>targetArrowHead
     *             </ul>
     * @param style the style of the line which should be one of the styles accepted by jsPlumb:<p>
     *              <b>for source-/targetArrowHead</b>
     *              <ul>
     *                  <li>none
     *                  <li>PlainArrow
     *                  <li>Diamond
     *              </ul><b>for dash</b>
     *              <ul>
     *                  <li>plain
     *                  <li>dotted
     *                  <li>dotted2
     *              </ul>
     */
    selectArrowItem(type?: string, style?: string) {
        const hasType = !isNullOrUndefined(type);
        const hasStyle = !isNullOrUndefined(style);
        let dashSelected = false;
        let sourcearrowheadSelected = false;
        let targetarrowheadSelected = false;
        if (hasType && type === 'dash') {
            this.relationshipData.dash = hasStyle ? style : this.relationshipData.dash;
            dashSelected = !this.relationshipData.boolData.dashSelected;
        } else if (hasType && type === 'sourceArrowHead') {
            this.relationshipData.sourceArrowHead = hasStyle ? style : this.relationshipData.sourceArrowHead;
            sourcearrowheadSelected = !this.relationshipData.boolData.sourceArrowHeadSelected;
        } else if (hasType && type === 'targetArrowHead') {
            this.relationshipData.targetArrowHead = hasStyle ? style : this.relationshipData.targetArrowHead;
            targetarrowheadSelected = !this.relationshipData.boolData.targetArrowHeadSelected;
        }
        this.relationshipData.boolData.dashSelected = dashSelected;
        this.relationshipData.boolData.sourceArrowHeadSelected = sourcearrowheadSelected;
        this.relationshipData.boolData.targetArrowHeadSelected = targetarrowheadSelected;
    }

    saveToServer() {
        if (this.isRelationshipType) {
            this.service.saveVisuals(new RelationshipTypesVisualsApiData(this.relationshipData, false)).subscribe(
                data => this.handleResponse(data),
                error => this.handleError(error)
            );
        } else {
            this.service.saveVisuals(new NodeTypesVisualsApiData(this.nodeTypeData)).subscribe(
                data => this.handleResponse(data),
                error => this.handleError(error)
            );
        }
    }

    getRelationshipData() {
        this.service.getData().subscribe(
            data => this.handleRelationshipData(data),
            error => this.handleError(error)
        );
    }

    getNodeTypeData() {
        this.service.getData().subscribe(
            data => this.handleColorData(data),
            error => this.handleError(error)
        );
    }

    handleColorData(data: any) {
        this.nodeTypeData = new NodeTypesVisualsApiData(data);
        this.loading = false;
    }

    handleRelationshipData(data: any) {
        this.relationshipData = new RelationshipTypesVisualsApiData(data, true);
        this.loading = false;
    }

    onUploadSuccess() {
        this.loading = true;
        if (this.isRelationshipType) {
            this.getRelationshipData();
        } else {
            this.getNodeTypeData();
        }
    }

    public get colorLocal() {
        return this.relationshipData.color;
    }

    public set colorLocal(color: string) {
        this.relationshipData.color = color;
        this.saveToServer();
    }

    public get borderColorLocal() {
        return this.nodeTypeData.color;
    }

    public set borderColorLocal(color: string) {
        this.nodeTypeData.color = color;
        this.saveToServer();
    }

    public get hoverColorLocal() {
        return this.relationshipData.hoverColor;
    }

    public set hoverColorLocal(color: string) {
        this.relationshipData.hoverColor = color;
        this.saveToServer();
    }

    private handleResponse(response: HttpResponse<string>) {
        this.loading = false;
        this.notify.success('Successfully saved visual data!');
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message);
    }

}
