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
 *     Lukas Harzenetter - initial API and implementation
 */

import { Component, OnInit, ViewChild } from '@angular/core';
import { InterfacesService } from './interfaces.service';
import { InstanceService } from '../instance.service';
import {
    InterfacesApiData, InterfaceOperationApiData, InterfaceParameter, InputParameters,
    OutputParameters
} from './interfacesApiData';
import { isNullOrUndefined } from 'util';
import { YesNoEnum } from "../../interfaces/enums";
import { handleError } from 'typings/dist/support/cli';
import { NotificationService } from '../../notificationModule/notificationservice';
import { ValidatorObject } from '../../validators/duplicateValidator.directive';

@Component({
    selector: 'winery-instance-interfaces',
    templateUrl: 'interfaces.component.html',
    providers: [
        InterfacesService
    ],
})
export class InterfacesComponent implements OnInit {

    loading: boolean = true;
    interfacesData: InterfacesApiData[];

    operations: any[] = null;
    inputParameters: Array<any> = null;
    outputParameters: Array<any> = null;
    selectedInterface: InterfacesApiData = null;
    selectedOperation: InterfaceOperationApiData = null;
    columns: Array<any> = [
        { title: 'Name', name: 'name', sort: true },
        { title: 'Type', name: 'type', sort: true },
        { title: 'Required', name: 'required', sort: false }
    ];

    modalTitle: string;
    modalParamTitle: string;
    validatorObject: ValidatorObject;
    @ViewChild('addIntOpModal') addInterfaceOrPropertyModal: any;
    @ViewChild('addIntParametersModal') addParametersModal: any;

    constructor(private service: InterfacesService,
                private sharedData: InstanceService,
                private notify: NotificationService) {
    }

    ngOnInit() {
        this.service.setPath(this.sharedData.path);
        this.service.getInterfaces()
            .subscribe(
                data => this.handleInterfacesApiData(data),
                error => this.handleError(error)
            );
    }

    // region ########### Template Callbacks ##########

    addInterface() {
        this.modalTitle = 'Interface';
        this.validatorObject = new ValidatorObject(this.interfacesData, 'name');
        this.addInterfaceOrPropertyModal.show();
    }

    addOperation() {
        this.modalTitle = 'Operation';
        this.validatorObject = new ValidatorObject(this.operations, 'name');
        this.addInterfaceOrPropertyModal.show();
    }

    addInputParam() {
        this.modalParamTitle = 'Input Parameter';
        this.validatorObject = new ValidatorObject(this.inputParameters, 'name');
        this.addParametersModal.show();
    }

    addOutputParam() {
        this.modalParamTitle = 'Output Parameter';
        this.validatorObject = new ValidatorObject(this.outputParameters, 'name');
        this.addParametersModal.show();
    }

    onAddInterface(name: string) {
        this.interfacesData.push(new InterfacesApiData(name));
        name = null;
    }

    onAddOperation(name: string) {
        if (!isNullOrUndefined(this.selectedInterface)) {
            this.selectedInterface.operation.push(new InterfaceOperationApiData(name));
        }
    }

    onInterfaceSelect(selectedInterface: any) {
        if (selectedInterface !== this.selectedInterface) {
            this.outputParameters = null;
            this.inputParameters = null;
        }
        this.selectedInterface = selectedInterface;
        this.operations = selectedInterface.operation;
    }

    onOperationSelected(selectedOperation: any) {
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

    onAddInputParam(name: string, type: string, required: boolean) {
        this.selectedOperation.inputParameters.inputParameter.push(new InterfaceParameter(name, type, required ? YesNoEnum.YES : YesNoEnum.NO));
    }

    onAddOutputParam(name: string, type: string, required: boolean) {
        this.selectedOperation.outputParameters.outputParameter.push(new InterfaceParameter(name, type, required ? YesNoEnum.YES : YesNoEnum.NO));
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

    // endregion
}
