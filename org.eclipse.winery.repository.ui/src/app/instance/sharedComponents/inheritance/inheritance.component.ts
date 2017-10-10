/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { Response } from '@angular/http';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../../instance.service';
import { InheritanceService } from './inheritance.service';
import { InheritanceApiData } from './inheritanceApiData';
import { ToscaTypes } from '../../../wineryInterfaces/enums';
import { SelectData } from '../../../wineryInterfaces/selectData';
import { SelectItem } from 'ng2-select';
import { Router } from '@angular/router';

@Component({
    selector: 'winery-instance-inheritance',
    templateUrl: 'inheritance.component.html',
    providers: [InheritanceService],
})
export class InheritanceComponent implements OnInit {

    readonly noneElement: SelectData[] = [{ text: 'None', id: 'none', children: [{ text: '(none)', id: '(none)' }] }];

    inheritanceApiData: InheritanceApiData;
    availableSuperClasses: SelectData[];
    toscaType: ToscaTypes;
    loading = true;
    enableButton = false;
    @ViewChild('derivedFromSelector') aboutModal: any;
    initialActiveItem: Array<SelectData>;

    constructor(private sharedData: InstanceService,
                private service: InheritanceService,
                private notify: WineryNotificationService, private router: Router) {
    }

    ngOnInit() {
        this.getData();
        this.service.getAvailableSuperClasses()
            .subscribe(
                data => this.handleSuperClassData(data),
                error => this.handleError(error)
            );
        this.toscaType = this.sharedData.toscaComponent.toscaType;
    }

    getData() {
        this.service.getInheritanceData()
            .subscribe(
                data => this.handleInheritanceData(data),
                error => this.handleError(error)
            );
    }

    onSelectedValueChanged(value: SelectItem) {
        this.inheritanceApiData.derivedFrom = value.id;
        this.enableButton = this.inheritanceApiData.derivedFrom !== '(none)';
    }

    saveToServer(): void {
        this.loading = true;
        this.service.saveInheritanceData(this.inheritanceApiData)
            .subscribe(
                data => this.handlePutResponse(data),
                error => this.handleError(error)
            );
    }

    onButtonClick() {
        const parts = this.inheritanceApiData.derivedFrom.split('}');
        const namespace = parts[0].slice(1);
        const name = parts[1];
        this.router.navigate([this.toscaType + '/' + encodeURIComponent(namespace) + '/' + name]);
    }

    private handleInheritanceData(inheritance: InheritanceApiData) {
        this.inheritanceApiData = inheritance;
        this.initialActiveItem = [{
            'id': this.inheritanceApiData.derivedFrom, 'text': this.inheritanceApiData.derivedFrom.split('}').pop()
        }];
        if (!isNullOrUndefined(this.availableSuperClasses)) {
            this.loading = false;
            this.enableButton = this.inheritanceApiData.derivedFrom !== '(none)';
        }
    }

    private handleSuperClassData(superClasses: SelectData[]) {
        this.availableSuperClasses = this.noneElement.concat(superClasses);

        if (!isNullOrUndefined(this.inheritanceApiData)) {
            this.loading = false;
            this.enableButton = this.inheritanceApiData.derivedFrom !== '(none)';
        }
    }

    private handlePutResponse(response: Response) {
        this.getData();
        this.notify.success('Saved changes', 'Success');
    }

    private handleError(error: Response): void {
        this.loading = false;
        this.notify.error(error.text(), 'Error');
    }

}
