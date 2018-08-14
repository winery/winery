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

import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { EntityTypesModel } from '../../models/entityTypesModel';
import { TNodeTemplate } from '../../models/ttopology-template';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { Subscription } from 'rxjs';
import { CapabilityModel } from '../../models/capabilityModel';

@Component({
    selector: 'winery-capabilities',
    templateUrl: './capabilities.component.html',
    styleUrls: ['./capabilities.component.css']
})
/**
 * This Handles Information about the node capabilities
 */
export class CapabilitiesComponent implements OnInit, OnChanges, OnDestroy {

    @Output() toggleModalHandler: EventEmitter<any>;
    @Input() readonly: boolean;
    @Input() currentNodeData: any;

    capabilities: any[] = [];
    capabilitiesExist: boolean;
    entityTypes: EntityTypesModel;
    nodeTemplate: TNodeTemplate;
    subscription: Subscription;
    currentCapability: CapabilityModel;

    constructor(private ngRedux: NgRedux<IWineryState>) {
        this.toggleModalHandler = new EventEmitter();
        this.subscription = this.ngRedux.select(state => state.wineryState.currentJsonTopology.nodeTemplates)
            .subscribe(currentNodes => this.updateCaps());
    }

    /**
     * Gets called if nodes representation in the store changes
     */
    updateCaps(): void {
        if (this.currentNodeData) {
            if (this.currentNodeData.nodeTemplate.capabilities) {
                this.capabilities = this.currentNodeData.nodeTemplate.capabilities.capability;
                this.capabilitiesExist = true;
            }
        }
    }

    /**
     * Angular lifecycle event.
     */
    ngOnChanges(changes: SimpleChanges) {
        if (changes.currentNodeData.currentValue.entityTypes) {
            this.entityTypes = changes.currentNodeData.currentValue.entityTypes;
            this.nodeTemplate = changes.currentNodeData.currentValue.nodeTemplate;
        }
    }

    /**
     * Propagates the click event to node.component, where the capabilities modal gets opened.
     * @param $event
     */
    public toggleModal($event) {
        this.currentCapability = null;
        const currentCapId = $event.srcElement.textContent;

        if (this.capabilities) {
            this.capabilities.some(cap => {
                if (cap.id === currentCapId) {
                    this.currentCapability = cap;
                    return true;
                }
            });
        } else {
            this.capabilities = [];
        }

        if ($event.srcElement.innerText === 'Add new') {
            this.currentNodeData.currentCapability = null;
        } else {
            this.currentNodeData.currentCapability = this.currentCapability;
        }

        this.toggleModalHandler.emit(this.currentNodeData);
    }

    ngOnInit() {
    }

    /**
     * Lifecycle event
     */
    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}
