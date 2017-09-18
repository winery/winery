/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Philipp Meyer - initial API and implementation
 */
import { Component, forwardRef, Input, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { isNullOrUndefined } from 'util';

const noop = () => {
};

export const CUSTOM_INPUT_CONTROL_VALUE_ACCESSOR: any = {
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => WineryEditorComponent),
    multi: true
};

declare var requirejs: any;

/**
 * This component provides an editor for editing and showing code with syntax highlight for different
 * kinds of languages like xml, javascript etc.
 * For more information look under <a href="https://wiki.eclipse.org/Orion">Orion</a>
 * <b>Important notes:</b> The model binding just works in one direction.
 * That means that if a model is bind to the component the current text is shown.
 * In order to get the current text of the editor component the getData() method of the component have to be called.
 *
 *
 * <label>Inputs</label>
 * <ul>
 *     <li><code>dataEditorLang</code> sets the language for the syntax highlight
 *     </li>
 *     <li><code>height</code> Sets the height of the editor
 * </ul>
 *
 *
 * @example <caption>Basic usage</caption>
 * ```html
 *  <winery-editor #editor
 *  [ngModel]="xmlData">
 *  </winery-editor>
 * ```
 */
@Component({
    selector: 'winery-editor',
    templateUrl: 'wineryEditor.component.html',
    providers: [CUSTOM_INPUT_CONTROL_VALUE_ACCESSOR]
})
export class WineryEditorComponent implements ControlValueAccessor, OnInit {

    @Input() dataEditorLang = 'application/xml';
    @Input() height = 500;

    loading = true;
    orionEditor: any = undefined;

    // The internal data model
    private innerValue: any = '';

    // Placeholders for the callbacks which are later provided
    // by the Control Value Accessor
    private onTouchedCallback: () => void = noop;
    private onChangeCallback: (_: any) => void = noop;

    constructor() {
    }

    ngOnInit() {
        requirejs(['orion/editor/edit'], function (edit: any) {
            this.orionEditor = edit({ className: 'editor', parent: 'xml', contentType: this.dataEditorLang })[0];
            this.orionEditor.setText(this.innerValue);
        }.bind(this));
    }

    // get accessor
    get value(): any {
        return this.innerValue;
    };

    // set accessor including call the onchange callback
    set value(v: any) {
        if (v !== this.innerValue) {
            this.innerValue = v;
            this.onChangeCallback(v);
        }
    }

    // Set touched on blur
    onBlur() {
        this.onTouchedCallback();
    }

    // From ControlValueAccessor interface
    writeValue(value: any) {
        if (value !== this.innerValue && !isNullOrUndefined(value)) {
            this.innerValue = value;
            if (!isNullOrUndefined(this.orionEditor)) {
                this.orionEditor.setText(this.innerValue);
            }
        }
    }

    // From ControlValueAccessor interface
    registerOnChange(fn: any) {
        this.onChangeCallback = fn;
    }

    // From ControlValueAccessor interface
    registerOnTouched(fn: any) {
        this.onTouchedCallback = fn;
    }

    getData() {
        if (!isNullOrUndefined(this.orionEditor)) {
            return this.orionEditor.getText();
        }
    }

    setData(value: string) {
        if (!isNullOrUndefined(this.orionEditor)) {
            return this.orionEditor.setText(value);
        }
    }

}
