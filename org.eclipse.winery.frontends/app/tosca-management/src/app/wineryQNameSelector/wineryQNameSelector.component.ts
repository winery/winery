/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { NameAndQNameApiDataList } from './wineryNameAndQNameApiData';
import { ToscaTypes } from '../model/enums';

/**
 * This component provides a selector for QNames in addition with a link to the currently
 * selected QName.
 *
 * <label>Inputs</label>
 * <ul>
 *     <li><code>title</code> sets the title for the selector.
 *     </li>
 *     <li><code>displayList</code> the list of QNames in {@link NameAndQNameApiDataList} format. This field is
 *         mandatory!
 *     <li><code>toscaType</code>
 *     </li>
 *     <li><code>selectedValue</code> sets the currently selected value in the dropdown
 *     </li>
 * </ul>
 *
 * <label>Outputs</label>
 * <ul>
 *     <li><code>selectedValueChanged</code> emits the selected value in the dropdown.
 *     </li>
 * </ul>
 *
 * @example <caption>Basic usage</caption>
 * ```html
 * <winery-qname-selector
 *     [title]="'Derived from'"
 *     [displayList]="availableSuperClasses"
 *     [toscaType]="toscaType"
 *     [selectedValue]="inheritanceApiData.derivedFrom"
 *     (selectedValueChanged)="onSelectedValueChanged($event.value)">
 * </winery-qname-selector>
 * ```
 */
@Component({
    selector: 'winery-qname-selector',
    templateUrl: './wineryQNameSelector.component.html',
})
export class WineryQNameSelectorComponent implements OnInit {

    @Input() title: string;
    @Input() displayList: NameAndQNameApiDataList;
    @Input() toscaType: ToscaTypes;
    @Input() selectedValue: string;
    @Input() width = 600;
    @Input() showOpenButton = true;
    @Output() selectedValueChanged = new EventEmitter();

    qNameList: NameAndQNameApiDataList;
    openSuperClassLink = '';
    queryPath: string;

    constructor() {
    }

    ngOnInit() {
        this.setButtonLink();
    }

    onChange(value: string): void {
        this.selectedValue = value;
        this.setButtonLink();
        this.selectedValueChanged.emit({ value: this.selectedValue });
    }

    private handleData(availableSuperClasses: NameAndQNameApiDataList): void {

        this.qNameList = availableSuperClasses;
        this.setButtonLink();
    }

    private setButtonLink(): void {
        if (isNullOrUndefined(this.selectedValue)) {
            this.selectedValue = '(none)';
        }

        const parts = this.selectedValue.split('}');

        // can be '(none)'
        if (parts.length > 1) {
            const namespace = parts[0].slice(1);
            const name = parts[1];
            this.openSuperClassLink = '/' + this.toscaType + '/' + encodeURIComponent(namespace) + '/' + name;
        }
    }
}
