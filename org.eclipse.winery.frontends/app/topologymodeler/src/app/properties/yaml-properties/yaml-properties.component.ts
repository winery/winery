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
import { InheritanceUtils } from '../../models/InheritanceUtils';
import { ConstraintChecking } from '../property-constraints';
import { ToscaUtils } from '../../models/toscaUtils';
import { isWellKnown } from '../../../../../tosca-management/src/app/model/constraint';


@Component({
    selector: 'winery-yaml-properties',
    templateUrl: './yaml-properties.component.html',
})
export class YamlPropertiesComponent implements OnChanges, OnDestroy {
    @Input() readonly: boolean;
    @Input() properties: object;
    @Input() templateType: string;

    @Output() propertyEdited: EventEmitter<KeyValueItem> = new EventEmitter<KeyValueItem>();

    propertyValues: any;
    propertyDefinitions: Array<any>;

    private outputDebouncer: Subject<KeyValueItem> = new Subject<KeyValueItem>();
    private nodeTypes: Array<EntityType> = [];
    private subscriptions: Array<Subscription> = [];

    constructor(private backend: BackendService) {
        this.subscriptions.push(this.backend.model$.subscribe(
            model => {
                this.nodeTypes = model.unGroupedNodeTypes.concat(model.relationshipTypes);
            }
        ));
        this.subscriptions.push(this.outputDebouncer.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(kv => this.propertyEdited.emit(kv)));
    }

    ngOnChanges(changes: SimpleChanges): void {
        // TODO flattening the object graph for yaml properties is a more involved operation than we can leave to the keysPipe
        if (changes.templateType) {
            this.templateType = changes.templateType.currentValue;
            this.determineProperties();
        }
        if (changes.properties) {
            this.propertyValues = changes.properties.currentValue;
            // values that are property functions need to be marked as complex
            this.backfillPropertyDefaults();
        }
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(s => s.unsubscribe());
    }

    propertyChangeRequest(validValue: any, definition: any) {
        this.outputDebouncer.next({
            key: definition.name,
            value: validValue,
        });
    }

    static isValid(value: any, constraints: any): boolean {
        if (constraints === null) { return true; }
        for (const c of constraints) {
            ConstraintChecking.isValid(c, value);
        }
        return true;
    }

    private determineProperties(): void {
        const inheritance = InheritanceUtils.getInheritanceAncestry(this.templateType, this.nodeTypes);
        const definedProperties = [];
        for (const type of inheritance) {
            const definition = ToscaUtils.getDefinition(type);
            if (definition.propertiesDefinition === undefined) {
                continue;
            }
            for (const propertyDefinition of definition.propertiesDefinition.properties || []) {
                // only add properties that have not been overwritten by subtypes.
                // "lower" types come earlier in the ancestry list, that's why we can afford this
                if (definedProperties.find(def => def.name === propertyDefinition.name) === undefined) {
                    definedProperties.push(propertyDefinition);
                }
            }
        }
        this.propertyDefinitions = definedProperties;
    }

    private backfillPropertyDefaults() {
        for (const propDefinition of this.propertyDefinitions) {
            if (this.propertyValues[propDefinition.name] === undefined) {
                this.propertyValues[propDefinition.name] = propDefinition.defaultValue || '';
            }
        }
    }
}
