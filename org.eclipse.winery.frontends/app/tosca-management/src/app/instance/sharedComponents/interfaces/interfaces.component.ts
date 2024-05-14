/*******************************************************************************
 * Copyright (c) 2017-2023 Contributors to the Eclipse Foundation
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
import { backendBaseURL } from '../../../configuration';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { ExistService } from '../../../wineryUtils/existService';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { InstanceService } from '../../instance.service';
import { GenerateArtifactApiData } from './generateArtifactApiData';
import { IAReport, InterfacesService } from './interfaces.service';
import { InheritedInterface, InterfaceOperationApiData, InterfacesApiData } from './interfacesApiData';
import { InterfaceParameter } from '../../../model/parameters';
import { ModalDirective } from 'ngx-bootstrap';
import { NgForm } from '@angular/forms';
import { GenerateData } from '../../../wineryComponentExists/wineryComponentExists.component';
import { ToscaTypes } from '../../../model/enums';
import { Utils } from '../../../wineryUtils/utils';
import { SelectableListComponent } from './selectableList/selectableList.component';
import { WineryVersion } from '../../../model/wineryVersion';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Interfaces } from './interfaces';
import { ArtifactTypeSelectData } from '../../../model/selectData';
import { WineryArtifactService } from '../wineryArtifacts/artifact.service';

@Component({
    selector: 'winery-instance-interfaces',
    templateUrl: 'interfaces.component.html',
    styleUrls: [
        'interfaces.component.css'
    ],
    providers: [
        InterfacesService
    ],
})
export class InterfacesComponent implements OnInit {
    _loading = {

        getPropertiesDefinitions: false,
        getInheritedPropertiesDefinitions: false,
        getMergedPropertiesDefinitions: false,
    };
    generating = false;
    isServiceTemplate = false;
    interfacesData: InterfacesApiData[];
    inheritedInterfacesData: InheritedInterface[];
    operations: InterfaceOperationApiData[] = null;
    inputParameters: InterfaceParameter[] = null;
    outputParameters: InterfaceParameter[] = null;
    selectedInterface: InterfacesApiData = null;
    selectedOperation: InterfaceOperationApiData = null;
    modalTitle: string;
    elementToRemove: string;
    validatorObject: WineryValidatorObject;
    @ViewChild('addIntOpModal') addIntOpModal: ModalDirective;
    @ViewChild('removeElementModal') removeElementModal: ModalDirective;
    @ViewChild('addElementForm') addElementForm: NgForm;
    @ViewChild('generateImplModal') generateImplModal: ModalDirective;
    @ViewChild('itemList') interfaceComponent: SelectableListComponent;
    generateArtifactApiData = new GenerateArtifactApiData();
    toscaType: ToscaTypes;
    createImplementation = true;
    createArtifactTemplate = true;
    implementationName: string = null;
    implementationNamespace: string = null;
    implementation: GenerateData = new GenerateData();
    artifactTemplate: GenerateData = new GenerateData();
    artifactTypes: ArtifactTypeSelectData[];
    modalOpen: boolean;

    constructor(private service: InterfacesService, private notify: WineryNotificationService,
                public sharedData: InstanceService, private existService: ExistService,
                private artifactService: WineryArtifactService, private route: Router) {
    }

    ngOnInit() {
        this.service.getInterfaces()
            .subscribe(
                data => this.handleInterfacesApiData(data),
                error => this.handleError(error)
            );
        this.service.getInheritedInterfaces()
            .subscribe(
                data => this.handleInheritedInterfaceData(data),
                error => this.handleError(error)
            );
        this.artifactService.getAllArtifactTypes().subscribe(
            res => this.handleArtifactTypeData(res),
            error => this.handleError(error)
        );
        this.toscaType = this.sharedData.toscaComponent.toscaType;
        this.isServiceTemplate = this.toscaType === ToscaTypes.ServiceTemplate;
    }

    // region ########### Template Callbacks ##########
    // region ########### Interfaces ##########
    addInterface() {
        this.modalTitle = 'Interface';
        this.validatorObject = new WineryValidatorObject(this.interfacesData, 'name');
        this.addElementForm.reset();
        this.addIntOpModal.show();
    }

    onAddInterface(name: string) {
        const tmp = new InterfacesApiData(name);
        this.interfacesData.push(tmp);
        this.onInterfaceSelect(tmp);
        name = null;
    }

    onInterfaceSelect(selectedInterface: InterfacesApiData) {
        if (selectedInterface !== this.selectedInterface) {
            this.outputParameters = null;
            this.inputParameters = null;
        }
        this.selectedInterface = selectedInterface;
        this.operations = selectedInterface.operations;
        this.selectedOperation = null;
    }

    removeInterface() {
        this.modalTitle = 'Remove Interface';
        this.elementToRemove = this.selectedInterface.name;
        this.removeElementModal.show();
    }

    onRemoveInterface() {
        this.interfacesData.splice(this.interfacesData.indexOf(this.selectedInterface), 1);
        this.inputParameters = null;
        this.outputParameters = null;
        this.operations = null;
        this.selectedOperation = null;
        this.selectedInterface = null;
    }

    // endregion

    // region ########## Operations ##########
    addOperation() {
        this.modalTitle = 'Operation';
        this.validatorObject = new WineryValidatorObject(this.operations, 'name');
        this.addElementForm.reset();
        this.addIntOpModal.show();
    }

    onAddOperation(name: string) {
        if (this.selectedInterface) {
            const tmp = new InterfaceOperationApiData(name);

            // if we are working on a target interface in servicetemplates, delete unnecessary attributes to
            // ensure data consistency with the backend.
            if (this.isServiceTemplate) {
                delete tmp.outputParameters;
                delete tmp.inputParameters;
                delete tmp.any;
                delete tmp.documentation;
                delete tmp.otherAttributes;
            }

            this.selectedInterface.operations.push(tmp);
            this.onOperationSelected(tmp);
        }
    }

    onOperationSelected(selectedOperation: InterfaceOperationApiData) {
        this.selectedOperation = selectedOperation;

        if (!this.isServiceTemplate) {
            if (!selectedOperation.inputParameters) {
                selectedOperation.inputParameters = [];
            }
            if (!selectedOperation.outputParameters) {
                selectedOperation.outputParameters = [];
            }

            this.inputParameters = selectedOperation.inputParameters;
            this.outputParameters = selectedOperation.outputParameters;
        }
    }

    removeOperation() {
        this.modalTitle = 'Remove Operation';
        this.elementToRemove = this.selectedOperation.name;
        this.removeElementModal.show();
    }

    onRemoveOperation() {
        this.operations.splice(this.operations.indexOf(this.selectedOperation), 1);
        this.inputParameters = null;
        this.outputParameters = null;
        this.selectedOperation = null;
    }

    // endregion

    // region ########## Generate Implementation ##########
    showGenerateImplementationModal(): void {
        this.artifactTemplate.name = this.sharedData.toscaComponent.localNameWithoutVersion
            + '-' + this.sharedData.currentVersion.toString()
            + '-' + this.getSelectedInterfaceOrOperationName()
            + '-IA';
        this.artifactTemplate.selectedInterface = this.selectedInterface;
        if (this.selectedOperation) {
            this.artifactTemplate.selectedOperation = this.selectedOperation;
        }
        let artifactTemplateNamespace = this.sharedData.toscaComponent.namespace;
        if (this.sharedData.toscaComponent.namespace.includes('nodetypes')) {
            artifactTemplateNamespace = this.sharedData.toscaComponent.namespace
                .replace('nodetypes', 'artifacttemplates');
        } else if (this.sharedData.toscaComponent.namespace.includes('relationshiptypes')) {
            artifactTemplateNamespace = this.sharedData.toscaComponent.namespace
                .replace('relationshiptypes', 'artifacttemplates');
        }

        this.artifactTemplate.namespace = artifactTemplateNamespace;
        this.artifactTemplate.toscaType = ToscaTypes.ArtifactTemplate;

        this.generateArtifactApiData = new GenerateArtifactApiData();

        this.generateArtifactApiData.autoCreateArtifactTemplate = 'yes';
        this.generateArtifactApiData.interfaceName = this.selectedInterface.name;
        this.generateArtifactApiData.autoGenerateIA = 'yes';

        this.implementation.name = this.sharedData.toscaComponent.localNameWithoutVersion
            + '-' + this.sharedData.currentVersion.toString()
            + '-Implementation';

        let implementationNamespace = this.sharedData.toscaComponent.namespace;
        if (this.sharedData.toscaComponent.namespace.includes('nodetypes')) {
            implementationNamespace = this.sharedData.toscaComponent.namespace
                .replace('nodetypes', 'nodetypeimplementations');
        } else if (this.sharedData.toscaComponent.namespace.includes('relationshiptypes')) {
            implementationNamespace = this.sharedData.toscaComponent.namespace
                .replace('relationshiptypes', 'relationshiptypeimplementations');
        }
        this.implementation.namespace = implementationNamespace;
        this.implementation.toscaType = Utils.getImplementationOrTemplateOfType(this.toscaType);

        this.generateImplModal.show();
    }

    generateImplementationArtifact(): void {
        this.generating = true;
        this.generateArtifactApiData.artifactTemplateName = this.artifactTemplate.name;
        this.generateArtifactApiData.artifactTemplateNamespace = this.artifactTemplate.namespace;
        this.generateArtifactApiData.artifactName = this.generateArtifactApiData.artifactTemplateName;
        if (this.artifactTemplate.selectedOperation.name === this.removeNSFromName(this.artifactTemplate.selectedInterface.name)) {
            // Implement whole interface
            this.generateArtifactApiData.operationName = 'interface';
        } else {
            this.generateArtifactApiData.operationName = this.artifactTemplate.selectedOperation.name;
        }
        // Fix this prevent weird renaming by backend
        this.generateArtifactApiData.artifactTemplate = null;
        // Save the current interfaces & operations first in order to prevent inconsistencies.
        this.save();
    }

    // endregion

    // region ########## Generate Lifecycle Interface ##########

    generateLifecycleInterface(): void {
        const lifecycle = new InterfacesApiData();
        if (this.toscaType === ToscaTypes.RelationshipType && this.route.url.endsWith('/interfaces')) {
            lifecycle.name = Interfaces.RELATIONSHIP_CONFIGURE;
            lifecycle.operations.push(new InterfaceOperationApiData(Interfaces.RELATIONSHIP_CONFIGURE_PRE_CONFIGURE_SOURCE));
            lifecycle.operations.push(new InterfaceOperationApiData(Interfaces.RELATIONSHIP_CONFIGURE_PRE_CONFIGURE_TARGET));
            lifecycle.operations.push(new InterfaceOperationApiData(Interfaces.RELATIONSHIP_CONFIGURE_POST_CONFIGURE_SOURCE));
            lifecycle.operations.push(new InterfaceOperationApiData(Interfaces.RELATIONSHIP_CONFIGURE_POST_CONFIGURE_TARGET));
        } else {
            // Node Types and Relationship Types (Source and Target Interface)
            lifecycle.name = Interfaces.LIFECYCLE_STANDARD;
            lifecycle.operations.push(new InterfaceOperationApiData(Interfaces.LIFECYCLE_STANDARD_INSTALL));
            lifecycle.operations.push(new InterfaceOperationApiData(Interfaces.LIFECYCLE_STANDARD_CONFIGURE));
            lifecycle.operations.push(new InterfaceOperationApiData(Interfaces.LIFECYCLE_STANDARD_START));
            lifecycle.operations.push(new InterfaceOperationApiData(Interfaces.LIFECYCLE_STANDARD_STOP));
            lifecycle.operations.push(new InterfaceOperationApiData(Interfaces.LIFECYCLE_STANDARD_UNINSTALL));
        }
        this.interfacesData.push(lifecycle);
        this.interfaceComponent.selectItem(lifecycle);
    }

    containsDefaultLifecycle(): boolean {
        if (this.sharedData.currentVersion.editable) {
            if (this.interfacesData === null || this.interfacesData === undefined) {
                return false;
            }
            const lifecycleId = this.interfacesData.findIndex((value) => {
                return value.name.startsWith(Interfaces.LIFECYCLE_STANDARD)
                    || value.name.startsWith(Interfaces.RELATIONSHIP_CONFIGURE);
            });
            return lifecycleId !== -1;
        }
        return true;
    }

    // endregion

    // region ######### Checker ##########

    checkImplementationExists(): void {
        if (!this.implementationNamespace.endsWith('/')) {
            this.existService.check(backendBaseURL + '/'
                + Utils.getTypeOfTemplateOrImplementation(this.toscaType)
                + encodeURIComponent(encodeURIComponent(this.implementationNamespace)) + '/'
                + this.implementationName + '/'
            ).subscribe(
                () => this.createImplementation = false,
                () => this.createImplementation = true
            );
        }
    }

    checkArtifactTemplateExists(): void {
        if (!this.generateArtifactApiData.artifactTemplateNamespace.endsWith('/')) {
            this.existService.check(backendBaseURL + '/artifacttemplates/'
                + encodeURIComponent(encodeURIComponent(this.generateArtifactApiData.artifactTemplateNamespace)) + '/'
                + this.generateArtifactApiData.artifactTemplateName + '/'
            ).subscribe(
                () => this.createArtifactTemplate = false,
                () => this.createArtifactTemplate = true
            );
        }
    }

    // endregion

    onRemoveElement() {
        switch (this.modalTitle) {
            case 'Remove Operation':
                this.onRemoveOperation();
                break;
            case 'Remove Interface':
                this.onRemoveInterface();
                break;
            default:
                this.notify.error('Couldn\'t remove element!');
        }
    }

    save() {
        this.service.save(this.interfacesData)
            .subscribe(
                () => this.handleSave(),
                error => this.handleError(error)
            );
    }

    isLoading = () => Utils.isLoading(this._loading);

    toggleDiv(parentInterface: InheritedInterface) {
        parentInterface.is_shown = !parentInterface.is_shown;
    }

    processUrl(parentType: string) {
        const process = parentType.replace('{', '').split('}');
        process[0] = Utils.nodeTypeURL(parentType);
        return process;
    }

    overrideInterface(inh: InterfacesApiData) {
        const filteredInterface: InterfacesApiData = this.interfacesData.find((value) => value.name === inh.name);
        if (!filteredInterface) {
            const clone = JSON.parse(JSON.stringify(inh));
            this.interfacesData.push(clone);
        }
    }

    interfaceDoesNotExist(inh: InterfacesApiData): boolean {
        const filteredInterface: InterfacesApiData = this.interfacesData.find((value) => value.name === inh.name);
        return !filteredInterface;
    }

    overrideOperation(inh: InterfacesApiData, op: InterfaceOperationApiData) {
        const filteredInterface: InterfacesApiData = this.interfacesData.find((value) => value.name === inh.name);
        if (!filteredInterface) {
            const clone = JSON.parse(JSON.stringify(inh));
            const operationClone: InterfaceOperationApiData = JSON.parse(JSON.stringify(op));
            clone.operations = [operationClone];
            this.interfacesData.push(clone);
        } else {
            const clone = JSON.parse(JSON.stringify(op));
            filteredInterface.operations.push(clone);
        }
    }

    operationDoesNotExists(inh: InterfacesApiData, op: InterfaceOperationApiData): boolean {
        if (this.interfaceDoesNotExist(inh)) {
            return true;
        } else {
            const filteredInterface: InterfacesApiData = this.interfacesData.find((value) => value.name === inh.name);
            const filteredOperation: InterfaceOperationApiData = filteredInterface.operations.find((oper) => op.name === oper.name);
            return !filteredOperation;
        }
    }

    handleCancel() {
        this.modalOpen = false;
    }

    // endregion

    // region ########## Private Methods ##########

    private handleInterfacesApiData(data: InterfacesApiData[]) {
        this.interfacesData = data ? data : [];

    }

    private handleInheritedInterfaceData(data: InheritedInterface[]) {

        this.inheritedInterfacesData = data ? data : [];

    }

    private handleSave() {
        this.notify.success('Changes saved!');

        // If there is a generation of implementations in progress, generate those now.
        if (this.generating) {
            this.implementation.name += WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + this.implementation.version.toString();
            if (this.implementation.createComponent) {
                this.service.createImplementation(this.implementation.name, this.implementation.namespace)
                    .subscribe(
                        data => this.handleGeneratedImplementation(data),
                        error => this.handleError(error)
                    );
            } else if (!this.implementation.createComponent && this.artifactTemplate.createComponent) {
                this.handleGeneratedImplementation();
            } else {
                this.generating = false;
            }
        }
    }

    private handleError(error: HttpErrorResponse) {
        this.generating = false;
        this.notify.error(error.error);
    }

    private handleGeneratedImplementation(data?: any) {
        if (this.artifactTemplate.createComponent) {
            this.generateArtifactApiData.artifactTemplateName = this.generateArtifactApiData.artifactName = this.artifactTemplate.name;
            this.generateArtifactApiData.artifactTemplateNamespace = this.artifactTemplate.namespace;
            this.generateArtifactApiData.artifactType = this.artifactTemplate.artifactTypeQName;

            this.service.createArtifactTemplate(this.implementation.name, this.implementation.namespace, this.generateArtifactApiData)
                .subscribe(
                    (response) => this.handleGeneratedArtifact(response),
                    error => this.handleError(error)
                );
        } else {
            this.generating = false;
            this.generateImplModal.hide();
        }
        if (data) {
            this.notify.success('Successfully created Implementation!');
        }
    }

    private handleGeneratedArtifact(response: HttpResponse<IAReport>) {
        const report: IAReport = response.body;
        let message = '';
        if (this.toscaType === ToscaTypes.NodeType) {
            // backend return a path to the created artifacttemplate in the form: http://localhost:8080/winery/
            // therefore truncate backendBaseUrl to receive path
            const artifacttemplateUrl = response.url.replace(backendBaseURL, '');
            message = 'It\'s available for download at ' +
                '<a style="color: black;" href=' + artifacttemplateUrl + '"/#/">' + ' source</a>.';
        }
        this.generating = false;
        this.generateImplModal.hide();
        if (report.warning !== undefined && report.warning.length > 0) {
            this.notify.warning(report.warning);
        } else {
            this.notify.success(message, 'Successfully created Artifact!', { enableHTML: true });
        }
    }

    // endregion

    private handleArtifactTypeData(res: any) {
        this.artifactTypes = res;
    }

    private getSelectedInterfaceOrOperationName() {
        // If there is only one operation, or none is selected, we just implement the whole interface. As such, we name the ArtifactTemplate accordingly.
        if (this.selectedInterface.operations.length === 1 || !this.selectedOperation) {
            return this.removeNSFromName(this.selectedInterface.name);
        }
        return this.removeNSFromName(this.selectedOperation.name);
    }

    private removeNSFromName(name: string) {
        // Assumption: the actual interface name does not contain any '/'
        // Also works if there is no NS prefixing the name
        return name.substr(name.lastIndexOf('/') + 1);
    }
}
