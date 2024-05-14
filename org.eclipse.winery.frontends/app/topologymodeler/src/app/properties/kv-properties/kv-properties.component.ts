/*******************************************************************************
 * Copyright (c) 2020-2023 Contributors to the Eclipse Foundation
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
import { Subject, Subscription } from 'rxjs';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { WineryActions } from '../../redux/actions/winery.actions';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { KeyValueItem } from '../../../../../tosca-management/src/app/model/keyValueItem';
import { Utils } from '../../../../../tosca-management/src/app/wineryUtils/utils';
import { EntityType } from '../../models/ttopology-template';

@Component({
    selector: 'winery-kv-properties',
    templateUrl: './kv-properties.component.html',
    styleUrls: ['./kv-properties.component.css']
})
export class KvPropertiesComponent implements OnInit, OnDestroy {
    @Input() readonly: boolean;
    @Input() nodeProperties: object;
    @Input() nodeId: string;
    @Input() entityType: EntityType;

    @Output() propertyEdited: EventEmitter<KeyValueItem> = new EventEmitter<KeyValueItem>();

    invalidNodeProperties: any = {};
    kvPatternMap: any;
    kvDescriptionMap: any;
    checkEnabled: boolean;

    propertiesSubject: Subject<any> = new Subject<any>();
    debouncer: Subject<any> = new Subject<any>();
    subscriptions: Array<Subscription> = [];

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions) {
    }

    ngOnInit(): void {
        this.subscriptions.push(this.debouncer.pipe(debounceTime(50))
            .subscribe(target => {
                this.propertyEdited.emit({
                    key: target.name,
                    value: target.value,
                });
            }));

        if (this.nodeProperties) {
            this.initKVDescriptionMap();
            this.initKVPatternMap();
        }

        this.subscriptions.push(this.ngRedux.select((state) => {
            return state.topologyRendererState.buttonsState.checkNodePropertiesButton;
        })
            .subscribe((checked) => {
                this.checkEnabled = checked;
                if (this.checkEnabled) {
                    this.checkAllProperties();
                } else {
                    this.invalidNodeProperties = {};
                    this.ngRedux.dispatch(this.actions.setNodePropertyValidity(this.nodeId, true));
                }
            }));

        this.subscriptions.push(this.propertiesSubject.pipe(
            distinctUntilChanged(),
        ).subscribe((property) => {
            if (this.checkEnabled) {
                this.checkProperty(property.key, property.value);
            }
            this.propertyEdited.emit({ key: property.key, value: property.value });
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

    initKVDescriptionMap() {
        this.kvDescriptionMap = {};
        try {
            if (this.entityType) {
                const propertyDefinitionKVList =
                    this.entityType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertyDefinitionKVList;
                if (propertyDefinitionKVList) {
                    propertyDefinitionKVList.forEach((prop) => {
                        this.kvDescriptionMap[prop.key] = prop['description'];
                    });
                }
            }
        } catch (e) {
            console.error(e);
        }
    }

    initKVPatternMap() {
        this.kvPatternMap = {};
        try {
            if (this.entityType) {
                const propertyDefinitionKVList =
                    this.entityType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertyDefinitionKVList;
                if (propertyDefinitionKVList) {
                    propertyDefinitionKVList.forEach((prop) => {
                        this.kvPatternMap[prop.key] = prop['pattern'];
                    });
                }
            }
        } catch (e) {
            console.error(e);
        }
    }

    hasError(key: string): boolean {
        return !!this.invalidNodeProperties[key];
    }

    checkForErrors() {
        if (Object.keys(this.invalidNodeProperties).length > 0) {
            this.ngRedux.dispatch(this.actions.setNodePropertyValidity(this.nodeId, false));
        } else {
            this.ngRedux.dispatch(this.actions.setNodePropertyValidity(this.nodeId, true));
        }
    }

    checkAllProperties() {
        Object.keys(this.nodeProperties).forEach((key) => {
            this.checkProperty(key, this.nodeProperties[key]);
        });
        this.checkForErrors();
    }

    checkProperty(key: string, value: string) {
        try {
            delete this.invalidNodeProperties[key];
            if (value && this.kvPatternMap[key]) {
                if (!(this.isInputOrPropertyValue(value))) {
                    this.checkAndSetPattern(key, value);
                }
            }
        } catch (e) {
            console.log(e);
        } finally {
            this.checkForErrors();
        }
    }

    trackByFn(index, item) {
        return index;
    }

    checkAndSetPattern(key: string, value: string): void {
        const pattern = this.kvPatternMap[key];
        if (!new RegExp(pattern).test(value)) {
            this.invalidNodeProperties[key] = pattern;
        }
    }

    isInputOrPropertyValue(value: string): boolean {
        return value.startsWith('get_input:') || value.startsWith('get_property:');
    }
}
