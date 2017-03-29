///<reference path="interfacesApiData.ts"/>
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
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { InterfacesService } from './interfaces.service';
import {
    InputParameters,
    InterfaceOperationApiData,
    InterfaceParameter,
    InterfacesApiData,
    OutputParameters
} from './interfacesApiData';
import { YesNoEnum } from '../../interfaces/enums';
import { NotificationService } from '../../notificationModule/notification.service';
import { ValidatorObject } from '../../validators/duplicateValidator.directive';
import { WineryTableColumn } from '../../wineryTableModule/wineryTable.component';
import { InstanceService } from '../instance.service';
import { ExistService } from '../../util/existService';
import { backendBaseUri } from '../../configuration';

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
    interfacesData: InterfacesApiData[];

    operations: any[] = null;
    inputParameters: Array<any> = null;
    outputParameters: Array<any> = null;
    selectedInterface: InterfacesApiData = null;
    selectedOperation: InterfaceOperationApiData = null;
    selectedInputParameter: InterfaceParameter;
    selectedOutputParameter: InterfaceParameter;
    columns: Array<WineryTableColumn> = [
        {title: 'Name', name: 'name', sort: true},
        {title: 'Type', name: 'type', sort: true},
        {title: 'Required', name: 'required', sort: false}
    ];

    modalTitle: string;
    elementToRemove: string;
    validatorObject: ValidatorObject;
    @ViewChild('addIntOpModal') addInterfaceOrPropertyModal: any;
    @ViewChild('addIntParametersModal') addParametersModal: any;
    @ViewChild('removeElementModal') removeElementModal: any;
    @ViewChild('addElementForm') addElementForm: any;
    @ViewChild('parameterForm') parameterForm: any;

    @ViewChild('generateImplModal') generateImplModal: any;
    javaPackageName: string = null;
    selectedResource: string;
    resourceType: string;
    implementationName: string = null;
    implementationNamespace: string = null;
    artifactName: string = null;
    artifactNamespace: string = null;
    createImplementation = true;
    createArtifactTemplate = true;

    constructor(private service: InterfacesService,
                private sharedData: InstanceService, private existService: ExistService,
                private notify: NotificationService, private changeRef: ChangeDetectorRef) {
    }

    ngOnInit() {
        this.service.getInterfaces()
            .subscribe(
                data => this.handleInterfacesApiData(data),
                error => this.handleError(error)
            );
        this.selectedResource = this.sharedData.selectedResource.charAt(0).toUpperCase() + this.sharedData.selectedResource.slice(1);
        this.resourceType = this.sharedData.selectedResource.replace(' ', '').toLowerCase();
    }

    // region ########### Template Callbacks ##########
    // region ########### Interfaces ##########
    addInterface() {
        this.modalTitle = 'Interface';
        this.validatorObject = new ValidatorObject(this.interfacesData, 'name');
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
        this.validatorObject = new ValidatorObject(this.operations, 'name');
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

    // region ########## Input Parameters ##########
    addInputParam() {
        this.modalTitle = 'Input Parameter';
        this.validatorObject = new ValidatorObject(this.inputParameters, 'name');
        this.parameterForm.reset();
        this.addParametersModal.show();
    }

    onAddInputParam(name: string, type: string, required: boolean) {
        this.inputParameters.push(new InterfaceParameter(name, type, required ? YesNoEnum.YES : YesNoEnum.NO));
    }

    onInputParameterSelected(selectedInput: InterfaceParameter) {
        this.selectedInputParameter = selectedInput;
    }

    removeInputParameter() {
        this.modalTitle = 'Remove Input Parameter';
        this.elementToRemove = this.selectedInputParameter.name;
        this.removeElementModal.show();
    }

    onRemoveInputParameter() {
        this.inputParameters.splice(this.inputParameters.indexOf(this.selectedInputParameter));
    }

    // endregion

    // region ########## Output Parameters ##########
    addOutputParam() {
        this.modalTitle = 'Output Parameter';
        this.validatorObject = new ValidatorObject(this.outputParameters, 'name');
        this.parameterForm.reset();
        this.addParametersModal.show();
    }

    onAddOutputParam(name: string, type: string, required: boolean) {
        this.outputParameters.push(new InterfaceParameter(name, type, required ? YesNoEnum.YES : YesNoEnum.NO));
    }

    onOutputParameterSelected(selectedOutput: InterfaceParameter) {
        this.selectedOutputParameter = selectedOutput;
    }

    removeOutputParameter() {
        this.modalTitle = 'Remove Output Parameter';
        this.elementToRemove = this.selectedOutputParameter.name;
        this.removeElementModal.show();
    }

    onRemoveOutputParameter() {
        this.outputParameters.splice(this.outputParameters.indexOf(this.selectedOutputParameter));
    }

    // endregion

    // region ########## Generate Implementation ##########
    showGenerateImplementationModal(): void {
        this.javaPackageName = this.getPackageNameFromNamespace();
        this.implementationName = this.sharedData.selectedComponentId + '_impl';
        this.implementationNamespace = this.sharedData.selectedNamespace;
        this.artifactName = this.sharedData.selectedComponentId + '__IA';
        this.artifactNamespace = this.sharedData.selectedNamespace;

        this.checkImplementationExists();
        this.checkArtifactTemplateExists();

        this.generateImplModal.show();
    }

    generateImplementationArtifact(): void {
        console.log('generate');
        this.generateImplModal.hide();
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
            case 'Remove Input Parameter':
                this.onRemoveInputParameter();
                break;
            case 'Remove Output Parameter':
                this.onRemoveOutputParameter();
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
        console.log(this.createImplementation);
        this.existService.check(backendBaseUri + '/'
            + this.resourceType + 'implementations/'
            + encodeURIComponent(encodeURIComponent(this.implementationNamespace)) + '/'
            + this.implementationName + '/'
        ).subscribe(
            data => this.createImplementation = false,
            error => this.createArtifactTemplate = true
        );
    }

    private checkArtifactTemplateExists(): void {
        this.existService.check(backendBaseUri + '/artifacttemplates'
            + encodeURIComponent(encodeURIComponent(this.artifactNamespace))
            + this.artifactName + '/'
        ).subscribe(
            data => this.createArtifactTemplate = false,
            error => this.createArtifactTemplate = true
        );
    }

    // endregion
}
