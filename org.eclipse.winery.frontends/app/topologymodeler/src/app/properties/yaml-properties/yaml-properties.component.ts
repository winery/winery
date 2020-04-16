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
import { BackendService } from '../../services/backend.service';
import { EntityType, TDataType } from '../../models/ttopology-template';
import { QName } from '../../models/qname';
import { InheritanceUtils } from '../../models/InheritanceUtils';

@Component({
    selector: 'winery-yaml-properties',
    templateUrl: './yaml-properties.component.html',
    // styleUrls: ['./yaml-properties.component.css'],
})
export class YamlPropertiesComponent implements OnChanges, OnDestroy {
    @Input() readonly: boolean;
    @Input() nodeProperties: object;
    @Input() nodeType: string;

    @Output() propertyEdited: EventEmitter<KeyValueItem> = new EventEmitter<KeyValueItem>();

    propertyValues: any;
    properties: Array<any>;

    // subject allows for debouncing
    private outputSubject: Subject<KeyValueItem> = new Subject<KeyValueItem>();
    private nodeTypes: Array<EntityType> = [];
    private dataTypes: Array<TDataType> = [];
    private subscriptions: Array<Subscription> = [];

    constructor(private backend: BackendService) {
        this.subscriptions.push(this.backend.model$.subscribe(
            model => {
                this.nodeTypes = model.unGroupedNodeTypes;
                this.dataTypes = model.dataTypes;
            }
        ));
        this.subscriptions.push(this.outputSubject.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(kv => this.propertyEdited.emit(kv)
        ));
    }

    ngOnChanges(changes: SimpleChanges): void {
        // TODO flattening the object graph for yaml properties is a more involved operation than we can leave to the keysPipe
        if (changes.nodeProperties) {
            this.propertyValues = changes.nodeProperties.currentValue;
        }
        if (changes.nodeType) {
            this.nodeType = changes.nodeType.currentValue;
            this.determineProperties();
        }
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(s => s.unsubscribe());
    }

    propertyChangeRequest(target: EventTarget, definition: any) {
        if (this.isValid(target.value, definition.constraints)) {
            this.outputSubject.next({
                key: definition.name,
                value: target.value,
            });
        }
    }

    private isValid(value: any, constraints: any): boolean {
        if (constraints === null) { return true; }
        return true;
    }

    private determineProperties(): void {
        const inheritance = InheritanceUtils.getInheritanceAncestry(this.nodeType, this.nodeTypes);
        const definedProperties = [];
        for (const type of inheritance) {
            const definition = type.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0];
            for (const propertyDefinition of definition.properties || []) {
                // FIXME deal with constraints coming from datatypes
                definedProperties.push(propertyDefinition);
            }
        }
        this.properties = definedProperties;
    }
}
