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

import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { WineryActions } from '../redux/actions/winery.actions';
import { JsPlumbService } from '../services/jsPlumb.service';
import { PropertyDefinitionType } from '../models/enums';
import { KeyValueItem } from '../../../../tosca-management/src/app/model/keyValueItem';

@Component({
    selector: 'winery-properties',
    templateUrl: './properties.component.html',
    styleUrls: ['./properties.component.css']
})
export class PropertiesComponent implements OnInit, OnChanges, OnDestroy {

    @Input() readonly: boolean;
    @Input() currentNodeData: any;

    properties: Subject<string> = new Subject<string>();
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
                } else if (this.currentNodeData.propertyDefinitionType === PropertyDefinitionType.YAML) {
                    // FIXME this is not really useful, actually
                    this.nodeProperties = currentProperties.kvproperties;
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
                } else if (this.currentNodeData.propertyDefinitionType === PropertyDefinitionType.YAML) {
                    // FIXME this is not really useful, actually
                    this.nodeProperties = currentProperties.kvproperties;
                }
            } catch (e) {
            }
        }
    }

    ngOnDestroy() {
        this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }

    xmlPropertyEdit($event: string) {
        this.nodeProperties = $event;
        this.dispatchRedux();
    }

    kvPropertyEdit($event: KeyValueItem) {
        this.nodeProperties[$event.key] = $event.value;
        this.dispatchRedux();
    }

    yamlPropertyEdit($event: KeyValueItem) {
        // FIXME deal with the fact that yaml properties support complex datatypes, implying nesting
        this.nodeProperties[$event.key] = $event.value;
        this.dispatchRedux();
    }

    private dispatchRedux(): void {
        this.$ngRedux.dispatch(this.actions.setProperty({
            nodeProperty: {
                newProperty: this.nodeProperties,
                propertyType: this.currentNodeData.propertyDefinitionType,
                nodeId: this.currentNodeData.nodeTemplate.id
            }
        }));
    }

}
