/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 *     Lukas Balzer - added fileUploader and color picker component
 */

import { Component, OnInit, ViewChild } from '@angular/core';
import { VisualAppearanceService } from './visualAppearance.service';
import { FileUploader, FileItem } from 'ng2-file-upload';
import { NotificationService } from '../../notificationModule/notification.service';
import { isNullOrUndefined } from 'util';
import { RelationshipTypesVisualsApiData } from './relationshipTypesVisualsApiData';
import { NodeTypesVisualsApiData } from './nodeTypesVisualsApiData';

@Component({
    selector: 'winery-instance-visualAppearance',
    templateUrl: 'visualAppearance.component.html',
    styleUrls: [
        'visualAppearance.component.css'
    ],
    providers: [VisualAppearanceService]
})
export class VisualAppearanceComponent implements OnInit {
    relationshipData: RelationshipTypesVisualsApiData;
    nodeTypeData: NodeTypesVisualsApiData;
    color = '#f00';
    isColorLoaded = false;
    loading = true;
    img16uploader: FileUploader;
    img50uploader: FileUploader;
    fileItem: FileItem;
    img16Path: string;
    img50Path: string;
    hasImg16DropZoneOver = false;
    hasImg50DropZoneOver = false;
    @ViewChild('upload16Modal') upload16Modal: any;
    @ViewChild('upload50Modal') upload50Modal: any;

    constructor(private service: VisualAppearanceService,
                private notify: NotificationService) {
    }

    ngOnInit() {
        this.loading = true;
        this.img16Path = this.service.getImg16x16Path();
        this.img50Path = this.service.getImg50x50Path();
        this.img16uploader = this.service.getUploader(this.img16Path);
        this.img50uploader = this.service.getUploader(this.img50Path);
        if (this.service.isNodeType) {
            this.getNodeTypeData();
        } else {
            this.getRelationshipdata();
        }
    }

    /**
     *
     * @param type the part of the arrow that should be changed<p>
     *             should be one of<ul>
     *                 <li>dash
     *                 <li>sourcearrowhead
     *                 <li>targetarrowhead
     *                 </ul>
     * @param style the style of the line which should be one of the styles accepted by jsPlumb:<p>
     *              <b>for source-/targetarrowhead</b>
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
        let shouldOpen = false;
        let hasType = !isNullOrUndefined(type);
        let hasStyle = !isNullOrUndefined(style);
        let dashSelected = false;
        let sourcearrowheadSelected = false;
        let targetarrowheadSelected = false;
        if (hasType && type === 'dash') {
            this.relationshipData.dash = hasStyle ? style : this.relationshipData.dash;
            dashSelected = !this.relationshipData.boolData.dashSelected;
        } else if (hasType && type === 'sourcearrowhead') {
            this.relationshipData.sourcearrowhead = hasStyle ? style : this.relationshipData.sourcearrowhead;
            sourcearrowheadSelected = !this.relationshipData.boolData.sourcearrowheadSelected;
        } else if (hasType && type === 'targetarrowhead') {
            this.relationshipData.targetarrowhead = hasStyle ? style : this.relationshipData.targetarrowhead;
            targetarrowheadSelected = !this.relationshipData.boolData.targetarrowheadSelected;
        }
        this.relationshipData.boolData.dashSelected = dashSelected;
        this.relationshipData.boolData.sourcearrowheadSelected = sourcearrowheadSelected;
        this.relationshipData.boolData.targetarrowheadSelected = targetarrowheadSelected;
    }

    onUpload(uploader: FileUploader, event: any, modal?: any): boolean {
        if (!isNullOrUndefined(uploader.queue[0])) {
            this.loading = true;
            this.fileItem = uploader.queue[0];
            if (!this.fileItem._file.type.includes('image')) {
                uploader.clearQueue();
                this.loading = false;
                this.notify.error('Please upload an image file');
            } else {
                this.fileItem.upload();
                uploader.onCompleteItem = (item: any, response: string, status: number, headers: any) => {
                    uploader.clearQueue();
                    this.loading = false;

                    if (!isNullOrUndefined(modal)) {
                        modal.hide();
                    }
                    if (status === 204) {
                        this.notify.success('Successfully saved Icon');
                    } else {
                        this.notify.error('Error while uploading Icon');
                    }
                    return {item, response, status, headers};
                };
            }
        }
        return event;
    }

    saveToServer() {
        if (this.service.isNodeType) {
            this.service.saveVisuals(new NodeTypesVisualsApiData(this.nodeTypeData)).subscribe(
                data => this.handleResponse(data),
                error => this.handleError(error)
            );
        } else {
            this.service.saveVisuals(new RelationshipTypesVisualsApiData(this.relationshipData, false)).subscribe(
                data => this.handleResponse(data),
                error => this.handleError(error)
            );
        }
    }

    getRelationshipdata() {
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
        return this.relationshipData.hovercolor;
    }

    public set hoverColorLocal(color: string) {
        this.relationshipData.hovercolor = color;
        this.saveToServer();
    }

    private handleResponse(response: any) {
        this.loading = false;
        this.notify.success('Successfully saved visual data!');
    }

    private handleError(error: any): void {
        this.loading = false;
        this.notify.error(error);
    }

}
