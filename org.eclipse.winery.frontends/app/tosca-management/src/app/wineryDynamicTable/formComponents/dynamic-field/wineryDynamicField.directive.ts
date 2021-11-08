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

import { ComponentFactoryResolver, ComponentRef, Directive, Input, OnInit, ViewContainerRef } from '@angular/core';
import { WineryDynamicTableMetadata } from '../../wineryDynamicTableMetadata';
import { DynamicCheckboxComponent } from '../dynamicCheckbox.component';
import { DynamicDropdownComponent } from '../dynamicDropdown.component';
import { DynamicTextComponent } from '../dynamicText.component';
import { DynamicFieldComponent } from './dynamicFieldComponent';
import { FormGroup } from '@angular/forms';
import { DynamicConstraintsComponent } from '../dynamicConstraints/dynamicConstraints.component';

const formComponents = {
    'checkbox': DynamicCheckboxComponent,
    'dropdown': DynamicDropdownComponent,
    'textbox': DynamicTextComponent,
    'constraints': DynamicConstraintsComponent
};

/**
 * This directive is used in {@Link WineryDynamicFormModalComponent} and dynamically creates
 * the components which contains the requested forms.
 * To add a new form component, add it to the <code>formComponents</code> object with the corresponding
 * key set inside the correlating metadata and the component to create. The key has to be the same
 * as <code>controlType</code> set inside the metadata class.
 */
@Directive({
    selector: '[wineryDynamicField]'
})
export class WineryDynamicFieldDirective implements OnInit {
    @Input() config: WineryDynamicTableMetadata;
    @Input() group: FormGroup;

    private component: ComponentRef<DynamicFieldComponent>;

    constructor(private resolver: ComponentFactoryResolver,
                private container: ViewContainerRef) {
    }

    ngOnInit(): void {
            const factory = this.resolver.resolveComponentFactory<DynamicFieldComponent>(formComponents[this.config.controlType]);
            this.component = this.container.createComponent<DynamicFieldComponent>(factory);
            this.component.instance.config = this.config;
            this.component.instance.group = this.group;
    }
}
