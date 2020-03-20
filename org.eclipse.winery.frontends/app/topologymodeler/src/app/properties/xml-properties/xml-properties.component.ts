/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
    selector: 'winery-xml-properties',
    templateUrl: './xml-properties.component.html',
    // styleUrls: ['./xml-properties.component.css']
})
export class XmlPropertiesComponent implements OnInit, OnDestroy {
    @Input() readonly: boolean;
    @Input() propertiesValue: any;

    @Output() propertyEdited: EventEmitter<string> = new EventEmitter<string>();
    properties: Subject<string> = new Subject<string>();

    subscriptions: Array<Subscription> = [];

    ngOnInit(): void {
        this.subscriptions.push(this.properties.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(value => {
                this.propertyEdited.emit(value);
            }));
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(s => s.unsubscribe());
    }
}
