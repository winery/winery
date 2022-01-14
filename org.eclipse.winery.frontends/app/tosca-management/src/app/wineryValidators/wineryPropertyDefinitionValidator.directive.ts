/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { Directive, Input } from '@angular/core';
import {
    AbstractControl, NG_VALIDATORS, ValidationErrors, Validator
} from '@angular/forms';
import {
    PropertiesDefinitionKVElement
} from '../instance/sharedComponents/propertiesDefinition/propertiesDefinition.types';
import { ConstraintChecking } from '../../../../topologymodeler/src/app/properties/property-constraints';

@Directive({
    selector: '[wineryPropertyDefinition]',
    providers: [{ provide: NG_VALIDATORS, useExisting: WineryPropertyDefinitionValidatorDirective, multi: true }]
})
export class WineryPropertyDefinitionValidatorDirective implements Validator {

    @Input() wineryPropertyDefinition: PropertiesDefinitionKVElement;

    private patterns: { [type: string]: RegExp } = {
        'xsd:float': new RegExp(/^\s*([+-]?((0|[1-9][0-9]*)(\.[0-9]*)?|\.[0-9]+)([Ee][+-]?[0-9]+)?)\s*$/g),
        'xsd:integer': new RegExp(/^\s*[+-]?(0|[1-9][0-9]*)([Ee][+]?[0-9]+)?\s*$/g),
        'xsd:decimal': new RegExp(/^\d*\.?\d*$/g),
        'xsd:anyURI': new RegExp(/^([a-zA-Z]\:|\\\\[^\/\\:*?"<>|]+\\[^\/\\:*?"<>|]+)(\\[^\/\\:*?"<>|]+)+(\.[^\/\\:*?"<>|]+)$/g),
        'xsd:qName': new RegExp(/^\{(.*?)\}(.*)$/g)
    };


    validate(control: AbstractControl): ValidationErrors | null {
        console.log(control);
        const value = control.value;

        // Assert required
        if (this.wineryPropertyDefinition.required && !value) {
            return { required: this.wineryPropertyDefinition.required };
        }

        // Assert type
        const pattern = this.patterns[this.wineryPropertyDefinition.type];
        if (pattern) {
            if (!pattern.test(value)) {
                return { type: this.wineryPropertyDefinition.type };
            }
        }

        // Assert constraints
        if (this.wineryPropertyDefinition.constraints) {
            for (const constraint of this.wineryPropertyDefinition.constraints) {
                if (!ConstraintChecking.isValid({
                    operator: constraint.key,
                    value: constraint.list || constraint.value
                }, value)) {
                    return { constraint: constraint.key };
                }
            }

            return null;
        }
    }
}
