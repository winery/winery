/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { WineryActions } from '../../redux/actions/winery.actions';
import { Subscription } from 'rxjs/Subscription';
import { isNullOrUndefined } from 'util';

@Component({
    selector: 'winery-properties-content',
    templateUrl: './properties-content.component.html',
    styleUrls: ['./properties-content.component.css']
})
export class PropertiesContentComponent implements OnInit, OnChanges, OnDestroy {

    properties: Subject<string> = new Subject<string>();
    keyOfEditedKVProperty: Subject<string> = new Subject<string>();
    propertyDefinitionType: string;
    @Input() currentNodeData: any;
    key: string;
    nodeProperties: any;
    subscriptions: Array<Subscription> = [];

    constructor(private $ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions) {
    }

    /**
     * Angular lifecycle event.
     */
    ngOnChanges(changes: SimpleChanges) {
        setTimeout(() => {
            if (this.currentNodeData.currentNodePart === 'PROPERTIES') {
                if (changes.currentNodeData.currentValue.nodeTemplate.properties) {
                    try {
                        const currentProperties = changes.currentNodeData.currentValue.nodeTemplate.properties;
                        if (this.propertyDefinitionType === 'KV') {
                            this.nodeProperties = currentProperties.kvproperties;
                        } else if (this.propertyDefinitionType === 'XML') {
                            this.nodeProperties = currentProperties.any;
                        }
                    } catch (e) {
                    }
                }
            }
        }, 1);
    }

    /**
     * Angular lifecycle event.
     */
    ngOnInit() {
        // find out which type of properties shall be displayed
        if (this.currentNodeData.currentNodePart === 'PROPERTIES') {
            this.findOutPropertyDefinitionTypeForProperties(this.currentNodeData.nodeTemplate.type);
        }

        // find out which row was edited by key
        this.subscriptions.push(this.keyOfEditedKVProperty
            .debounceTime(200)
            .distinctUntilChanged()
            .subscribe(key => {
                this.key = key;
            }));
        // set key value property with a debounceTime of 300ms
        this.subscriptions.push(this.properties
            .debounceTime(300)
            .distinctUntilChanged()
            .subscribe(value => {
                if (this.propertyDefinitionType === 'KV') {
                    this.nodeProperties[this.key] = value;
                } else {
                    this.nodeProperties = value;
                }
                switch (this.currentNodeData.currentNodePart) {
                    case 'PROPERTIES':
                        this.$ngRedux.dispatch(this.actions.setProperty({
                            nodeProperty: {
                                newProperty: this.nodeProperties,
                                propertyType: this.propertyDefinitionType,
                                nodeId: this.currentNodeData.nodeTemplate.id
                            }
                        }));
                        break;
                }
            }));
    }

    /**
     * This function determines which kind of properties the nodeType embodies.
     * We have 3 possibilities: none, XML element, or Key value pairs.
     * @param {string} type
     */
    findOutPropertyDefinitionTypeForProperties(type: string): void {
        if (this.currentNodeData.entityTypes.groupedNodeTypes) {
            for (const nameSpace of this.currentNodeData.entityTypes.groupedNodeTypes) {
                for (const nodeTypeVar of nameSpace.children) {
                    if (nodeTypeVar.id === type) {
                        // if PropertiesDefinition doesn't exist then it must be of type NONE
                        if (isNullOrUndefined(nodeTypeVar.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition)) {
                            this.propertyDefinitionType = 'NONE';
                        } else {
                            // if no XML element inside PropertiesDefinition then it must be of type Key Value
                            if (!nodeTypeVar.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element) {
                                this.propertyDefinitionType = 'KV';
                            } else {
                                // else we have XML
                                this.propertyDefinitionType = 'XML';
                            }
                        }
                    }
                }
            }
        }
    }

    ngOnDestroy() {
        this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }
}
