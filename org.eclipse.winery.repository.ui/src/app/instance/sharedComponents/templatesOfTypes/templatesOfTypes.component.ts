/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, OnInit } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { TemplatesOfTypeService } from './templatesOfTypes.service';
import { ImplementationAPIData } from '../implementations/implementationAPIData';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';

@Component({
    selector: 'winery-templates-of-type',
    templateUrl: 'templatesOfType.component.html',
    providers: [TemplatesOfTypeService,
        WineryNotificationService],
})
export class TemplatesOfTypeComponent implements OnInit {
    templateData: ImplementationAPIData[];
    loading = true;
    selectedCell: any;
    validatorObject: WineryValidatorObject;
    columns: Array<WineryTableColumn> = [
        { title: 'Namespace', name: 'namespace', sort: true },
        { title: 'Name', name: 'localname', sort: true },
    ];

    constructor(private service: TemplatesOfTypeService,
                private notificationService: WineryNotificationService) {
        this.templateData = [];
    }

    ngOnInit() {
        this.getImplementationData();
    }

    // region ######## table methods ########
    onCellSelected(data: any) {
        if (!isNullOrUndefined(data)) {
            this.selectedCell = data.row;
        }
    }

    // endregion

    // region ######## call service methods and subscription handlers ########
    private getImplementationData(): void {
        this.service.getTemplateData()
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }

    private handleData(impl: ImplementationAPIData[]) {
        this.templateData = impl;
        if (this.service.getPath().includes('artifact')) {
            this.templateData = this.templateData.map(item => {
                const url = '/#/' + 'artifacttemplates' + '/' + encodeURIComponent(encodeURIComponent(item.namespace))
                    + '/' + item.localname;
                item.localname = '<a href="' + url + '">' + item.localname + '</a>';
                return item;
            })
        } else if (this.service.getPath().includes('policy')) {
            this.templateData = this.templateData.map(item => {
                const url = '/#/' + 'policytemplates' + '/' + encodeURIComponent(encodeURIComponent(item.namespace))
                    + '/' + item.localname;
                item.localname = '<a href="' + url + '">' + item.localname + '</a>';
                return item;
            })
        }
        this.loading = false;
    }

    private handleError(error: any): void {
        this.loading = false;
        this.notificationService.error('Action caused an error:\n', error);
    }

    // endregion
}
