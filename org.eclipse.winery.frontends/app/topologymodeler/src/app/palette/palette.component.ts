/********************************************************************************
 * Copyright (c) 2017-2021 Contributors to the Eclipse Foundation
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

import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';
import { WineryActions } from '../redux/actions/winery.actions';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TNodeTemplate } from '../models/ttopology-template';
import { NewNodeIdTypeColorPropertiesModel } from '../models/newNodeIdTypeColorModel';
import { Subscription } from 'rxjs';
import { TopologyTemplateUtil } from '../models/topologyTemplateUtil';
import { EntityTypesModel } from '../models/entityTypesModel';
import { NodeTypeModel } from '../models/groupedNodeTypeModel';
import { Visuals } from '../models/visuals';
import { BackendService } from '../services/backend.service';
import { ManageTopologyService } from '../services/manage-topology.service';
import { InheritanceUtils } from '../models/InheritanceUtils';

/**
 * This is the left sidebar, where nodes can be created from.
 */
@Component({
    selector: 'winery-palette-component',
    templateUrl: './palette.component.html',
    styleUrls: ['./palette.component.scss'],
    providers: [],
    animations: [
        trigger('paletteItemState', [
            state('shrunk', style({
                display: 'none',
                opacity: '0',
            })),
            state('extended', style({
                display: 'block',
                opacity: '1',
            })),
            transition('shrunk => extended', animate('500ms ease-out')),
            transition('extended => shrunk', animate('50ms ease-out'))
        ]),
        trigger('paletteButtonState', [
            state('left', style({
                transform: 'rotate(-90deg) translateY(-135px) translateX(-135px)'
            })),
            state('top', style({
                transform: 'rotate(0deg) translateY(0px) translateX(0px)'
            })),
            transition('left => top', animate('50ms ease-in')),
            transition('top => left', animate('50ms ease-in', keyframes([
                style({ opacity: '1', transform: 'rotate(0deg) translateY(0px) translateX(0px)' }),
                style({ opacity: '0', transform: 'rotate(-45deg) translateY(-75px) translateX(-75px)' }),
                style({ opacity: '1', transform: 'rotate(-90deg) translateY(-135px) translateX(-135px)' })
            ])))
        ])
    ]
})
export class PaletteComponent implements OnInit, OnDestroy {

    @Input() entityTypes: EntityTypesModel;
    @Input() top: number;

    paletteRootState = 'extended';
    paletteButtonRootState = 'left';
    subscriptions: Array<Subscription> = [];
    public oneAtATime = true;
    // All Node Types grouped by their namespaces
    allNodeTemplates: TNodeTemplate[] = [];
    readonly newNodePositionOffsetX = 108;
    readonly newNodePositionOffsetY = 30;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private backendService: BackendService,
                private actions: WineryActions,
                private generateTopologyService: ManageTopologyService) {
        this.subscriptions.push(ngRedux.select(wineryState => wineryState.wineryState.currentJsonTopology.nodeTemplates)
            .subscribe(currentNodes => this.updateNodes(currentNodes)));
        this.subscriptions.push(ngRedux.select(wineryState => wineryState.wineryState.currentPaletteOpenedState)
            .subscribe(currentPaletteOpened => this.updateState(currentPaletteOpened)));
    }

    ngOnInit(): void {
        this.generateTopologyService.subscribePalette(this);
    }

    /**
     * Applies the correct animations, depending on if the palette is open or not.
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
     * @param child
     */
    generateNewNodeFromMouseEvent($event: MouseEvent, child: NodeTypeModel): void {
        const x = $event.pageX - this.newNodePositionOffsetX;
        const y = $event.pageY - this.newNodePositionOffsetY;

        this.generateNewNode(x, y, child);
    }

    generateNewNode(x: number, y: number, child: any): TNodeTemplate {
        const newIdTypeColorProperties = this.generateIdTypeAndProperties(child);
        const nodeVisuals: Visuals = TopologyTemplateUtil.getNodeVisualsForNodeTemplate(newIdTypeColorProperties.type, this.entityTypes.nodeVisuals);
        const newNode: TNodeTemplate = new TNodeTemplate(
            newIdTypeColorProperties.properties,
            newIdTypeColorProperties.id,
            newIdTypeColorProperties.type,
            this.removeVersionIdentifier(child.text),
            1,
            1,
            nodeVisuals,
            [],
            [],
            {},
            x,
            y,
            null,
            null,
            null,
            null
        );
        this.ngRedux.dispatch(this.actions.saveNodeTemplate(newNode));
        return newNode;
    }

    /**
     * strips versioning substring
     * @param name
     * @return result
     */
    removeVersionIdentifier(name: string): string {
        return name.replace(/_([a-zA-Z0-9\.\-]*)(-w[0-9]+)(-wip[0-9]+)?/g, '');
    }

    /**
     * Generates a new unique node id, type, color and properties
     * @param nodeType
     * @return result
     */
    generateIdTypeAndProperties(nodeType: NodeTypeModel): NewNodeIdTypeColorPropertiesModel {
        let idNumber = 0, newId = '';
        do {
            newId = `${this.backendService.configuration.idPrefix}${nodeType.text}_${idNumber++}`;
        } while (this.allNodeTemplates && this.allNodeTemplates.find(element => element.id === newId));

        return {
            id: newId,
            type: nodeType.id,
            properties: InheritanceUtils.getDefaultPropertiesFromEntityTypes(nodeType.id, this.entityTypes.unGroupedNodeTypes)
        };
    }

    /**
     * Angular lifecycle event.
     */
    ngOnDestroy() {
        this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }

    getImageUrl(child: NodeTypeModel): string {
        const visuals = TopologyTemplateUtil.getNodeVisualsForNodeTemplate(child.id,
            this.entityTypes.nodeVisuals);

        // if the node doesn't have a picture the URL is "null"
        if (visuals.imageUrl !== 'null') {
            return visuals.imageUrl;
        }
    }
}


