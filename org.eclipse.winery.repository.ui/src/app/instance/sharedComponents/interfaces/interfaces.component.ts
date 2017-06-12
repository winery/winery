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
import { InputParameters, OutputParameters } from '../../../wineryInterfaces/parameters';

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

    loading = true;
    generating = false;
    interfacesData: InterfacesApiData[];

    operations: any[] = null;
    inputParameters: Array<any> = null;
    outputParameters: Array<any> = null;
    selectedInterface: InterfacesApiData = null;
    selectedOperation: InterfaceOperationApiData = null;

    modalTitle: string;
    elementToRemove: string;
    validatorObject: WineryValidatorObject;
    @ViewChild('addIntOpModal') addInterfaceOrPropertyModal: any;
    @ViewChild('removeElementModal') removeElementModal: any;
    @ViewChild('addElementForm') addElementForm: any;

    @ViewChild('generateImplModal') generateImplModal: any;
    generateArtifactApiData = new GenerateArtifactApiData();
    selectedResource: string;
    createImplementation = true;
    createArtifactTemplate = true;
    implementationName: string = null;
    implementationNamespace: string = null;

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
    }

    // region ########### Template Callbacks ##########
    // region ########### Interfaces ##########
    addInterface() {
        this.modalTitle = 'Interface';
        this.validatorObject = new WineryValidatorObject(this.interfacesData, 'name');
        this.addElementForm.reset();
        this.addInterfaceOrPropertyModal.show();
    }

    onAddInterface(name: string) {
        this.interfacesData.push(new InterfacesApiData(name));
        name = null;
    }

    onInterfaceSelect(selectedInterface: InterfacesApiData) {
        if (selectedInterface !== this.selectedInterface) {
            this.outputParameters = null;
            this.inputParameters = null;
        }
        this.selectedInterface = selectedInterface;
        this.operations = selectedInterface.operation;
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
        this.addInterfaceOrPropertyModal.show();
    }

    onAddOperation(name: string) {
        if (!isNullOrUndefined(this.selectedInterface)) {
            this.selectedInterface.operation.push(new InterfaceOperationApiData(name));
        }
    }

    onOperationSelected(selectedOperation: InterfaceOperationApiData) {
        this.selectedOperation = selectedOperation;

        if (isNullOrUndefined(selectedOperation.inputParameters)) {
            selectedOperation.inputParameters = new InputParameters();
        }
        if (isNullOrUndefined(selectedOperation.outputParameters)) {
            selectedOperation.outputParameters = new OutputParameters();
        }

        this.inputParameters = selectedOperation.inputParameters.inputParameter;
        this.outputParameters = selectedOperation.outputParameters.outputParameter;
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
        this.generateArtifactApiData = new GenerateArtifactApiData();
        this.generateArtifactApiData.javaPackage = this.getPackageNameFromNamespace();
        this.generateArtifactApiData.artifactTemplateName =
            this.sharedData.selectedComponentId + '_' + this.selectedInterface.name.replace(/\W/g, '_') + '_IA';
        this.generateArtifactApiData.artifactTemplateNamespace = this.sharedData.selectedNamespace;
        this.generateArtifactApiData.autoCreateArtifactTemplate = 'yes';
        this.generateArtifactApiData.interfaceName = this.selectedInterface.name;

        this.implementationName = this.sharedData.selectedComponentId + '_impl';
        this.implementationNamespace = this.sharedData.selectedNamespace;

        this.checkImplementationExists();
        this.checkArtifactTemplateExists();

        this.generateImplModal.show();
    }

    generateImplementationArtifact(): void {
        this.generating = true;
        this.generateArtifactApiData.artifactName = this.generateArtifactApiData.artifactTemplateName;
        if (this.createImplementation) {
            this.service.createImplementation(this.selectedResource.replace(' ', '').toLowerCase(),
                this.implementationName, this.implementationNamespace)
                .subscribe(
                    data => this.handleGeneratedImplementation(data),
                    error => this.handleError(error)
                );
        } else if (!this.createImplementation && this.createArtifactTemplate) {
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
        lifecycle.operation.push(new InterfaceOperationApiData('unistall'));
        this.interfacesData.push(lifecycle);
    }

    containsDefaultLifecycle(): boolean {
        if (isNullOrUndefined(this.interfacesData)) {
            return false;
        }

        const lifecycleId = this.interfacesData.findIndex((value, index, obj) => {
            return value.name.endsWith('http://www.example.com/interfaces/lifecycle');
        });

        return lifecycleId !== -1;
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

    private checkImplementationExists(): void {
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

    private checkArtifactTemplateExists(): void {
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

    private handleGeneratedImplementation(data?: any) {
        if (this.createArtifactTemplate) {
            this.service.createImplementationArtifact(this.selectedResource.replace(' ', '').toLowerCase(), this.implementationName,
                this.implementationNamespace, this.generateArtifactApiData)
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
