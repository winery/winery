/********************************************************************************
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
 ********************************************************************************/

import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { WineryActions } from '../../redux/actions/winery.actions';
import { JsPlumbService } from '../../services/jsPlumb.service';
import { PropertyDefinitionType } from '../../models/enums';

@Component({
    selector: 'winery-properties-content',
    templateUrl: './properties-content.component.html',
    styleUrls: ['./properties-content.component.css']
})
export class PropertiesContentComponent implements OnInit, OnChanges, OnDestroy {

    properties: Subject<string> = new Subject<string>();
    keyOfEditedKVProperty: Subject<string> = new Subject<string>();
    @Input() readonly: boolean;
    @Input() currentNodeData: any;
    key: string;
    nodeProperties: any;
    subscriptions: Array<Subscription> = [];

    constructor(private $ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                private jsPlumbService: JsPlumbService) {
    }

    /**
     * Angular lifecycle event.
     */
    ngOnChanges(changes: SimpleChanges) {
        if (changes.currentNodeData.currentValue.nodeTemplate.properties) {
            try {
                const currentProperties = changes.currentNodeData.currentValue.nodeTemplate.properties;
                if (this.currentNodeData.propertyDefinitionType === PropertyDefinitionType.KV) {
                    this.nodeProperties = currentProperties.kvproperties;
                } else if (this.currentNodeData.propertyDefinitionType === PropertyDefinitionType.XML) {
                    this.nodeProperties = currentProperties.any;
                }
            } catch (e) {
            }
        }
        // repaint jsPlumb to account for height change of the accordion
        setTimeout(() => this.jsPlumbService.getJsPlumbInstance().repaintEverything(), 1);
    }

    /**
     * Angular lifecycle event.
     */
    ngOnInit() {
        if (this.currentNodeData.nodeTemplate.properties) {
            try {
                const currentProperties = this.currentNodeData.nodeTemplate.properties;
                if (this.currentNodeData.propertyDefinitionType === PropertyDefinitionType.KV) {
                    this.nodeProperties = currentProperties.kvproperties;
                } else if (this.currentNodeData.propertyDefinitionType === PropertyDefinitionType.XML) {
                    this.nodeProperties = currentProperties.any;
                }
            } catch (e) {
            }
        }
        // find out which row was edited by key
        this.subscriptions.push(this.keyOfEditedKVProperty.pipe(
            debounceTime(200),
            distinctUntilChanged(), )
            .subscribe(key => {
                this.key = key;
            }));
        // set key value property with a debounceTime of 300ms
        this.subscriptions.push(this.properties.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(value => {
                if (this.currentNodeData.propertyDefinitionType === PropertyDefinitionType.KV) {
                    this.nodeProperties[this.key] = value;
                } else {
                    this.nodeProperties = value;
                }
                this.$ngRedux.dispatch(this.actions.setProperty({
                    nodeProperty: {
                        newProperty: this.nodeProperties,
                        propertyType: this.currentNodeData.propertyDefinitionType,
                        nodeId: this.currentNodeData.nodeTemplate.id
                    }
                }));
            }));
    }

    ngOnDestroy() {
        this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }
}
