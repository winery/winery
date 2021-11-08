/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import { Component, OnInit } from '@angular/core';
import { InputOutputParametersService } from './inputOutputParameters.service';
import { Parameter } from '../../../model/parameters';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';

export enum ParameterMode {
    INPUT,
    OUTPUT,
}

@Component({
    templateUrl: 'inputOutputParameters.component.html',
    providers: [InputOutputParametersService]
})
export class InputOutputParametersComponent implements OnInit {

    mode: ParameterMode;
    modeType = ParameterMode;

    inputParameters: Parameter[] = [];
    outputParameters: Parameter[] = [];

    columnsInputParameters: Array<WineryTableColumn> = [
        { title: 'Name', name: 'key', sort: true },
        { title: 'Type', name: 'type', sort: false },
        { title: 'Required', name: 'required', sort: false },
        { title: 'Default Value', name: 'defaultValue', sort: false },
        { title: 'Description', name: 'description', sort: false },
    ];
    columnsOutputParameters: Array<WineryTableColumn> = [
        { title: 'Name', name: 'key', sort: true },
        { title: 'Type', name: 'type', sort: false },
        { title: 'Required', name: 'required', sort: false },
        { title: 'Value', name: 'value', sort: false },
        { title: 'Description', name: 'description', sort: false },
    ];

    loading = false;

    constructor(private parametersService: InputOutputParametersService) {
    }

    ngOnInit() {
        this.parametersService.getInputParameters()
            .subscribe(
                data => data && data.forEach(item => this.inputParameters.push(Object.assign(new Parameter(), item))),
                error => console.error(error)
            );
        this.parametersService.getOutputParameters()
            .subscribe(
                data => data && data.forEach(item => this.outputParameters.push(Object.assign(new Parameter(), item))),
                error => console.log(error)
            );
    }

    save(mode: ParameterMode) {
        this.loading = true;
        if (mode === ParameterMode.INPUT) {
            this.parametersService.updateInputParameters(this.inputParameters)
                .subscribe(
                    () => this.loading = false,
                    error => console.log(error)
                );
        }
        if (mode === ParameterMode.OUTPUT) {
            this.parametersService.updateOutputParameters(this.outputParameters)
                .subscribe(
                    () => this.loading = false,
                    error => console.log(error)
                );
        }
    }
}
