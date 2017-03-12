/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */

import { Component, OnInit, Input, forwardRef } from '@angular/core';
import { NamespaceSelectorService } from './namespaceSelector.service';
import { NotificationService } from '../notificationModule/notificationservice';
import { NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';

const noop = () => {
};

const customInputControl: any = {
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => NamespaceSelectorComponent),
    multi: true,
};

/**
 * This component only wraps the namespace chooser. It gets the whole list from the backend and
 * provides typeahead for selecting a namespace. This component can be used with the <code>ngModel</code> directive.
 * <p>
 *     It also provides a way for setting the namespace required using the <code>isRequired</code> input. By default,
 *     required is set to false.
 * </p>
 *
 * @example <caption>Basic usage</caption>
 * ```html
 * <winery-namespaceSelector
 *     [(ngModel)]="mySelectedNamespace"
 *     [isRequired]="false">
 * </winery-namespaceSelector>
 * ```
 *
 * @example <caption>Example with using the required validation</caption>
 * ```html
 * <form #myForm="ngForm">
 *     <winery-namespaceSelector
 *         name="namespaceSelector"
 *         [(ngModel)]="mySelectedNamespace"
 *         [isRequired]="true">
 *     </winery-namespaceSelector>
 *     <button type="button" [disabled]="!myForm?.form.valid" (click)="onSave();">Save</button>
 * </form>
 * ```
 */
@Component({
    selector: 'winery-namespaceSelector',
    templateUrl: 'namespaceSelector.component.html',
    providers: [
        NamespaceSelectorService,
        customInputControl
    ]
})
export class NamespaceSelectorComponent implements OnInit, ControlValueAccessor {

    @Input() isRequired: boolean = false;

    loading: boolean = true;
    allNamespaces: string[] = [];

    private innerValue: string = '';
    private onTouchedCallback: () => void = noop;
    private onChangeCallback: (_: any) => void = noop;

    constructor(private service: NamespaceSelectorService, private notify: NotificationService) {
    }

    ngOnInit() {
        this.service.getAllNamespaces()
            .subscribe(
                data => {
                    this.allNamespaces = data;
                    this.loading = false;
                },
                error => this.notify.error(error.toString())
            );
    }

    get selectedNamespace(): string {
        return this.innerValue;
    }

    set selectedNamespace(v: string) {
        if (v !== this.innerValue) {
            this.innerValue = v;
            this.onChangeCallback(v);
        }
    }

    writeValue(value: string) {
        if (value !== this.innerValue) {
            this.innerValue = value;
        }
    }

    registerOnChange(fn: any) {
        this.onChangeCallback = fn;
    }

    registerOnTouched(fn: any) {
        this.onTouchedCallback = fn;
    }
}
