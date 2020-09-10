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

import { ValidatorFn } from '@angular/forms';

/**
 * Object model which describes the data which should be displayed inside the table
 * and how the forms to add/edit data should look like and their behavior.
 * All Metadata types are children of this class.
 *
 * <label>Attributes</label>
 * <ul>
 *     <li><code>key</code> The key of the column to use</li>
 *     <li><code>label</code> Label of the form and of the column</li>
 *     <li><code>defaultValue</code> Default value set in the form</li>
 *     <li><code>order</code> Orders forms by number lowest to highest number</li>
 *     <li><code>sortTableCol</code> Whether the table should automatically sort by the property</li>
 *     <li><code>disabled</code> Whether to disable the form inside the modal</li>
 *     <li><code>validation</code> Accepts standard Angular validators, used in the form</li>
 *     <li><code>controlType</code> Specifies the component type, needed to automatically load
 *     components based on this value. The whole list can be found in {@link WineryDynamicFieldDirective}</li>
 * </ul>
 */
export class WineryDynamicTableMetadata<T = any> {
    controlType: string;

    constructor(
        public key: string,
        public label?: string,
        public order?: number,
        public defaultValue?: T,
        public disabled?: boolean,
        public sortTableCol?: boolean,
        public isVisible?: boolean,
        public validation?: ValidatorFn[] | ValidatorFn) {

        this.label = label || '';
        this.disabled = disabled || false;
        this.order = order === undefined ? 1 : order;
        this.sortTableCol = sortTableCol || false;
        if (isVisible === true || isVisible === false) {
            this.isVisible = isVisible;
        } else {
            this.isVisible = true;
        }
    }

    /**
     * utility method to make complicated data human readable
     * used to show data inside winery tables in a human readable form
     *
     * can be overwritten by certain child classes e.g. in {@link DynamicConstraintsData}
     * @param value
     */
    public toHumanReadable(value: T): string {
        if (!value) {
            return '';
        }
        return value.toString();
    }
}
