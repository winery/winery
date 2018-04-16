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
import { TNodeTemplate, TRelationshipTemplate } from '../models/ttopology-template';
import { NgRedux } from '@angular-redux/store';
import { WineryActions } from '../redux/actions/winery.actions';
import { IWineryState } from '../redux/store/winery.store';
import { ILoaded } from '../services/loaded.service';
import { Subscription } from 'rxjs/Subscription';

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
    @Input() nodeTemplates: Array<TNodeTemplate>;
    @Input() relationshipTemplates: Array<TRelationshipTemplate>;
    @Output() generatedReduxState = new EventEmitter();
    hideNavBarState: boolean;
    subscriptions: Array<Subscription> = [];

    loader: ILoaded;
    showDiffLegend: boolean;

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
