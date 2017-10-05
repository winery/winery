/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, EventEmitter, forwardRef, Input, OnInit, Output } from '@angular/core';
import { WineryNamespaceSelectorService } from './wineryNamespaceSelector.service';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { NamespaceWithPrefix } from '../wineryInterfaces/namespaceWithPrefix';

const noop = () => {
};

const customInputControl: any = {
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => WineryNamespaceSelectorComponent),
    multi: true,
};

/**
 * This component only wraps the namespace chooser. It gets the whole list from the backend and
 * provides typeahead for selecting a namespace. This component can be used with the <code>ngModel</code> directive.
 *
 * <label>Inputs</label>
 * <ul>
 *     <li><code>ngModel</code> required for getting the value from this input.
 *     </li>
 *     <li><code>isRequired</code> provides a way for setting the namespace input as required. By default,
 *         required is set to false.
 *     </li>
 *     <li><code>typeAheadListLimit</code> sets the length of the options which are shown by typeahead. By default,
 *         the limit is set to 50
 *     </li>
 * </ul>
 * <br>
 * @example <caption>Basic usage</caption>
 * ```html
 * <winery-namespace-selector name="namespaceSelector" [(ngModel)]="mySelectedNamespace">
 * </winery-namespace-selector>
 * ```
 *
 * @example <caption>Example with using the required validation</caption>
 * ```html
 * <form #myForm="ngForm">
 *     <winery-namespace-selector
 *         name="namespaceSelector"
 *         [(ngModel)]="mySelectedNamespace"
 *         [isRequired]="true"
 *         [typeAheadListLimit]="20"
 *         required>
 *     </winery-namespace-selector>
 *     <button type="button" [disabled]="!myForm?.form.valid" (click)="onSave();">Save</button>
 * </form>
 * ```
 */
@Component({
    selector: 'winery-namespace-selector',
    templateUrl: './wineryNamespaceSelector.component.html',
    providers: [
        WineryNamespaceSelectorService,
        customInputControl
    ]
})
export class WineryNamespaceSelectorComponent implements OnInit, ControlValueAccessor {

    @Input() isRequired = false;
    @Input() typeAheadListLimit = 50;
    @Output() onChange = new EventEmitter<any>();

    loading = true;
    allNamespaces: NamespaceWithPrefix[] = [];

    private innerValue = '';
    private onTouchedCallback: () => void = noop;
    private onChangeCallback: (_: any) => void = noop;

    constructor(private service: WineryNamespaceSelectorService, private notify: WineryNotificationService) {
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
            this.onChange.emit();
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
