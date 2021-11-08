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

import { WineryDynamicTableMetadata } from '../../wineryDynamicTableMetadata';
import { Component, OnInit } from '@angular/core';
import { DynamicFieldComponent } from '../dynamic-field/dynamicFieldComponent';
import { FormGroup } from '@angular/forms';
import { Constraint } from '../../../model/constraint';

@Component({
    selector: 'winery-dynamic-constraints',
    templateUrl: 'dynamicConstraints.component.html',
    styleUrls: [
        'dynamicConstraints.component.css'
    ],
})
export class DynamicConstraintsComponent implements DynamicFieldComponent, OnInit {
    config: DynamicConstraintsData;
    group: FormGroup;

    ngOnInit(): void {
        if (this.config.defaultValue) {
            this.group.controls[this.config.key].setValue(this.config.defaultValue);
        }
    }

    removeConstraint(clause: Constraint) {
        const tmp = (this.currentConstraints as Constraint[])
            .filter((item: any) => !(item.key === clause.key && item.value === clause.value));
        this.group.controls[this.config.key].setValue(tmp);
    }

    addConstraint(clause: Constraint) {
        if (clause && !this.alreadyExists(clause)) {
            this.currentConstraints.push(clause);
        }
    }

    alreadyExists(clause: Constraint) {
        return this.currentConstraints
            .find((item) => item.key === clause.key && item.value === clause.value);
    }

    toConstraint(key: string, value: string): Constraint {
        let clauseList: string[] = [];
        if (this.config.listConstraintsKeys.find((listKey) => listKey === key)) {
            clauseList = value.split(',');
            value = '';
        } else if (this.config.rangeConstraintsKeys.find((listKey) => listKey === key)) {
            clauseList = value.split(',');
            if (clauseList.length !== 2) {
                // range constraints can not contain more than 2 values
                return undefined;
            }
            value = '';
        }
        return new Constraint(key, value, clauseList);
    }

    get currentConstraints(): Constraint[] {
        if (!this.group.controls[this.config.key].value) {
            this.group.controls[this.config.key].setValue([]);
        }
        return this.group.controls[this.config.key].value as Constraint[];
    }
}

export class DynamicConstraintsData extends WineryDynamicTableMetadata<Constraint[]> {
    controlType = 'constraints';

    constructor(key: string,
                label: string,
                public possibleConstraintKeys: string[],
                public listConstraintsKeys: string[],
                public rangeConstraintsKeys: string[],
                order?: number,
                disabled?: boolean,
                sortTableCol?: boolean,
                isVisible?: boolean) {
        super(key, label, order, undefined, disabled, sortTableCol, isVisible);
        this.possibleConstraintKeys = possibleConstraintKeys || [];
        this.listConstraintsKeys = listConstraintsKeys || [];
        this.rangeConstraintsKeys = rangeConstraintsKeys || [];
    }

    // @override
    toHumanReadable(value: Constraint[]): string {
        let res = '';
        let length = value.length;
        for (const clause of value) {
            res += clause.key + ': ';
            if (clause.list && clause.list.length > 0) {
                res += clause.list.toString();
            } else {
                res += clause.value;
            }
            length -= 1;
            if (length > 0) {
                res += '; ';
            }
        }
        return res;
    }
}
