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
    // styleUrls: ['./yaml-properties.component.css'],
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
    private dataTypes: Array<TDataType> = [];
    private subscriptions: Array<Subscription> = [];
    JSON: JSON;

    constructor(private backend: BackendService) {
        this.subscriptions.push(this.backend.model$.subscribe(
            model => {
                this.nodeTypes = model.unGroupedNodeTypes.concat(model.relationshipTypes);
                this.dataTypes = model.dataTypes;
            }
        ));
        this.subscriptions.push(this.outputDebouncer.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(kv => this.propertyEdited.emit(kv)));
        // this is a fix to make the global javascript object available inside the component template
        this.JSON = JSON;
    }

    ngOnChanges(changes: SimpleChanges): void {
        // TODO flattening the object graph for yaml properties is a more involved operation than we can leave to the keysPipe
        if (changes.templateType) {
            this.templateType = changes.templateType.currentValue;
            this.determineProperties();
        }
        if (changes.properties) {
            this.propertyValues = changes.properties.currentValue;
            this.backfillPropertyDefaults();
        }
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(s => s.unsubscribe());
    }

    propertyChangeRequest(target: any, definition: any) {
        let result;
        try {
            result = JSON.parse(target.value);
        } catch (e) {
            // try reparsing as string
            try {
                result = JSON.parse( '"' + target.value + '"');
            } catch (e) {
                console.log('failed to parse value', target.value);
                return;
            }
        }
        if (YamlPropertiesComponent.isValid(result, definition.constraints)) {
            this.outputDebouncer.next({
                key: definition.name,
                value: result,
            });
        }
    }

    private static isValid(value: any, constraints: any): boolean {
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
            if (definition.properties === undefined) {
                continue;
            }
            for (const propertyDefinition of definition.properties.properties || []) {
                if (isWellKnown(propertyDefinition.type)) {
                    // the property type is a simple type like "string" or "integer"
                    if (propertyDefinition.type === 'list' || propertyDefinition.type === 'map') {
                        // known types list and map require JSON processing, which is tied to the "complex" flag
                        propertyDefinition.complex = true;
                    }
                    definedProperties.push(propertyDefinition);
                } else {
                    this.handleDataType(propertyDefinition, definedProperties);
                }
            }
        }
        this.propertyDefinitions = definedProperties;
    }

    private handleDataType(propertyDefinition: any, definedProperties: any[]) {
        // FIXME the inheritance hierarchy resolution for a type may need to be namespace-aware
        const dataTypeInheritance = InheritanceUtils.getInheritanceAncestry(propertyDefinition.type, this.dataTypes);
        // FIXME we may have messed up some kind of normalization on the backend
        if (dataTypeInheritance.some(t => t.properties || ToscaUtils.getDefinition(t).properties)) {
            this.handleComplexDataType(dataTypeInheritance, propertyDefinition, definedProperties);
        } else {
            // aggregate constraints through the hierarchy
            const push: any = {};
            Object.assign(push, propertyDefinition);
            if (push.constraints === undefined) {
                push.constraints = [];
            }
            for (const ancestor of dataTypeInheritance) {
                // no need to check for ancestor properties, that's handled by handleComplexDataType
                push.constraints.push(ToscaUtils.getDefinition(ancestor).constraints);
            }
        }
    }

    private handleComplexDataType(dataTypeInheritance: EntityType[], propertyDefinition: any, definedProperties: any[]) {
        // FIXME need to find some way to represent hierarchical data types
        // TODO the inheritance hierarchy resolution for a type may need to be namespace-aware
        propertyDefinition.complex = true;
        definedProperties.push(propertyDefinition);
        console.warn('pushing complex typed property to definedProperties without flattening!');
    }

    private backfillPropertyDefaults() {
        for (const propDefinition of this.propertyDefinitions) {
            if (this.propertyValues[propDefinition.name] === undefined) {
                this.propertyValues[propDefinition.name] = propDefinition.defaultValue || '';
            }
        }
    }

    unQuote(value: string): string {
        if (value.startsWith('"') && value.endsWith('"')) {
            return value.substr(1, value.length - 2);
        }
        return value;
    }
}
