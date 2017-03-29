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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { QNameList } from './qNameApiData';

@Component({
    selector: 'winery-qNameSelector',
    templateUrl: 'qNameSelector.component.html',
})
export class QNameSelectorComponent {

    @Input() title: string;
    @Input() displayList: QNameList;
    @Input() selectedResource: string;
    @Input() selectedValue: string;

    @Output() selectedValueChanged = new EventEmitter();

    qNameList: QNameList;
    openSuperClassLink = '';
    queryPath: string;

    constructor() {}

    onChange(value: string): void {
        this.selectedValue = value;
        this.setButtonLink();
        this.selectedValueChanged.emit({ value: this.selectedValue});
    }

    private handleData(availableSuperClasses: QNameList): void {

        this.qNameList = availableSuperClasses;
        this.setButtonLink();
    }

    private setButtonLink(): void {
        if (isNullOrUndefined(this.selectedValue)) {
            this.selectedValue = '(none)';
        }

        let parts = this.selectedValue.split('}');

        // can be '(none)'
        if (parts.length > 1) {
            let namespace = parts[0].slice(1);
            let name = parts[1];
            this.openSuperClassLink = '/' + this.selectedResource + '/' + encodeURIComponent(encodeURIComponent(namespace)) + '/' + name;
        }
    }
}
