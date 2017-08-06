/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier, Lukas Harzenetter - initial API and implementation
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { backendBaseURL } from '../../../configuration';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { ExistService } from '../../../wineryUtils/existService';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { InstanceService } from '../../instance.service';
import { GenerateArtifactApiData } from './generateArtifactApiData';
import { InterfacesService } from './interfaces.service';
import { InterfaceOperationApiData, InterfacesApiData } from './interfacesApiData';
import { InputParameters, InterfaceParameter, OutputParameters } from '../../../wineryInterfaces/parameters';
import { ModalDirective } from 'ngx-bootstrap';
import { NgForm } from '@angular/forms';
import { GenerateData } from '../../../wineryComponentExists/wineryComponentExists.component';

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

    loading = false;
    generating = false;
    isServiceTemplate = false;
    interfacesData: InterfacesApiData[];

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
    generateArtifactApiData = new GenerateArtifactApiData();
    selectedResource: string;
    createImplementation = true;
    createArtifactTemplate = true;
    implementationName: string = null;
    implementationNamespace: string = null;
    implementation: GenerateData = new GenerateData();
    artifactTemplate: GenerateData = new GenerateData();

    constructor(private service: InterfacesService, private notify: WineryNotificationService,
                private sharedData: InstanceService, private existService: ExistService) {
    }

    ngOnInit() {
        this.service.getInterfaces()
            .subscribe(
                data => this.handleInterfacesApiData(data),
                error => this.handleError(error)
            );
        this.selectedResource = this.sharedData.selectedResource.charAt(0).toUpperCase() + this.sharedData.selectedResource.slice(1);
        this.isServiceTemplate = this.selectedResource === 'ServiceTemplate';
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
        this.operations = selectedInterface.operation;
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
        if (!isNullOrUndefined(this.selectedInterface)) {
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

            this.selectedInterface.operation.push(tmp);
            this.onOperationSelected(tmp);
        }
    }

    onOperationSelected(selectedOperation: InterfaceOperationApiData) {
        this.selectedOperation = selectedOperation;

        if (!this.isServiceTemplate) {
            if (isNullOrUndefined(selectedOperation.inputParameters)) {
                selectedOperation.inputParameters = new InputParameters();
            }
            if (isNullOrUndefined(selectedOperation.outputParameters)) {
                selectedOperation.outputParameters = new OutputParameters();
            }

            this.inputParameters = selectedOperation.inputParameters.inputParameter;
            this.outputParameters = selectedOperation.outputParameters.outputParameter;
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
        this.artifactTemplate.name =
            this.sharedData.selectedComponentId + '_' + this.selectedInterface.name.replace(/\W/g, '_') + '_IA';
        this.artifactTemplate.namespace = this.sharedData.selectedNamespace;
        this.artifactTemplate.selectedResource = 'Artifact';
        this.artifactTemplate.selectedResourceType = 'Template';

        this.generateArtifactApiData = new GenerateArtifactApiData();
        this.generateArtifactApiData.javaPackage = this.getPackageNameFromNamespace();

        this.generateArtifactApiData.autoCreateArtifactTemplate = 'yes';
        this.generateArtifactApiData.interfaceName = this.selectedInterface.name;

        this.implementation.name = this.sharedData.selectedComponentId + '_impl';
        this.implementation.namespace = this.sharedData.selectedNamespace;
        this.implementation.selectedResource = this.sharedData.selectedResource;
        this.implementation.selectedResourceType = 'Implementation';

        this.generateImplModal.show();
    }

    generateImplementationArtifact(): void {
        this.generating = true;
        this.generateArtifactApiData.artifactName = this.generateArtifactApiData.artifactTemplateName;
        if (this.implementation.createComponent) {
            this.service.createImplementation(this.implementation.name, this.implementation.namespace)
                .subscribe(
                    data => this.handleGeneratedImplementation(data),
                    error => this.handleError(error)
                );
        } else if (!this.implementation.createComponent && this.artifactTemplate.createComponent) {
            this.handleGeneratedImplementation();
        }
    }

    // endregion

    // region ########## Generate Lifecycle Interface ##########
    generateLifecycleInterface(): void {
        const lifecycle = new InterfacesApiData('http://www.example.com/interfaces/lifecycle');
        lifecycle.operation.push(new InterfaceOperationApiData('install'));
        lifecycle.operation.push(new InterfaceOperationApiData('configure'));
        lifecycle.operation.push(new InterfaceOperationApiData('start'));
        lifecycle.operation.push(new InterfaceOperationApiData('stop'));
        lifecycle.operation.push(new InterfaceOperationApiData('uninstall'));
        this.interfacesData.push(lifecycle);
    }

    containsDefaultLifecycle(): boolean {
        if (isNullOrUndefined(this.interfacesData)) {
            return false;
        }

        const lifecycleId = this.interfacesData.findIndex((value) => {
            return value.name.endsWith('http://www.example.com/interfaces/lifecycle');
        });

        return lifecycleId !== -1;
    }

    // endregion

    // region ######### Checker ##########
    checkImplementationExists(): void {
        if (!this.implementationNamespace.endsWith('/')) {
            this.existService.check(backendBaseURL + '/'
                + this.selectedResource.replace(' ', '').toLowerCase() + 'implementations/'
                + encodeURIComponent(encodeURIComponent(this.implementationNamespace)) + '/'
                + this.implementationName + '/'
            ).subscribe(
                data => this.createImplementation = false,
                error => this.createImplementation = true
            );
        }
    }

    checkArtifactTemplateExists(): void {
        if (!this.generateArtifactApiData.artifactTemplateNamespace.endsWith('/')) {
            this.existService.check(backendBaseURL + '/artifacttemplates/'
                + encodeURIComponent(encodeURIComponent(this.generateArtifactApiData.artifactTemplateNamespace)) + '/'
                + this.generateArtifactApiData.artifactTemplateName + '/'
            ).subscribe(
                data => this.createArtifactTemplate = false,
                error => this.createArtifactTemplate = true
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
        this.loading = true;
        this.service.save(this.interfacesData)
            .subscribe(
                data => this.handleSave(),
                error => this.handleError(error)
            );
    }

    // endregion

    // region ########## Private Methods ##########
    private handleInterfacesApiData(data: InterfacesApiData[]) {
        this.interfacesData = data;
        this.loading = false;
    }

    private handleSave() {
        this.loading = false;
        this.notify.success('Changes saved!');
    }

    private handleError(error: any) {
        this.loading = false;
        this.generating = false;
        this.notify.error(error.toString());
    }

    private getPackageNameFromNamespace(): string {
        // to only get the relevant information, without the 'http://'
        const namespaceArray = this.sharedData.selectedNamespace.split('/').slice(2);
        const domainArray = namespaceArray[0].split('.');

        let javaPackage = '';
        for (let i = domainArray.length - 1; i >= 0; i--) {
            if (javaPackage.length > 0) {
                javaPackage += '.';
            }
            javaPackage += domainArray[i];
        }
        for (let i = 1; i < namespaceArray.length; i++) {
            javaPackage += '.' + namespaceArray[i];
        }

        return javaPackage;
    }

    private handleGeneratedImplementation(data?: any) {
        if (this.artifactTemplate.createComponent) {
            this.generateArtifactApiData.artifactTemplateName = this.generateArtifactApiData.artifactName =  this.artifactTemplate.name;
            this.generateArtifactApiData.artifactTemplateNamespace = this.artifactTemplate.namespace;
            this.service.createArtifactTemplate(this.implementation.name, this.implementation.namespace, this.generateArtifactApiData)
                .subscribe(
                    () => this.handleGeneratedArtifact(),
                    error => this.handleError(error)
                );
        } else {
            this.generating = false;
            this.generateImplModal.hide();
        }
        if (!isNullOrUndefined(data)) {
            this.notify.success('Successfully created Implementation!');
        }
    }

    private handleGeneratedArtifact() {
        this.generating = false;
        this.generateImplModal.hide();
        this.notify.success('Successfully created Artifact!');
    }

    // endregion
}
