/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
import { Component, OnInit, ViewChild } from '@angular/core';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { SelectableInterface, WineryArtifactService } from './artifact.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { NameAndQNameApiData, NameAndQNameApiDataList } from '../../../wineryQNameSelector/wineryNameAndQNameApiData';
import { InstanceService } from '../../instance.service';
import { GenerateArtifactApiData } from '../interfaces/generateArtifactApiData';
import { ModalDirective } from 'ngx-bootstrap';
import { ArtifactApiData, WineryInstance } from '../../../model/wineryComponent';
import { backendBaseURL, hostURL } from '../../../configuration';
import { Router } from '@angular/router';
import { FilesApiData, FilesService } from '../filesTag/files.service';
import { GenerateData } from '../../../wineryComponentExists/wineryComponentExists.component';
import { ToscaTypes } from '../../../model/enums';
import { WineryVersion } from '../../../model/wineryVersion';
import { HttpErrorResponse } from '@angular/common/http';
import { SelectData } from '../../../model/selectData';
import { Utils } from '../../../wineryUtils/utils';
import { SectionService } from '../../../section/section.service';
import { AddComponentValidation } from '../../../wineryAddComponentModule/addComponentValidation';
import { ExistService } from '../../../wineryUtils/existService';
import { WineryAddComponentDataComponent } from '../../../wineryAddComponentDataModule/addComponentData.component';

@Component({
    selector: 'winery-artifact',
    templateUrl: 'artifact.component.html',
    styleUrls: ['artifact.component.css'],
    providers: [WineryArtifactService, FilesService, SectionService]
})
export class WineryArtifactComponent implements OnInit {

    columns: WineryTableColumn[] = [];
    uploadUrl: string;
    name: string;
    loading = true;
    elementToRemove: ArtifactApiData;
    artifactsData: ArtifactApiData[] = [];
    interfacesList: SelectableInterface[];
    newArtifact: GenerateArtifactApiData = new GenerateArtifactApiData();
    artifact: GenerateData = new GenerateData();
    artifactUrl: string;
    artifactTypesList: NameAndQNameApiDataList = { 'classes': null };
    artifactTemplatesList: NameAndQNameApiDataList = { 'classes': null };
    selectedInterface: SelectableInterface;
    selectedOperation: string;
    selectedRadioButton = 'createArtifactTemplate';
    selectedArtifactType: string;
    selectedArtifactTemplate: string;
    filesList: FilesApiData[];
    baseUrl = hostURL;
    fileToRemove: FilesApiData;
    noneSelected = true;
    isImplementationArtifact = false;

    commonColumns: WineryTableColumn[] = [
        { title: 'Name', name: 'name' },
        { title: 'Artifact Template', name: 'artifactRefLocalName' },
        { title: 'Artifact Type', name: 'artifactTypeLocalName' },
        { title: 'Specific Content', name: 'anyText' }
    ];

    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addArtifactModal') addArtifactModal: ModalDirective;
    @ViewChild('uploadFileModal') uploadFileModal: ModalDirective;
    @ViewChild('removeElementModal') removeElementModal: ModalDirective;
    @ViewChild('addComponentData') addComponentData: WineryAddComponentDataComponent;

    private implementationArtifactColumns = [
        { title: 'Interface Name', name: 'interfaceName' },
        { title: 'Operation Name', name: 'operationName' }
    ];
    toscaType = ToscaTypes.ArtifactTemplate;
    valid: boolean;
    validation: any;
    hideHelp = true;
    typeRequired = false;
    types: SelectData[];
    private createComponent: boolean;
    private nodetype: string;

    constructor(private service: WineryArtifactService,
                public sharedData: InstanceService,
                private notify: WineryNotificationService,
                private fileService: FilesService,
                private sectionService: SectionService,
                private existService: ExistService,
                private nodeTypeService: InstanceService,
                private router: Router) {
    }

