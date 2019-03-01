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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {isNullOrUndefined} from 'util';
import {InterfacesApiData} from '../interfacesApiData';
import { InstanceService } from '../../../instance.service';

@Component({
    selector: 'winery-selectable-list',
    templateUrl: 'selectableList.component.html',
    styleUrls: [
        'selectableList.component.css',
    ],
})
export class SelectableListComponent implements OnInit {

    @Input() rows: Array<any>;
    @Input() title: string;

    @Output() removeButtonClicked = new EventEmitter<any>();
    @Output() addButtonClicked = new EventEmitter<any>();
    @Output() selectionChanged = new EventEmitter<any>();
    currentSelected: any;

    constructor(public sharedData: InstanceService) {
    }

    ngOnInit() {
        if (isNullOrUndefined(this.rows)) {
            this.rows = [];
        }
    }

    onAdd($event: Event) {
        $event.stopPropagation();
        this.addButtonClicked.emit();
    }

    onRemove($event: Event) {
        $event.stopPropagation();
        this.removeButtonClicked.emit(this.currentSelected);
    }

    onChange(value: any) {
        this.currentSelected = value;
        this.selectionChanged.emit(value);
    }

    selectItem(item: InterfacesApiData) {
        this.currentSelected = item;
        this.onChange(item);
    }
}
