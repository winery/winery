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
import { ChangeDetectorRef, Component, ElementRef, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { PropertiesDefinitionService } from '../propertiesDefinition.service';
import { WineryValidatorObject } from '../../../../wineryValidators/wineryDuplicateValidator.directive';
import { PropertiesDefinition, PropertiesDefinitionKVElement, WinerysPropertiesDefinition } from '../propertiesDefinitionsResourceApiData';
import { YamlPropertyDefinition } from '../yaml/yamlPropertyDefinition';
import { DataTypesService } from '../../../dataTypes/dataTypes.service';
import { Constraint, yaml_well_known, YamlWellKnown } from '../../../../model/constraint';
import { TDataType } from '../../../../../../../topologymodeler/src/app/models/ttopology-template';

const valid_constraint_keys = ['equal', 'greater_than', 'greater_or_equal', 'less_than', 'less_or_equal', 'in_range',
    'valid_values', 'length', 'min_length', 'max_length', 'pattern', 'schema'];
const list_constraint_keys = ['valid_values', 'in_range'];
const range_constraint_keys = ['in_range'];

@Component({
    selector: 'winery-properties-definition-editor',
    templateUrl: 'propertiesDefinitionEditor.component.html',
    providers: [
        PropertiesDefinitionService, DataTypesService
    ],
})
export class PropertiesDefinitionEditorComponent implements OnChanges {

    @Input() editedProperty: YamlPropertyDefinition | PropertiesDefinitionKVElement;
    @Input() propertyData: PropertiesDefinition | WinerysPropertiesDefinition;
    isValid: boolean;
    @ViewChild('editPropertyForm') exposedForm: ElementRef;

    validatorObject: WineryValidatorObject;
    // computed properties
    isYaml = (this.editedProperty as YamlPropertyDefinition) !== undefined;
    availableTypes: string[] = [];

    constructor(private service: PropertiesDefinitionService, private dataTypes: DataTypesService,
                private changeDetection: ChangeDetectorRef) {}

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.editedProperty) {
            this.editedProperty = changes.editedProperty.currentValue;
            this.availableTypes.length = 0;
            // create a validator object according to the property type we're dealing with
            if ((this.editedProperty as YamlPropertyDefinition) !== undefined) {
                this.isYaml = true;
                this.validatorObject = new WineryValidatorObject((this.propertyData as PropertiesDefinition).properties, 'name');

                // fill the available types with the types we know
                setTimeout(() => {
                    yaml_well_known.forEach(t => this.availableTypes.push(t));
                    this.dataTypes.getDataTypes().subscribe(
                        (types: TDataType[]) => types.forEach(t => this.availableTypes.push(`{${t.namespace}}${t.id}`)),
                        error => console.log(error),
                        // () => { this.availableTypes = yamlTypes; this.changeDetection.markForCheck(); }
                    );
                });
            } else {
                this.isYaml = false;
                setTimeout(() => {
                    this.availableTypes.push('xsd:string', 'xsd:float', 'xsd:decimal', 'xsd:anyURI', 'xsd:QName');
                });
                this.validatorObject = new WineryValidatorObject((this.propertyData as WinerysPropertiesDefinition).propertyDefinitionKVList, 'key');
            }
        }
    }

    addConstraint(selectedConstraintKey: string, constraintValue: string) {
        // lists have to be separated by ','
        if (list_constraint_keys.indexOf(selectedConstraintKey) > -1) {
            this.editedProperty.constraints.push(new Constraint(selectedConstraintKey, null, constraintValue.split(',')));
        } else {
            this.editedProperty.constraints.push(new Constraint(selectedConstraintKey, constraintValue, null));
        }
    }

    /**
     * removes item from constraint list
     * @param constraintClause
     */
    removeConstraint(constraintClause: Constraint) {
        const index = this.editedProperty.constraints.indexOf(constraintClause);
        if (index > -1) {
            this.editedProperty.constraints.splice(index, 1);
        }
    }

    get valid_constraint_keys() {
        return valid_constraint_keys;
    }

    get list_constraint_keys() {
        return list_constraint_keys;
    }

    get range_constraint_keys() {
        return range_constraint_keys;
    }
}
