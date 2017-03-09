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

import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import { isNullOrUndefined } from 'util';

@Component({
    selector: 'winery-selectableList',
    templateUrl: 'selectableList.component.html',
    styleUrls: [
        'selectableList.component.css',
    ],
})
export class SelectableListComponent implements OnInit {

    @Input() rows: Array<any>;
    @Input() title: string;

    @Output() removeButtonClicked: EventEmitter<any>;
    @Output() addButtonClicked: EventEmitter<any>;

    currentSelected: any;

    constructor() {
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
}