    ngOnInit() {
        this.columns = this.columns.concat(this.commonColumns);

        this.getArtifacts();
        this.getArtifactTemplates();
        this.getArtifactTypes();
        this.newArtifact.artifactType = '';

        if (this.router.url.includes('implementationartifacts')) {
            this.isImplementationArtifact = true;
            this.columns.splice(1, 0, this.implementationArtifactColumns[0]);
            this.columns.splice(2, 0, this.implementationArtifactColumns[1]);
            this.getInterfacesOfAssociatedType();
        }

        this.name = this.isImplementationArtifact ? 'Implementation' : 'Deployment';
        this.nodeTypeService.getComponentData()
            .subscribe(
                compData => this.handleComponentData(compData)
            );
        this.getTypes();
    }

    onAddClick() {
        this.resetArtifactCreationData();
        if (this.sharedData.toscaComponent.namespace.endsWith('/')) {
            this.artifact.namespace = this.sharedData.toscaComponent.namespace
                .slice(0, this.sharedData.toscaComponent.namespace.length - 1);
        } else {
            this.artifact.namespace = this.sharedData.toscaComponent.namespace;
        }
        this.artifact.namespace = this.artifact.namespace.slice(0, this.sharedData.toscaComponent.namespace.lastIndexOf('/') + 1) + ToscaTypes.ArtifactTemplate;
        this.artifact.name = this.nodetype;
        this.artifact.toscaType = ToscaTypes.ArtifactTemplate;
        this.existCheck();
        this.addComponentData.createArtifactName(this.sharedData.toscaComponent, this.sharedData.currentVersion,
            this.selectedOperation, this.isImplementationArtifact, this.nodetype);
        this.addArtifactModal.show();
    }

