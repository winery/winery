/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../../instance.service';
import { InheritanceService } from './inheritance.service';
import { InheritanceApiData } from './inheritanceApiData';
import { ToscaTypes } from '../../../wineryInterfaces/enums';
import { SelectData } from '../../../wineryInterfaces/selectData';
import { SelectItem } from 'ng2-select';

@Component({
    selector: 'winery-instance-inheritance',
    templateUrl: 'inheritance.component.html',
    providers: [InheritanceService],
})
export class InheritanceComponent implements OnInit {

    inheritanceApiData: InheritanceApiData;
    availableSuperClasses: SelectData[];
    toscaType: ToscaTypes;
    loading = true;
    openSuperClassLink = '';
    @ViewChild('derivedFromSelector') aboutModal: any;
    initialActiveItem: Array<any>;

    constructor(private sharedData: InstanceService,
                private service: InheritanceService,
                private notify: WineryNotificationService) {
    }

    ngOnInit() {
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
        this.toscaType = this.sharedData.toscaComponent.toscaType;
    }

    onSelectedValueChanged(value: SelectItem) {
        this.inheritanceApiData.derivedFrom = value.id;
        this.setButtonLink();
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
        this.initialActiveItem = [{'id': this.inheritanceApiData.derivedFrom, 'text': this.inheritanceApiData.derivedFrom.split('}').pop()}];
        if (!isNullOrUndefined(this.availableSuperClasses)) {
            this.loading = false;
        }
    }

    private handleSuperClassData(superClasses: SelectData[]) {
        this.availableSuperClasses = superClasses;

        if (!isNullOrUndefined(this.inheritanceApiData)) {
            this.loading = false;
        }
    }

    private handlePutResponse(response: any) {
        this.loading = false;
        this.notify.success('Saved changes', 'Success');
    }

    private handleError(error: any): void {
        this.loading = false;
        this.notify.error(error.toString(), 'Error');
    }

    private setButtonLink(): void {
        if (isNullOrUndefined(this.inheritanceApiData.derivedFrom)) {
            this.inheritanceApiData.derivedFrom = '(none)';
        }

        const parts = this.inheritanceApiData.derivedFrom.split('}');

        // can be '(none)'
        if (parts.length > 1) {
            const namespace = parts[0].slice(1);
            const name = parts[1];
            this.openSuperClassLink = '/' + 'nodetypes' + '/' + encodeURIComponent(encodeURIComponent(namespace)) + '/' + name;
        }
    }

}
