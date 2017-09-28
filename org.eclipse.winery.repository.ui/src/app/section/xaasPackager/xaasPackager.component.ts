/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, DoCheck, ViewChild } from '@angular/core';
import { ArtifactTypesAndInfrastructureNodetypes, PackagerService } from './xaasPackagerService';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';
import { ModalDirective } from 'ngx-bootstrap';
import { isNull, isNullOrUndefined } from 'util';
import { SelectItem } from 'ng2-select';

@Component({
    selector: 'winery-xaas-packager',
    templateUrl: 'xaasPackager.component.html',
    styleUrls: [
        'xaasPackager.component.css'
    ],
    providers: [
        PackagerService
    ]
})
export class XaasPackagerComponent implements DoCheck {

    isModalShown = false;
    isFormValid = false;

    nodeTypes: string[];
    infrastructureNodetypes: string[];
    artifactTypes: string[];
    tagItems: string[];

    selectedInfracstuctureNodeType: string = null;
    selectedArtifactType: string;
    selectedNodeTypes: string[];
    file: File;

    value: SelectItem[];

    @ViewChild('createFromArtifactModal') createFromArtifactModal: ModalDirective;

    constructor(private service: PackagerService,
                private notify: WineryNotificationService) {
    }

    ngDoCheck(): void {
        if (!isNullOrUndefined(this.selectedNodeTypes) && (this.selectedNodeTypes.length !== 0) && !isNullOrUndefined(this.selectedArtifactType)) {
            this.isFormValid = true;
        } else {
            this.isFormValid = false;
        }
    }

    onAddClick() {
        const formData: FormData = new FormData();
        if (!isNullOrUndefined(this.file)) {
            formData.append('file', this.file, this.file.name);
        }
        if (!isNull(this.selectedArtifactType)) {
            formData.append('artifactType', this.selectedArtifactType);
        }
        if (!isNullOrUndefined(this.selectedNodeTypes)) {
            for (const nodetype of this.selectedNodeTypes) {
                formData.append('nodeTypes', nodetype);
            }
        }

        if (!isNullOrUndefined(this.tagItems)) {
            for (const tag of this.tagItems) {
                formData.append('tags', tag);
            }
        } else {
            formData.append('tags', '');
        }

        if (!isNullOrUndefined(this.selectedInfracstuctureNodeType)) {
            formData.append('infrastructureNodeType', this.selectedInfracstuctureNodeType);
        }

        this.service.createTempalteFromArtifact(formData).subscribe(
            data => this.notify.success('Service Template successfully created!'),
            error => this.handleError(error)
        );

        this.resetArtifactCreationData();
        this.createFromArtifactModal.hide();
    }

    public onCreateFromArtifact() {
        this.service.getNodetypes().subscribe(
            data => this.nodeTypes = data.map(
                obj => {
                    if (!isNullOrUndefined(obj.qName)) {
                        return obj.qName;
                    }
                }),
            error => this.handleError(error)
        );

        this.service.getArtifactTpesAndInfrastructureNodetypes().subscribe(
            data => this.handleArtifactTypeAndInfrastructureNodetypesData(data),
            error => this.handleError(error)
        );
        this.isModalShown = true;
    }

    public refreshValue(value: SelectItem[]): void {
        this.value = value;
        this.selectedNodeTypes = this.itemsToStringArray(value);
    }

    public refreshSelectedArtifactType(value: SelectItem) {
        this.selectedArtifactType = value.text;
    }

    public refreshSelectedInfrastructureNodeType(value: SelectItem) {
        this.selectedInfracstuctureNodeType = value.text;
    }

    public itemsToString(value: Array<SelectItem> = []): string {
        return value
            .map((item: any) => {
                return item.text;
            }).join(',');
    }

    public itemsToStringArray(value: Array<SelectItem> = []): string[] {

        return value.map((item: any) => {
            return item.text;
        });
    }

    public fileChange(event: any) {
        const fileList: FileList = event.target.files;
        if (fileList.length > 0) {
            this.file = fileList[0];
        }
    }

    public onHidden(): void {
        this.isModalShown = false;
    }

    public hideCreateFromArtifactModal() {
        this.resetArtifactCreationData();
        this.createFromArtifactModal.hide();
    }

    public resetArtifactCreationData() {
        this.selectedInfracstuctureNodeType = null;
        this.selectedArtifactType = null;
        this.selectedNodeTypes = [];
        this.tagItems = [];
        this.value = [];
        this.file = null;
    }

    private handleArtifactTypeAndInfrastructureNodetypesData(data: ArtifactTypesAndInfrastructureNodetypes) {
        this.artifactTypes = data.artifactTypes;
        this.infrastructureNodetypes = data.infrastructureNodeTypes;
    }

    private handleError(error: any) {
        this.notify.error(error);
    }
}

export class NodeTypeData {
    namespace: string;
    id: string;
    name: string;
    qName: string;
}
