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
import { KeyValueItem } from '../../../../../tosca-management/src/app/model/keyValueItem';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
    selector: 'winery-yaml-properties',
    templateUrl: './yaml-properties.component.html',
    // styleUrls: ['./yaml-properties.component.css'],
})
export class YamlPropertiesComponent implements OnInit, OnChanges, OnDestroy {
    @Input() readonly: boolean;
    @Input() nodeProperties: object;

    @Output() propertyEdited: EventEmitter<KeyValueItem> = new EventEmitter<KeyValueItem>();

    properties: Array<KeyValueItem>;
    valueSubject: Subject<string> = new Subject<string>();
    keySubject: Subject<string> = new Subject<string>();

    // local storage of the edited key for outgoing events
    private key: string;
    private subscriptions: Array<Subscription> = [];

    ngOnInit(): void {
        // find out which row was edited by key
        this.subscriptions.push(this.keySubject.pipe(
            debounceTime(200),
            distinctUntilChanged(), )
            .subscribe(key => {
                this.key = key;
            }));
        this.subscriptions.push(this.valueSubject.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(value => {
                this.propertyEdited.emit({
                    key: this.key,
                    value: value,
                });
            }));
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.nodeProperties) {
            // TODO flattening the object graph for yaml properties is a more involved operation than we can leave to the keysPipe
            // this.properties = this.flatten(changes.nodeProperties.currentValue);
            this.properties = changes.nodeProperties.currentValue;
        }
    }

    private flatten(properties: any): Array<KeyValueItem> {
        const result = [];
        for (const key in properties) {
            if (properties.hasOwnProperty(key)) {
                result.push({key: key, value: properties[key]});
            }
        }
        return result;
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(s => s.unsubscribe());
    }
}
