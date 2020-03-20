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

import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { KeyValueItem } from '../../../../../tosca-management/src/app/model/keyValueItem';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
    selector: 'winery-kv-properties',
    templateUrl: './kv-properties.component.html',
    // styleUrls: ['./kv-properties.component.css'],
})
export class KvPropertiesComponent implements OnInit, OnDestroy {
    @Input() readonly: boolean;
    @Input() nodeProperties: object;

    @Output() propertyEdited: EventEmitter<KeyValueItem> = new EventEmitter<KeyValueItem>();

    properties: Subject<string> = new Subject<string>();
    keySubject: Subject<string> = new Subject<string>();

    // local storage of the edited key for outgoing events
    key: string;
    subscriptions: Array<Subscription> = [];

    ngOnInit(): void {
        // find out which row was edited by key
        this.subscriptions.push(this.keySubject.pipe(
            debounceTime(200),
            distinctUntilChanged(), )
            .subscribe(key => {
                this.key = key;
            }));
        this.subscriptions.push(this.properties.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(propertyValue => {
                this.propertyEdited.emit({
                    key: this.key,
                    value: propertyValue,
                });
            }));
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(s => s.unsubscribe());
    }

}
