/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../../instance.service';
import { InheritanceService } from './inheritance.service';
import { InheritanceApiData } from './inheritanceApiData';
import { ToscaTypes } from '../../../wineryInterfaces/enums';
import { SelectData } from '../../../wineryInterfaces/selectData';
import { SelectItem } from 'ng2-select';
import { Router } from '@angular/router';
import { ModalDirective } from 'ngx-bootstrap';
import { WineryAddComponent } from '../../../wineryAddComponentModule/addComponent.component';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component({
    selector: 'winery-instance-inheritance',
    templateUrl: 'inheritance.component.html',
    providers: [InheritanceService],
})
export class InheritanceComponent implements OnInit {

    readonly noneElement: SelectData[] = [
        { text: 'None', id: 'none', children: [{ text: '(none)', id: '(none)' }] }
    ];

    inheritanceApiData: InheritanceApiData;
    availableSuperClasses: SelectData[];
    toscaType: ToscaTypes;
    loading = true;
    enableButton = false;
    @ViewChild('derivedFromSelector') aboutModal: ModalDirective;
    @ViewChild('addSubTypeModal') addSubTypeModal: WineryAddComponent;
    initialActiveItem: Array<SelectData>;

    constructor(public sharedData: InstanceService,
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

    onAddSubType() {
        this.addSubTypeModal.onAdd();
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

    private handlePutResponse(response: HttpResponse<string>) {
        this.getData();
        this.notify.success('Saved changes', 'Success');
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message, 'Error');
    }
}
