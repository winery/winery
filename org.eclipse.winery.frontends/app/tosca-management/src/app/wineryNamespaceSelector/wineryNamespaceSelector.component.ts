/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
import { Component, ElementRef, forwardRef, Input, OnInit, ViewChild } from '@angular/core';
import { WineryNamespaceSelectorService } from './wineryNamespaceSelector.service';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { NamespaceProperties } from '../model/namespaceProperties';
import { StartNamespaces, ToscaTypes } from '../model/enums';
import { isNullOrUndefined } from 'util';
import { HttpErrorResponse } from '@angular/common/http';
import { WineryRepositoryConfigurationService } from '../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';

const noop = () => {
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
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => WineryNamespaceSelectorComponent),
            multi: true,
        }
    ]
})
export class WineryNamespaceSelectorComponent implements OnInit, ControlValueAccessor {

    @Input() isRequired = false;
    @Input() typeAheadListLimit = 50;
    @Input() toscaType: ToscaTypes;
    @Input() useStartNamespace = true;

    loading = true;
    isCollapsed = true;
    allNamespaces: NamespaceProperties[] = [];

    @ViewChild('namespaceInput') namespaceInput: ElementRef;
    public initNamespaceString = '';

    private innerNamespaceValue = '';

    private onTouchedCallback: () => void = noop;
    private propagateChange: (_: any) => void = noop;

    constructor(private service: WineryNamespaceSelectorService,
                private notify: WineryNotificationService,
                private configuration: WineryRepositoryConfigurationService) {
    }

    ngOnInit() {
        this.getDefaultNamespace();
        this.service.getNamespaces()
            .subscribe(
                data => {
                    this.allNamespaces = data;
                    this.loading = false;
                },
                (error: HttpErrorResponse) => {
                    this.notify.error(error.message);
                    this.loading = false;
                }
            );
    }

    get namespaceValue(): string {
        return this.innerNamespaceValue;
    }

    set namespaceValue(value: string) {
        this.innerNamespaceValue = value;
        this.propagateChange(this.innerNamespaceValue);
        if (this.namespaceInput) {
            this.namespaceInput.nativeElement.focus();
        }
    }

    applyNamespace() {
        localStorage.setItem(StartNamespaces.LocalStorageEntry.toString(), this.initNamespaceString);
        this.namespaceValue = this.initNamespaceString !== '' ? this.applyToscaTypeToNamespace(this.initNamespaceString) : '';
    }

    // region ########## ControlValueAccessor Interface ##########
    writeValue(value: string) {
        if (value !== this.innerNamespaceValue) {
            if ((isNullOrUndefined(value) || value.length === 0) && this.useStartNamespace) {
                // In the case that the namespace is set from outside this component via ngModel, don't overwrite the value set by the parent component.
                // Otherwise, use the default namespace.
                if (this.innerNamespaceValue.length === 0) {
                    this.getDefaultNamespace();
                    this.namespaceValue = this.applyToscaTypeToNamespace(this.initNamespaceString);
                } else {
                    this.namespaceValue = this.innerNamespaceValue;
                }
            } else {
                this.namespaceValue = value;
            }
        }
    }

    registerOnChange(fn: any) {
        this.propagateChange = fn;
    }

    registerOnTouched(fn: any) {
        this.onTouchedCallback = fn;
    }

    collapsed(event: any): void {
    }

    expanded(event: any): void {
    }

    // endregion

    private applyToscaTypeToNamespace(namespaceStart: string) {
        if (this.configuration.isYaml()) {
            return namespaceStart.endsWith('.') ? namespaceStart + this.toscaType : namespaceStart + '.' + this.toscaType;
        }

        return namespaceStart.endsWith('/') ? namespaceStart + this.toscaType : namespaceStart + '/' + this.toscaType;
    }

    private getDefaultNamespace() {
        const defaultNamespace = this.configuration.isYaml() ?
            StartNamespaces.DefaultStartNamespaceYaml.toString() : StartNamespaces.DefaultStartNamespace.toString();
        const storageValue = localStorage.getItem(StartNamespaces.LocalStorageEntry.toString());
        this.initNamespaceString = isNullOrUndefined(storageValue) || storageValue.length === 0 ? defaultNamespace : storageValue;
    }
}