    /**
     * handler for clicks on remove button
     * @param data
     */
    onRemoveClick(data: any) {
        if (!data) {
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
            this.selectedArtifactType = value.id;
            this.newArtifact.artifactType = value.id;
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

    addConfirmed() {
        if (this.selectedOperation === '(none)' || !this.selectedOperation) {
            this.selectedOperation = '';
        }
        if (!this.selectedInterface) {
            this.selectedInterface = new SelectableInterface();
            this.selectedInterface.text = '';
        }

        if (this.selectedRadioButton === 'createArtifactTemplate') {
            const version = new WineryVersion('', 1, 1);
            this.newArtifact.autoCreateArtifactTemplate = 'true';
            this.newArtifact.artifactTemplateNamespace = this.artifact.namespace;
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
                this.handleArtifactsData(data);
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

    handleArtifactsData(data: ArtifactApiData[]) {
        this.artifactsData = data;
        this.artifactsData = this.artifactsData.map(
            obj => {
                if (obj.artifactType) {
                    obj.artifactTypeLocalName = '<a href="#' + this.createArtifactTypeUrl(obj.artifactType) +
                        '">' + this.getLocalName(obj.artifactType) + '</a>';
                } else {
                    obj.artifactTypeLocalName = '';
                }
                if (obj.artifactRef) {
                    obj.artifactRefLocalName = '<a href="#' + this.createArtifactTemplateUrl(obj.artifactRef) +
                        '">' + this.getLocalName(obj.artifactRef) + '</a>';
                } else {
                    obj.artifactRefLocalName = '';
                }
                if (obj.any) {
                    obj.anyText = '';
                }
                if (!obj.interfaceName) {
                    obj.interfaceName = '';
                }
                if (!obj.operationName) {
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
            error => this.notify.error(error.message)
        );
    }

    loadFiles(templateUrl: string) {
        this.fileService.getFiles(templateUrl)
            .subscribe(
                data => this.filesList = data.files,
                error => this.notify.error(error.message) + 'error while loading files!'
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
        if (qName) {
            return qName.slice(qName.indexOf('}') + 1);
        } else {
            return '';
        }
    }

    private handleInterfaceData(data: SelectableInterface[]) {
        this.interfacesList = data;
    }

    private handleArtifactTypeData(data: NameAndQNameApiData[]) {
        this.artifactTypesList.classes = data;
    }

    private handleArtifactTemplateData(data: NameAndQNameApiData[]) {
        this.artifactTemplatesList.classes = data;
    }

    private handlePostResponse() {
        this.loading = false;
        this.notify.success('successfully created ' + this.name + ' Artifact ' + this.newArtifact.artifactName);
        if (this.selectedRadioButton === 'createArtifactTemplate') {
            this.loadFiles(this.uploadUrl);
            this.uploadFileModal.show();
        }
        this.getArtifacts();
    }

    private resetArtifactCreationData() {
        this.newArtifact = new GenerateArtifactApiData();
        this.newArtifact.artifactType = '';
        this.selectedRadioButton = 'createArtifactTemplate';
        this.selectedInterface = null;
        this.selectedOperation = '';
        this.selectedArtifactType = '';
        this.selectedArtifactTemplate = '';
        this.artifactUrl = '';
        this.uploadUrl = '';
        this.artifact.name = '';

    }

    private handleDelete() {
        this.notify.success('Successfully deleted ' + this.fileToRemove.name);
        this.fileToRemove = null;
        this.loadFiles(this.uploadUrl);
        this.loading = false;
    }

    private showError(error: HttpErrorResponse) {
        this.notify.error(error.message);
        this.loading = false;
    }

    private makeArtifactUrl() {
        this.artifactUrl = backendBaseURL + '/artifacttemplates/' + encodeURIComponent(encodeURIComponent(
            this.newArtifact.artifactTemplateNamespace)) + '/' + this.newArtifact.artifactTemplateName + '/';
        this.uploadUrl = this.artifactUrl + 'files';
    }

    private getNamespaceAndLocalNameFromQName(qname: string): { namespace: string; localname: string; } {
        const i = qname.indexOf('}');
        return {
            namespace: qname.substr(1, i - 1),
            localname: qname.substr(i + 1)
        };
    }

    private createArtifactTemplateUrl(qname: string): string {
        const nameAndNamespace = this.getNamespaceAndLocalNameFromQName(qname);
        return '/artifacttemplates/' + encodeURIComponent(encodeURIComponent(nameAndNamespace.namespace))
            + '/' + nameAndNamespace.localname + '/';

    }

    private createArtifactTypeUrl(qname: string): string {
        const nameAndNamespace = this.getNamespaceAndLocalNameFromQName(qname);
        return '/artifacttypes/' + encodeURIComponent(encodeURIComponent(nameAndNamespace.namespace))
            + '/' + nameAndNamespace.localname + '/';

    }

    private getTypes(componentType?: SelectData) {
        const typesUrl = Utils.getTypeOfTemplateOrImplementation(this.toscaType);
        if (typesUrl && !componentType) {
            this.loading = true;
            this.typeRequired = true;
            this.sectionService.getSectionData('/' + typesUrl + '?grouped=angularSelect')
                .subscribe(
                    data => this.handleTypes(data),
                    error => this.showError(error)
                );
        } else {
            this.typeRequired = false;
        }
    }

    private handleTypes(types: SelectData[]): void {
        this.types = types.length > 0 ? types : null;
        this.loading = false;
    }

    setNewArtifactName(name: string) {
        this.newArtifact.artifactTemplateName = name;
    }

    setNewArtifactNamespace(namespace: string) {
        this.artifact.namespace = namespace;
    }

    setValid(valid: boolean) {
        this.valid = !valid;
    }

    private existCheck() {
        const newComponentVersion: WineryVersion = new WineryVersion('', 1, 1);
        const newComponentFinalName = this.artifact.name + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + newComponentVersion.toString();
        this.artifactUrl = backendBaseURL + '/artifacttemplates/' + encodeURIComponent(encodeURIComponent(
            this.artifact.namespace)) + '/' + newComponentFinalName + '/';

        this.existService.check(this.artifactUrl)
            .subscribe(
                () => this.validate(false),
                () => this.validate(true)
            );
    }

    private validate(create: boolean) {
        this.validation = new AddComponentValidation();
        this.createComponent = create;
        this.validation.noDuplicatesAllowed = !this.createComponent;
    }

    interfaceAndOperation() {
        this.addComponentData.createArtifactName(this.sharedData.toscaComponent, this.sharedData.currentVersion,
            this.selectedOperation, this.isImplementationArtifact, this.nodetype);
    }

    private handleComponentData(compData: WineryInstance) {
        const node = compData.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].nodeType;
        this.nodetype = node.substring(node.indexOf('}') + 1, node.indexOf('_'));
    }

    clearOperation() {
        this.selectedOperation = '';
        this.interfaceAndOperation();
    }
}
