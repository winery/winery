/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier, Tino Stadelmaier - initial API and implementation
 */
import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { WineryTableColumn } from '../wineryTableModule/wineryTable.component';
import { WineryArtifactService } from './artifact.service';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { NameAndQNameApiData, NameAndQNameApiDataList } from '../wineryQNameSelector/wineryNameAndQNameApiData';
import { InstanceService } from '../instance/instance.service';
import { ExistService } from '../wineryUtils/existService';
import { InterfacesApiData } from '../instance/sharedComponents/interfaces/interfacesApiData';
import { GenerateArtifactApiData } from '../instance/sharedComponents/interfaces/generateArtifactApiData';
import { ModalDirective } from 'ngx-bootstrap';
import { ArtifactApiData } from '../wineryInterfaces/wineryComponent';
import { backendBaseURL, hostURL } from '../configuration';
import { FilesApiData, WineryArtifactFilesService } from './artifact.files.service.';

@Component({
    selector: 'winery-artifact',
    templateUrl: 'artifact.component.html',
    providers: [WineryArtifactService, WineryArtifactFilesService]
})
export class WineryArtifactComponent implements OnInit {

    columns: WineryTableColumn[] = [];
    URL: string;
    uploadUrl: string;
    name: string;
    loading = true;
    elementToRemove: ArtifactApiData;
    artifactsData: ArtifactApiData[];
    interfacesList: InterfacesApiData[];
    newArtifact: GenerateArtifactApiData = new GenerateArtifactApiData();
    artifact: GenerateData = new GenerateData();
    artifactUrl: string;
    artifactTypesList: NameAndQNameApiDataList = { 'classes': null };
    artifactTemplatesList: NameAndQNameApiDataList = { 'classes': null };
    selectedInterface: InterfacesApiData;
    selectedOperation: string;
    selectedRadioButton = 'createArtifactTemplate';
    selectedArtifactType: string;
    selectedArtifactTemplate: string;
    filesList: FilesApiData[];
    baseUrl = hostURL;
    fileToRemove: FilesApiData;
    noneSelected = true;
    _isDeploymentArtifact = false;

    @Input()
    set isDeploymentArtifact(pIsDeploymentArtifact: boolean) {
        this.columns = this.columns.concat(this.commonColumns);
        this._isDeploymentArtifact = pIsDeploymentArtifact;
        if (!pIsDeploymentArtifact) {
            this.columns.splice(1, 0, this.implementationArtifactColumns[0]);
            this.columns.splice(2, 0, this.implementationArtifactColumns[1]);
            this.URL = 'implementationartifacts/';
            this.name = 'Implementation';
        } else {
            this.URL = 'deploymentartifacts/';
            this.name = 'Deployment';
        }
    };

    @Input() title: string;
    @Input() artifactresources = {};

    @Output() addClicked = new EventEmitter();
    @Output() removeClicked = new EventEmitter();

    commonColumns = [
        { title: 'Name', name: 'name' },
        { title: 'Artifact Template', name: 'artifactRefLocalName' },
        { title: 'Artifact Type', name: 'artifactTypeLocalName' },
        { title: 'Specific Content', name: 'anyText' }
    ];

    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addArtifactModal') addArtifactModal: ModalDirective;
    @ViewChild('uploadFileModal') uploadFileModal: ModalDirective;
    @ViewChild('removeElementModal') removeElementModal: ModalDirective;

    private implementationArtifactColumns = [
        { title: 'Interface Name', name: 'interfaceName' },
        { title: 'Operation Name', name: 'operationName' }
    ];

    constructor(private service: WineryArtifactService,
                private sharedData: InstanceService,
                private notify: WineryNotificationService,
                private existService: ExistService,
                private fileService: WineryArtifactFilesService) {
    }

    ngOnInit() {
        this.getArtifacts();
        this.getArtifactTemplates();
        this.getArtifactTypes();
        this.newArtifact.artifactType = '';
        if (!this.isDeploymentArtifact) {
            this.getInterfacesOfAssociatedType();
        }

    }

    onAddClick() {
        if (this.sharedData.selectedNamespace.endsWith('/')) {
            this.artifact.namespace = this.sharedData.selectedNamespace.slice(0, this.sharedData.selectedNamespace.length - 1);
        } else {
            this.artifact.namespace = this.sharedData.selectedNamespace;
        }
        this.artifact.selectedResource = 'Artifact';
        this.artifact.selectedResourceType = 'Template';
        this.artifact.name = this.sharedData.selectedComponentId + 'Artifact';
        this.addClicked.emit();
        this.addArtifactModal.show();
    }

    /**
     * handler for clicks on remove button
     * @param data
     */
    onRemoveClick(data: any) {
        if (isNullOrUndefined(data)) {
            return;
        } else {
            this.elementToRemove = data;
            this.confirmDeleteModal.show();
        }
    }

    onSelectedArtifactTypeChanged(value: any) {
        if (value === '(none)') {
            this.selectedArtifactType = '';
            this.newArtifact.artifactType = '';
            this.noneSelected = true;
        } else {
            this.noneSelected = false;
            this.selectedArtifactType = value;
            this.newArtifact.artifactType = value;
        }
    }

    onSelectedArtifactTemplateChanged(value: any) {
        if (value === '(none)') {
            this.noneSelected = true;
            this.selectedArtifactTemplate = '';
            this.newArtifact.artifactTemplate = '';
        } else {
            this.noneSelected = false;
            this.selectedArtifactTemplate = value;
            this.newArtifact.artifactTemplate = value;
        }
    }

