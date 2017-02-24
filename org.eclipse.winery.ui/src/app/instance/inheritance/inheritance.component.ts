/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 */

import { Component, OnInit } from '@angular/core';
import { InheritanceService } from './inheritance.service';
import { InheritanceApiData } from './inheritanceApiData';
import { InstanceService } from '../instance.service';
import { QNameList } from '../../qNameSelector/qNameApiData';
import { isNullOrUndefined } from 'util';


@Component({
    selector: 'winery-instance-inheritance',
    templateUrl: 'inheritance.component.html',
    providers: [InheritanceService],
})
export class InheritanceComponent implements OnInit {

    inheritanceApiData: InheritanceApiData;
    availableSuperClasses: QNameList;
    loading: boolean = true;

    constructor(
        private sharedData: InstanceService,
        private service: InheritanceService
    ) {}

    ngOnInit() {
        this.service.setPath(this.sharedData.path);
        this.service.getInheritanceData()
            .subscribe(
                data => this.handleInheritanceData(data),
                error => this.handleError(error)
            );
        this.service.getAvailableSuperClasses()
            .subscribe(
                data => this.handleSuperClassData(data),
                error => this.handleError(error)
            );
    }

    onSelectedValueChanged(value: string) {
        this.inheritanceApiData.derivedFrom = value;
    }

    public saveToServer(): void {
        this.loading = true;
        this.service.saveInheritanceData(this.inheritanceApiData)
            .subscribe(
                data => this.handlePutResponse(data),
                error => this.handleError(error)
            );
    }

    private handleInheritanceData(inheritance: InheritanceApiData) {
        this.inheritanceApiData = inheritance;

        if (!isNullOrUndefined(this.availableSuperClasses)) {
            this.loading = false;
        }
    }

    private handleSuperClassData(superClasses: QNameList) {
        this.availableSuperClasses = superClasses;

        if (!isNullOrUndefined(this.inheritanceApiData)) {
            this.loading = false;
        }
    }

    private handlePutResponse(response: any) {
        this.loading = false;
        console.log(response);
    }

    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }

}
