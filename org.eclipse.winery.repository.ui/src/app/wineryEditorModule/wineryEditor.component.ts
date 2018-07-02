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
import { Component, forwardRef, Input, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { isNullOrUndefined } from 'util';
import { EditorBuilderService } from './editor-builder.service';

const noop = () => {
};

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
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => WineryEditorComponent),
            multi: true
        }
    ]
})
export class WineryEditorComponent implements ControlValueAccessor, OnInit {

    @Input() dataEditorLang = 'application/xml';
    @Input() height = 500;

    loading = true;
    editorViewer: any;

    // The internal data model
    private innerValue: any = '';

    // Placeholders for the callbacks which are later provided
    // by the Control Value Accessor
    private onTouchedCallback: () => void = noop;
    private onChangeCallback: (_: any) => void = noop;

    constructor(private editorBuilder: EditorBuilderService) {
    }

    ngOnInit() {

        const codeEdit = this.editorBuilder.createEditor('embeddedEditor');

        codeEdit.then((editorViewer: any) => {
            this.editorViewer = editorViewer;
            if (this.editorViewer.settings) {
                this.editorViewer.settings.contentAssistAutoTrigger = true;
                this.editorViewer.settings.showOccurrences = true;
            }
            this.editorViewer.setContents(this.innerValue, this.dataEditorLang);
        });
    }

    // get accessor
    get value(): any {
        return this.innerValue;
    }

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
            if (!isNullOrUndefined(this.editorViewer)) {
                this.editorViewer.setContents(this.innerValue, this.dataEditorLang);
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
        if (!isNullOrUndefined(this.editorViewer)) {
            const textModel = this.editorViewer.editor.getModel();
            return textModel.getText();
        }
    }

    setData(value: string) {
        if (!isNullOrUndefined(this.editorViewer)) {
            this.editorViewer.setContents(this.innerValue, this.dataEditorLang);
        }
    }

}