    onCreateArtifactTemplateClicked() {
        this.newArtifact.autoCreateArtifactTemplate = 'true';
    }

    cancelBtnClicked() {
        this.addArtifactModal.hide();
    }

    addConfirmed() {
        if (this.selectedOperation === '(none)' || isNullOrUndefined(this.selectedOperation)) {
            this.selectedOperation = '';
            console.log('this.selectedOperation = ' + this.selectedOperation);
        }
        if (isNullOrUndefined(this.selectedInterface)) {
            console.log('selectedInterface is NULL or undefined');
            this.selectedInterface = new InterfacesApiData();
            this.selectedInterface.text = '';
            console.log('selectedInterface.text = ' + this.selectedInterface.text);
        }

        if (this.selectedRadioButton === 'createArtifactTemplate') {
            this.newArtifact.autoCreateArtifactTemplate = 'true';
            this.newArtifact.artifactTemplateName = this.artifact.name ? this.artifact.name : '';
            this.newArtifact.artifactTemplateNamespace = this.artifact.namespace ? this.artifact.namespace : '';
            this.makeArtifactUrl();
        } else if (this.selectedRadioButton === 'linkArtifactTemplate') {
            this.newArtifact.autoCreateArtifactTemplate = '';

        } else if (this.selectedRadioButton === 'skipArtifactTemplate') {
            this.newArtifact.autoCreateArtifactTemplate = '';

        }
        this.newArtifact.interfaceName = this.selectedInterface.text;
        this.newArtifact.operationName = this.selectedOperation;
        this.newArtifact.javaPackage = '';
        this.createNewImplementationArtifact();
        this.addArtifactModal.hide();
    }

    createNewImplementationArtifact() {
        this.loading = true;
        this.service.createNewArtifact(this.newArtifact).subscribe(
            data => this.handlePostResponse(),
            error => this.showError(error)
        );
    }

    getArtifacts() {
        this.service.getAllArtifacts().subscribe(
            data => {
                this.handleData(data);
            },
            error => this.showError(error)
        );
    }

    getInterfacesOfAssociatedType() {
        this.service.getInterfacesOfAssociatedType().subscribe(
            data => this.handleInterfaceData(data),
            error => this.showError(error)
        );
    }

    getArtifactTypes() {
        this.service.getAllArtifactTypes().subscribe(
            data => this.handleArtifactTypeData(data),
            error => this.showError(error)
        );
    }

    getArtifactTemplates() {
        this.service.getAllArtifactTemplates().subscribe(
            data => this.handleArtifactTemplateData(data),
            error => this.showError(error)
        );
    }

    handleData(data: ArtifactApiData[]) {
        this.artifactsData = data;
        this.artifactsData = this.artifactsData.map(
            obj => {
                obj.artifactTypeLocalName = this.getLocalName(obj.artifactType);
                obj.artifactRefLocalName = this.getLocalName(obj.artifactRef);
                if (!isNullOrUndefined(obj.any)) {
                    obj.anyText = '';
                }
                if (isNullOrUndefined(obj.interfaceName)) {
                    obj.interfaceName = '';
                }
                if (isNullOrUndefined(obj.operationName)) {
                    obj.operationName = '';
                }
                return obj;
            });
        this.loading = false;
    }

    removeConfirmed() {
        this.service.deleteArtifact(this.elementToRemove.name).subscribe(
            data => {
                this.notify.success('Artifact deleted');
                this.getArtifacts();
            },
            error => this.notify.error(error.toString())
        );
    }

    loadFiles(templateUrl: string) {
        this.fileService.getFiles(templateUrl)
            .subscribe(
                data => this.filesList = data.files,
                error => this.notify.error(error.toString() + 'error from loadfiles()')
            );
    }

    deleteFile(file: FilesApiData) {
        this.fileToRemove = file;
        this.removeElementModal.show();
    }

    onRemoveElement() {
        this.loading = true;
        this.fileService.delete(this.fileToRemove)
            .subscribe(
                data => this.handleDelete(),
                error => this.showError(error)
            );
    }

    private getLocalName(qName: string): string {
        if (!isNullOrUndefined(qName)) {
            return qName.slice(qName.indexOf('}') + 1);
        } else {
            return '';
        }
    }

    private handleInterfaceData(data: InterfacesApiData[]) {
        this.interfacesList = data;
    }

    private handleArtifactTypeData(data: NameAndQNameApiData[]) {
        this.artifactTypesList.classes = data;
    }

    private handleArtifactTemplateData(data: NameAndQNameApiData[]) {
        this.artifactTemplatesList.classes = data;
    }

    private handlePostResponse() {
        this.getArtifacts();
        this.loadFiles(this.uploadUrl);
        this.loading = false;
        if (this.selectedRadioButton === 'createArtifactTemplate') {
            this.uploadFileModal.show();
        }
    }

    private handleDelete() {
        this.notify.success('Successfully deleted ' + this.fileToRemove.name);
        this.fileToRemove = null;
        this.loadFiles(this.uploadUrl);
        this.loading = false;
    }

    private showError(error: any) {
        this.notify.error(error);
        this.loading = false;
    }

    private makeArtifactUrl() {
        this.artifactUrl = backendBaseURL + '/artifacttemplates/' + encodeURIComponent(encodeURIComponent(
            this.newArtifact.artifactTemplateNamespace)) + '/' + this.newArtifact.artifactTemplateName + '/';
        this.uploadUrl = this.artifactUrl + 'files/';
    }
}

export class GenerateData {
    createComponent: boolean;
    namespace: string;
    name: string;
    selectedResource: string;
    selectedResourceType: string;
    url: string;
}
