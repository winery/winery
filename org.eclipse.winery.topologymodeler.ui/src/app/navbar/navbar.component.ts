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

import { Component, ElementRef, Input, OnDestroy, ViewChild } from '@angular/core';
import { animate, style, transition, trigger } from '@angular/animations';
import { WineryAlertService } from '../winery-alert/winery-alert.service';
import { NgRedux } from '@angular-redux/store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { ButtonsStateModel } from '../models/buttonsState.model';
import { IWineryState } from '../redux/store/winery.store';
import { BackendService } from '../services/backend.service';
import { Subscription } from 'rxjs/Subscription';
import { Hotkey, HotkeysService } from 'angular2-hotkeys';

/**
 * The navbar of the topologymodeler.
 */
@Component({
    selector: 'winery-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.css'],
    animations: [
        trigger('navbarInOut', [
            transition('void => *', [
                style({ transform: 'translateY(-100%)' }),
                animate('200ms ease-out')
            ]),
            transition('* => void', [
                animate('200ms ease-in', style({ transform: 'translateY(-100%)' }))
            ])
        ])
    ]
})
export class NavbarComponent implements OnDestroy {

    /**
     * Boolean variables that hold the state {pressed vs. !pressed} of the navbar buttons.
     * @type {boolean}
     */
    @Input() hideNavBarState;

    @ViewChild('exportCsarButton')
    private exportCsarButtonRef: ElementRef;

    navbarButtonsState: ButtonsStateModel;
    unformattedTopologyTemplate;
    subscriptions: Array<Subscription> = [];
    exportCsarUrl: string;
    splittingOngoing: boolean;
    matchingOngoing: boolean;

    constructor(private alert: WineryAlertService,
                private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions,
                private backendService: BackendService,
                private hotkeysService: HotkeysService) {
        this.subscriptions.push(ngRedux.select(state => state.topologyRendererState)
            .subscribe(newButtonsState => this.setButtonsState(newButtonsState)));
        this.subscriptions.push(ngRedux.select(currentState => currentState.wineryState.currentJsonTopology)
            .subscribe(topologyTemplate => this.unformattedTopologyTemplate = topologyTemplate));
        this.hotkeysService.add(new Hotkey('ctrl+s', (event: KeyboardEvent): boolean => {
            event.stopPropagation();
            this.saveTopologyTemplateToRepository();
            return false; // Prevent bubbling
        }));
        this.exportCsarUrl = this.backendService.topologyTemplateURL + '/?csar';
    }

    /**
     * Setter for buttonstate
     * @param newButtonsState
     */
    setButtonsState(newButtonsState: ButtonsStateModel): void {
        this.navbarButtonsState = newButtonsState;
        if (!this.navbarButtonsState.buttonsState.splitTopologyButton) {
            this.splittingOngoing = false;
        }
        if (!this.navbarButtonsState.buttonsState.matchTopologyButton) {
            this.matchingOngoing = false;
        }
    }

    /**
     * Getter for the style of a pressed button.
     * @param buttonPressed
     */
    getStyle(buttonPressed: boolean): string {
        if (buttonPressed) {
            return '#AAEEAA';
        }
    }

    /**
     * Exports the service template as a CSAR file
     * @param event
     */
    exportCsar(event) {
        let url = this.exportCsarUrl;
        if (event.ctrlKey) {
            url = url.replace(/csar$/, 'definitions');
            console.log(url);
        }
        window.open(url);
    }

    /**
     * This function is called whenever a navbar button is clicked.
     * It contains a separate case for each button.
     * It toggles the `pressed` state of a button and publishes the respective
     * button {id and boolean} to the subscribers of the Observable inside
     * SharedNodeNavbarService.
     * @param event -- The click event of a button.
     */
    toggleButton(event) {
        switch (event.target.id) {
            case 'targetLocations': {
                this.ngRedux.dispatch(this.actions.toggleTargetLocations());
                break;
            }
            case 'policies': {
                this.ngRedux.dispatch(this.actions.togglePolicies());
                break;
            }
            case 'requirementsCapabilities': {
                this.ngRedux.dispatch(this.actions.toggleRequirementsCapabilities());
                break;
            }
            case 'deploymentArtifacts': {
                this.ngRedux.dispatch(this.actions.toggleDeploymentArtifacts());
                break;
            }
            case 'properties': {
                this.ngRedux.dispatch(this.actions.toggleProperties());
                break;
            }
            case 'types': {
                this.ngRedux.dispatch(this.actions.toggleTypes());
                break;
            }
            case 'ids': {
                this.ngRedux.dispatch(this.actions.toggleIds());
                break;
            }
            case 'layout': {
                this.ngRedux.dispatch(this.actions.executeLayout());
                break;
            }
            case 'alignh': {
                this.ngRedux.dispatch(this.actions.executeAlignH());
                break;
            }
            case 'alignv': {
                this.ngRedux.dispatch(this.actions.executeAlignV());
                break;
            }
            case 'importTopology': {
                this.ngRedux.dispatch(this.actions.importTopology());
                break;
            }
            case 'split': {
                this.ngRedux.dispatch(this.actions.splitTopology());
                this.splittingOngoing = true;
                break;
            }
            case 'match': {
                this.ngRedux.dispatch(this.actions.matchTopology());
                this.matchingOngoing = true;
                break;
            }
        }
    }

    /**
     * Calls the BackendService's saveTopologyTemplate method and displays a success message if successful.
     */
    saveTopologyTemplateToRepository() {
        // Initialization
        let topologySkeleton = {
            documentation: [],
            any: [],
            otherAttributes: {},
            relationshipTemplates: [],
            nodeTemplates: []
        };
        // subsciption first
        this.backendService.serviceTemplate$.subscribe(data => {
            topologySkeleton = data;
        });
        // Prepare for saving by updating the existing topology with the current topology state inside the Redux store
        topologySkeleton.nodeTemplates = this.unformattedTopologyTemplate.nodeTemplates;
        topologySkeleton.relationshipTemplates = this.unformattedTopologyTemplate.relationshipTemplates;
        // remove the 'Color' field from all nodeTemplates as the REST Api does not recognize it.
        topologySkeleton.nodeTemplates.map(nodeTemplate => {
            delete nodeTemplate.color;
            delete nodeTemplate.imageUrl;
        });
        const topologyToBeSaved = topologySkeleton;
        console.log(topologyToBeSaved);
        // The topology gets saved here.
        this.backendService.saveTopologyTemplate(topologyToBeSaved)
            .subscribe(res => {
                res.ok === true ? this.alert.success('<p>Saved the topology!<br>' + 'Response Status: '
                    + res.statusText + ' ' + res.status + '</p>')
                    : this.alert.info('<p>Something went wrong! <br>' + 'Response Status: '
                    + res.statusText + ' ' + res.status + '</p>');
            });
    }

    /**
     * Angular lifecycle event.
     */
    ngOnDestroy() {
        this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }
}
