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
import { debounceTime } from 'rxjs/operators';
import { Utils } from '../../../../../tosca-management/src/app/wineryUtils/utils';

@Component({
    selector: 'winery-kv-properties',
    templateUrl: './kv-properties.component.html',
})
export class KvPropertiesComponent implements OnInit, OnDestroy {
    @Input() readonly: boolean;
    @Input() nodeProperties: object;

    @Output() propertyEdited: EventEmitter<KeyValueItem> = new EventEmitter<KeyValueItem>();

    debouncer: Subject<any> = new Subject<any>();
    subscriptions: Array<Subscription> = [];

    ngOnInit(): void {
        this.subscriptions.push(this.debouncer.pipe(debounceTime(50))
            .subscribe(target => {
                this.propertyEdited.emit({
                    key: target.name,
                    value: target.value,
                });
            }));
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(s => s.unsubscribe());
    }

    isEmpty(): boolean {
        return !this.nodeProperties || Utils.isEmpty(this.nodeProperties);
    }

    keyup(target: any): void {
        this.debouncer.next(target);
    }
}
