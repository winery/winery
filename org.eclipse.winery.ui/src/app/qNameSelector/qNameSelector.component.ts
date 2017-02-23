/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 *******************************************************************************/

import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { QNameService } from './qNameSelector.service';
import { AvailableSuperclassesApiData } from './availableSuperclassesApiData';
import { NotificationsService } from "angular2-notifications";

@Component({
    selector: 'winery-qNameSelector',
    templateUrl: 'qNameSelector.component.html',
    providers: [
      QNameService
    ],
})
export class QNameSelectorComponent implements OnInit {

    @Input() title: string;
    @Input() selectedValue: string;
    @Input() selectedResource: string;
    @Input() selectedNamespace: string;
    @Input() selectedComponentId: string;

    @Output() selectedValueChanged = new EventEmitter();

    availableSuperClasses: AvailableSuperclassesApiData;
    openSuperClassLink: string = '';
    queryPath: string;
    loading: boolean = true;


    constructor(private service: QNameService, private notify: NotificationsService) {}

    ngOnInit() {
        this.selectedResource = this.selectedResource.toLowerCase() + 's';

        this.queryPath = '/' + this.selectedResource
            + '/' + encodeURIComponent(encodeURIComponent(encodeURIComponent(this.selectedNamespace)))
            + '/' + this.selectedComponentId
            + '/getAvailableSuperClasses';

        this.service.getAvailableSuperClasses(this.queryPath)
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }

    onChange(value: string): void {
        this.selectedValue = value;
        this.setButtonLink();
        this.selectedValueChanged.emit({ value: this.selectedValue});
    }

    private handleData(availableSuperClasses: AvailableSuperclassesApiData): void {
        this.availableSuperClasses = availableSuperClasses;
        this.setButtonLink();
        this.loading = false;
    }

    private handleError(error: any): void {
        this.loading = false;
        this.notify.error('Error', 'An error has occured: ' + error);
    }

    private setButtonLink(): void {
        let parts = this.selectedValue.split('}');

        // can be '(none)'
        if (parts.length > 1) {
            let namespace = parts[0].slice(1);
            let name = parts[1];
            this.openSuperClassLink = '/' + this.selectedResource + '/' + encodeURIComponent(encodeURIComponent(namespace)) + '/' + name;
        }
    }
}
