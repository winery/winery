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

import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewContainerRef } from '@angular/core';
import { WineryAlertService } from '../winery-alert/winery-alert.service';
import { DifferenceStates, ToscaDiff } from '../models/ToscaDiff';
import { TNodeTemplate, TRelationshipTemplate, TTopologyTemplate } from '../models/ttopology-template';
import { isNullOrUndefined } from 'util';
import { NgRedux } from '@angular-redux/store';
import { WineryActions } from '../redux/actions/winery.actions';
import { IWineryState } from '../redux/store/winery.store';
import { ILoaded } from '../services/loaded.service';
import { Subscription } from 'rxjs/Subscription';
import { Utils } from '../models/utils';

/**
 * This is the parent component of the canvas and navbar component.
 */
@Component({
    selector: 'winery-topology-renderer',
    templateUrl: './topology-renderer.component.html',
    styleUrls: ['./topology-renderer.component.css']
})
export class TopologyRendererComponent implements OnInit, OnDestroy {

    @Input() entityTypes: any;
    @Input() relationshipTypes: Array<any> = [];
    @Input() differencesData: [ToscaDiff, TTopologyTemplate];
    @Input() nodeTemplates: Array<TNodeTemplate>;
    @Input() relationshipTemplates: Array<TRelationshipTemplate>;
    @Output() generatedReduxState = new EventEmitter();
    hideNavBarState: boolean;
    subscriptions: Array<Subscription> = [];

    private topologyDiff: ToscaDiff;
    private oldTopology: TTopologyTemplate;

    loader: ILoaded;
    diffMode: boolean;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                vcr: ViewContainerRef,
                private notify: WineryAlertService) {
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.hideNavBarAndPaletteState)
            .subscribe(hideNavBar => this.hideNavBarState = hideNavBar));
        this.notify.init(vcr);
    }

    ngOnInit() {
        this.loader = { loadedData: true, generatedReduxState: false };
        if (!isNullOrUndefined(this.differencesData)) {
            this.diffMode = true;
            this.topologyDiff = this.differencesData[0];
            this.oldTopology = this.differencesData[1];

            if (!isNullOrUndefined(this.topologyDiff.children)) {
                this.topologyDiff = this.topologyDiff.children.find(value => value.element === 'topologyTemplate');
                if (isNullOrUndefined(this.topologyDiff) || isNullOrUndefined(this.topologyDiff.children)) {
                    this.notify.info('No differences in the topology!');
                } else {
                    this.generateDiffTopology();
                }
            } else {
                this.notify.info('There are no differences between those definitions!');
            }
        } else {
            this.addElementsToRedux();
        }
    }

    generateDiffTopology() {
        // add all removed elements and color code them red
        // collect all added elements and color code them green
        // collect all changed elements and color code them yellow
        // also add a corresponding annotation for a better differentiation

        const changedNodes = this.topologyDiff.children.find(value => value.element === 'nodeTemplates');
        const changedRelationships = this.topologyDiff.children.find(value => value.element === 'relationshipTemplates');

        if (!isNullOrUndefined(changedNodes)) {
            changedNodes.children.forEach((node: ToscaDiff) => {
                let current = this.nodeTemplates.find(item => item.id === node.element);

                if (node.state === DifferenceStates.REMOVED) {
                    current = this.oldTopology.nodeTemplates.find(item => item.id === node.element);
                    current = Utils.createTNodeTemplateFromObject(current, this.entityTypes.nodeVisuals, node.state);
                    this.nodeTemplates.push(current);
                } else {
                    current.state = node.state;
                }
            });
        }

        if (!isNullOrUndefined(changedRelationships)) {
            changedRelationships.children.forEach((relationship: ToscaDiff) => {
                let current;

                if (relationship.state === DifferenceStates.REMOVED) {
                    current = this.oldTopology.relationshipTemplates.find(item => item.id === relationship.element);
                    this.relationshipTemplates.push(current);
                } else {
                    current = this.relationshipTemplates.find(item => item.id === relationship.element);
                }

                current.state = relationship.state;
            });
        }

        this.addElementsToRedux();
    }

    private addElementsToRedux() {
        this.relationshipTemplates.forEach(relationshipTemplate => {
            this.ngRedux.dispatch(this.actions.saveRelationship(relationshipTemplate));
        });
        this.nodeTemplates.forEach(nodeTemplate => {
            this.ngRedux.dispatch(this.actions.saveNodeTemplate(nodeTemplate));
        });

        this.loader.generatedReduxState = true;
        this.generatedReduxState.emit(this.loader);
    }

    /**
     * Lifecycle event
     */
    ngOnDestroy() {
        this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }
}
