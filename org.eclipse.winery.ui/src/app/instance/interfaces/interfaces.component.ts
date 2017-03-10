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
import { InterfacesApiData, InterfaceOperationApiData, InterfaceParameter } from './interfacesApiData';
import { isNullOrUndefined } from 'util';
import { YesNoEnum } from "../../interfaces/enums";

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
    modalTitle: string;
    modalParamTitle: string;
    columns: Array<any> = [
        {title: 'Name', name: 'name', sort: true},
        {title: 'Type', name: 'type', sort: true},
        {title: 'Required', name: 'required', sort: false}
    ];

    @ViewChild('addIntOpModal') addInterfaceOrPropertyModal: any;
    @ViewChild('addIntParametersModal') addParametersModal: any;

    constructor(private service: InterfacesService,
                private sharedData: InstanceService) {
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
        this.addInterfaceOrPropertyModal.show();
    }

    addOperation() {
        console.log('addOperation');
        this.modalTitle = 'Operation';
        this.addInterfaceOrPropertyModal.show();
    }

    onAddInterface(name: string) {
        this.interfacesData.push(new InterfacesApiData(name));
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
        this.inputParameters = selectedOperation.inputParameters.inputParameter;
        this.outputParameters = selectedOperation.outputParameters.outputParameter;
    }

    onAddInputParam(name: string, type: string, required: boolean) {
        this.selectedOperation.inputParameters.inputParameter.push(new InterfaceParameter(name, type, required ? YesNoEnum.YES : YesNoEnum.NO));
    }

    onAddOutputParam(name: string, type: string, required: boolean) {
        this.selectedOperation.outputParameters.outputParameter.push(new InterfaceParameter(name, type, required ? YesNoEnum.YES : YesNoEnum.NO));
    }

    addInputParam() {
        this.modalParamTitle = 'Input Parameter';
        this.addParametersModal.show();
    }

    addOutputParam() {
        this.modalParamTitle = 'Output Parameter';
        this.addParametersModal.show();
    }

    save() {
        this.service.save(this.interfacesData);
    }

    // endregion

    // region ########## Private Methods ##########
    private handleInterfacesApiData(data: InterfacesApiData[]) {
        this.interfacesData = data;
        this.loading = false;
    }

    private handleError(error: any) {

    }

    // endregion
}
