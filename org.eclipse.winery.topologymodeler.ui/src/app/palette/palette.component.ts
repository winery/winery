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

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';
import { WineryActions } from '../redux/actions/winery.actions';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TNodeTemplate } from '../models/ttopology-template';
import { BackendService } from '../services/backend.service';
import { NewNodeIdTypeColorPropertiesModel } from '../models/newNodeIdTypeColorModel';
import { isNullOrUndefined } from 'util';
import { Subscription } from 'rxjs/Subscription';

/**
 * This is the left sidebar, where nodes can be created from.
 */
@Component({
    selector: 'winery-palette-component',
    templateUrl: './palette.component.html',
    styleUrls: ['./palette.component.css'],
    providers: [],
    animations: [
        trigger('paletteItemState', [
            state('shrunk', style({
                display: 'none',
                opacity: '0',
                height: '*',
            })),
            state('extended', style({
                display: 'block',
                opacity: '1',
                height: '*',
            })),
            transition('shrunk => extended', animate('500ms ease-out')),
            transition('extended => shrunk', animate('500ms ease-out'))
        ]),
        trigger('paletteButtonState', [
            state('left', style({
                display: 'block',
                opacity: '1',
                height: '*',
                transform: 'rotate(-90deg) translateY(-135px) translateX(-135px)'
            })),
            state('top', style({
                display: 'block',
                opacity: '1',
                height: '*',
                transform: 'rotate(0deg) translateY(0px) translateX(0px)'
            })),
            transition('left => top', animate('200ms ease-in')),
            transition('top => left', animate('200ms ease-in', keyframes([
                style({ opacity: '1', transform: 'rotate(0deg) translateY(0px) translateX(0px)' }),
                style({ opacity: '0', transform: 'rotate(-45deg) translateY(-75px) translateX(-75px)' }),
                style({ opacity: '1', transform: 'rotate(-90deg) translateY(-135px) translateX(-135px)' })
            ])))
        ])
    ]
})
export class PaletteComponent implements OnInit, OnDestroy {
    @Input() entityTypes;
    paletteRootState = 'extended';
    paletteButtonRootState = 'left';
    subscriptions: Array<Subscription> = [];
    public oneAtATime = true;
    // All Node Types grouped by their namespaces
    allNodeTemplates: TNodeTemplate[] = [];
    readonly newNodePositionOffsetX = 108;
    readonly newNodePositionOffsetY = 60;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                private backendService: BackendService) {
        this.subscriptions.push(ngRedux.select(wineryState => wineryState.wineryState.currentJsonTopology.nodeTemplates)
            .subscribe(currentNodes => this.updateNodes(currentNodes)));
        this.subscriptions.push(ngRedux.select(wineryState => wineryState.wineryState.currentPaletteOpenedState)
            .subscribe(currentPaletteOpened => this.updateState(currentPaletteOpened)));
    }

    /**
     * Applies the correct css, depending on if the palette is open or not.
     * @param newPaletteOpenedState
     */
    updateState(newPaletteOpenedState: any) {
        if (!newPaletteOpenedState) {
            this.paletteRootState = 'shrunk';
            this.paletteButtonRootState = 'left';

        } else {
            this.paletteRootState = 'extended';
            this.paletteButtonRootState = 'top';
        }
    }

    /**
     * Angular lifecycle event.
     */
    ngOnInit() {
        // console.log(this.entityTypes);
    }

    /**
     * opens the palette if its closed and vice versa.
     */
    public toggleRootState(): void {
        if (this.paletteRootState === 'shrunk') {
            this.ngRedux.dispatch(this.actions.sendPaletteOpened(true));

        } else {
            this.ngRedux.dispatch(this.actions.sendPaletteOpened(false));
        }
    }

    /**
     * Gets called if nodes get deleted or created and calls the
     * correct handler.
     * @param currentNodes  List of all displayed nodes.
     */
    updateNodes(currentNodes: Array<TNodeTemplate>): void {
        this.allNodeTemplates = currentNodes;
    }

    /**
     * Generates and stores a new node in the store.
     * @param $event
     */
    generateNewNode($event): void {
        const left = ($event.pageX - this.newNodePositionOffsetX).toString();
        const top = ($event.pageY - this.newNodePositionOffsetY).toString();
        const name = $event.target.innerText;
        const y = top;
        const x = left;
        const newIdTypeColorProperties = this.generateIdTypeColorProperties(name);
        const newNode: TNodeTemplate = new TNodeTemplate(
            newIdTypeColorProperties.properties,
            newIdTypeColorProperties.id,
            newIdTypeColorProperties.type,
            name,
            1,
            1,
            newIdTypeColorProperties.color,
            '',
            [],
            [],
            {},
            x,
            y,
            {},
            {},
            {},
            {}
        );
        this.ngRedux.dispatch(this.actions.saveNodeTemplate(newNode));
    }

    /**
     * Generates a new unique node id, type, color and properties
     * @param name
     * @return result
     */
    generateIdTypeColorProperties(name: string): NewNodeIdTypeColorPropertiesModel {
        if (this.allNodeTemplates.length > 0) {
            // iterate from back to front because only the last added instance of a node type is important
            // e.g. Node_8 so to increase to Node_9 only the 8 is important which is in the end of the array
            for (let i = this.allNodeTemplates.length - 1; i >= 0; i--) {
                // get type of node Template
                const type = this.allNodeTemplates[i].type;
                const color = this.allNodeTemplates[i].color;
                // split it to get a string like "NodeTypeWithTwoProperties"
                let typeOfCurrentNode = type.split('}').pop();
                // eliminate whitespaces from both strings, important for string comparison
                typeOfCurrentNode = typeOfCurrentNode.replace(/\s+/g, '');
                name = name.replace(/\s+/g, '');
                if (name === typeOfCurrentNode) {
                    const idOfCurrentNode = this.allNodeTemplates[i].id;
                    const numberOfNewInstance = parseInt(idOfCurrentNode.substring(name.length + 1), 10) + 1;
                    let newId;
                    if (numberOfNewInstance) {

                        newId = name.concat('_', numberOfNewInstance.toString());
                    } else {
                        newId = name.concat('_', '2');
                    }
                    const result = {
                        id: newId,
                        type: type,
                        properties: this.getDefaultPropertiesFromNodeTypes(name),
                        color: color,
                    };
                    return result;
                }
            }
            return this.getNewNodeDataFromNodeTypes(name);
        } else {
            return this.getNewNodeDataFromNodeTypes(name);
        }
    }

    /**
     * This function transforms the node's KV properties from an array to an object representation
     * @param any type : the element type, e.g. capabilityType, requirementType etc.
     * @return newKvProperties : properties as a object
     */
    setKVProperties(type: any): any {
        let newKVProperies;
        const kvProperties = type.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].any[0].propertyDefinitionKVList;
        for (const obj of kvProperties) {
            const key = obj.key;
            let value;
            if (isNullOrUndefined(obj.value)) {
                value = '';
            } else {
                value = obj.value;
            }
            const keyValuePair = {
                [key]: value
            };
            newKVProperies = { ...newKVProperies, ...keyValuePair };
        }
        return newKVProperies;
    }

    /**
     * Generates node id, type, color and properties from the node types
     * @param name
     * @return result
     */
    private getNewNodeDataFromNodeTypes(name: string): any {
        // case that the node name is not in the array which contains a local copy of all node templates visible in the
        // DOM, then search in ungroupedNodeTypes where all possible node information is available
        for (const node of this.entityTypes.unGroupedNodeTypes) {
            if (node.id === name) {
                const result = {
                    id: node.id,
                    type: node.qName,
                    properties: this.getDefaultPropertiesFromNodeTypes(name),
                    color: node.color,
                };
                return result;
            }
        }
    }

    /**
     * Generates default properties from node types
     * @param name
     * @return result
     */
    private getDefaultPropertiesFromNodeTypes(name: string): any {
        for (const nodeType of this.entityTypes.unGroupedNodeTypes) {
            if (nodeType.name === name) {
                // if any is defined with at least one element it's a KV property, sets default values if there aren't
                // any in the node template
                if (nodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].any) {
                    if (nodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].any.length > 0) {
                        const properties = {
                            kvproperties: this.setKVProperties(nodeType)
                        };
                        return properties;
                    }
                    // if propertiesDefinition is defined it's a XML property
                } else if (nodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition) {
                    if (nodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element) {
                        const properties = {
                            any: nodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element
                        };
                        return properties;
                    }
                } else {
                    // else no properties
                    return null;
                }
            }
        }
    }

    /**
     * Angular lifecycle event.
     */
    ngOnDestroy() {
        this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }
}


