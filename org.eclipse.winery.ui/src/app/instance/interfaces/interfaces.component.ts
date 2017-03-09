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

import { Component, OnInit } from '@angular/core';
import { InterfacesService } from './interfaces.service';
import { InstanceService } from '../instance.service';
import { InterfacesApiData } from './interfacesApiData';
import { isNullOrUndefined } from 'util';

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

    operations: string[] = null;
    inputParameters: Array<any> = null;
    outputParameters: Array<any> = null;

    columns: Array<any> = [
        {title: 'Name', name: 'name', sort: true},
        {title: 'Type', name: 'type', sort: true},
        {title: 'Required', name: 'required', sort: false}
    ];

    constructor(
        private servcie: InterfacesService,
        private sharedData: InstanceService
    ) {}

    ngOnInit() {
        this.servcie.setPath(this.sharedData.path);
        this.servcie.getInterfaces()
            .subscribe(
                data => this.handleInterfacesApiData(data),
                error => this.handleError(error)
            );
    }

    // region ########### Template Callbacks ##########
    onAddInterface(name: string) {
        this.interfacesData.push(new InterfacesApiData(name));
    }
    onKacke() {
        console.log('h');
    }
    // endregion

    // region ########## Private Methods ##########
    private handleInterfacesApiData(data: InterfacesApiData[]) {
        this.interfacesData = data;
        this.loading = false;
    }

    private handleError(error: any) {
        console.log(error);
    }
    // endregion
}
