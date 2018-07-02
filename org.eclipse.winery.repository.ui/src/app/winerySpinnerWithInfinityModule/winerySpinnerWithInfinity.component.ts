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
import { Component, forwardRef, Input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

const noop = () => {
};

/**
 * This component provides a number input field with additional up and down arrow buttons
 * as well as an optional infinity symbol button
 *
 * <label>Inputs</label>
 * <ul>
 *     <li><code>min</code> minimum value for the input field</li>
 *     <li><code>max</code> maximum value for the input field</li>
 *     <li><code>label</code> label of the input field, which is in front of the input</li>
 *     <li><code>valueSt</code> defines the internal value of the infinity value</li>
 *     <li><code>withInfinity</code> defines whether to show the infinity button or not</li>
 * </ul>
 *
 * @example <caption>Basic usage</caption>
 * ```html
 *   <winery-spinner-with-infinity
 *       #upperBoundSpinner label="Upper Bound" withInfinity="true">
 *   </winery-spinner-with-infinity>
 * ```
 */
@Component({
    selector: 'winery-spinner-with-infinity',
    templateUrl: 'winerySpinnerWithInfinity.component.html',
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => SpinnerWithInfinityComponent),
            multi: true
        }
    ]
})

export class SpinnerWithInfinityComponent implements ControlValueAccessor {

    @Input() min = 0;
    @Input() max = 1000;
    @Input() label = 'Default Label';
    @Input() valueSt = 42;
    @Input() withInfinity = false;

    private infinityIsPlaced = false;

    // The internal data model
    private innerValue: any = '1';

    // Placeholders for the callbacks which are later provided
    // by the Control Value Accessor
    private onTouchedCallback: () => void = noop;
    private onChangeCallback: (_: any) => void = noop;

    // get accessor
    get value(): any {
        return this.innerValue;
    }

    // set accessor including call the onchange callback
    set value(v: any) {
        if (v !== this.innerValue) {
            this.innerValue = v;

            // return unbounded in case of infinity symbol
            if (v !== '∞') {
                this.onChangeCallback(v);
            } else {
                this.onChangeCallback('unbounded');
            }
        }
    }

    // Set touched on blur
    onBlur() {
        this.onTouchedCallback();
    }

    // From ControlValueAccessor interface
    writeValue(value: any) {
        if (value !== this.innerValue) {
            // show infinity symbol in case of unbounded is set
            if (value === 'unbounded') {
                this.innerValue = '∞';
                this.infinityIsPlaced = true;
            } else {
                this.innerValue = value;
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

    addInfinite(): void {
        this.value = '∞';
        this.infinityIsPlaced = true;
    }

    increment(): void {
        let modelAsNumber: number;
        if (this.infinityIsPlaced) {
            modelAsNumber = this.valueSt;
            this.infinityIsPlaced = false;
        } else {
            modelAsNumber = Number(this.innerValue);
        }
        modelAsNumber++;

        if (modelAsNumber <= this.max) {
            this.value = modelAsNumber.toString();
        }
    }

    decrement(): void {
        let modelAsNumber: number;
        if (this.infinityIsPlaced) {
            modelAsNumber = (this.valueSt + 1);
            this.infinityIsPlaced = false;
        } else {
            modelAsNumber = Number(this.innerValue);
        }
        modelAsNumber--;

        if (modelAsNumber >= this.min) {
            this.value = modelAsNumber.toString();
        }
    }

}
