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

import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { WineryActions } from '../redux/actions/winery.actions';
import { Subject, Subscription } from 'rxjs';

import { animate, state, style, transition, trigger } from '@angular/animations';
import { QName } from '../models/qname';
import { urlElement } from '../models/enums';
import { BackendService } from '../services/backend.service';

/**
 * This is the right sidebar, where attributes of nodes and relationships get displayed.
 */
@Component({
    selector: 'winery-sidebar',
    templateUrl: './sidebar.component.html',
    styleUrls: ['./sidebar.component.css'],
    animations: [
        trigger('sidebarAnimationStatus', [
            state('in', style({transform: 'translateX(0)'})),
            transition('void => *', [
                style({transform: 'translateX(100%)'}),
                animate('100ms cubic-bezier(0.86, 0, 0.07, 1)')
            ]),
            transition('* => void', [
                animate('200ms cubic-bezier(0.86, 0, 0.07, 1)', style({
                    opacity: 0,
                    transform: 'translateX(100%)'
                }))
            ])
        ])
    ]
})
export class SidebarComponent implements OnInit, OnDestroy {
    // ngRedux sidebarSubscription
    sidebarSubscription;
    sidebarState: any;
    sidebarAnimationStatus: string;
    maxInputEnabled = true;
    min = 1;

    @Output() sidebarDeleteButtonClicked: EventEmitter<any> = new EventEmitter<any>();
    public nodeNameKeyUp: Subject<string> = new Subject<string>();
    public nodeMinInstancesKeyUp: Subject<string> = new Subject<string>();
    public nodeMaxInstancesKeyUp: Subject<string> = new Subject<string>();
    subscription: Subscription;

    constructor(private $ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                private backendService: BackendService) {
    }

    deleteButtonSidebarClicked($event) {
        this.sidebarDeleteButtonClicked.emit({
            event: $event,
            clicked: true
        });
    }

    /**
     * Closes the sidebar.
     */
    closeSidebar() {
        this.$ngRedux.dispatch(this.actions.openSidebar({
            sidebarContents: {
                sidebarVisible: false,
                nodeClicked: false,
                id: '',
                nameTextFieldValue: '',
                type: '',
                minInstances: -1,
                maxInstances: -1
            }
        }));
    }

    /**
     * CSS styling for "infinity button"
     */
    getInfinityButtonStyle(): string {
        return !this.maxInputEnabled ? '#ffc0c0' : 'rgb(240, 240, 240)';
    }

    /**
     * Angular lifecycle event.
     * initializes the sidebar with the correct data, also implements debounce time for a smooth user experience
     */
    ngOnInit() {
        this.sidebarSubscription = this.$ngRedux.select(wineryState => wineryState.wineryState.sidebarContents)
            .subscribe(sidebarContents => {
                    this.sidebarState = sidebarContents;
                    if (!this.sidebarState.nameTextFieldValue) {
                        this.sidebarState.nameTextFieldValue = this.sidebarState.id;
                    }
                    if (sidebarContents.sidebarVisible) {
                        this.sidebarAnimationStatus = 'in';
                    }
                }
            );
        // apply changes to the node name <input> field with a debounceTime of 300ms
        this.subscription = this.nodeNameKeyUp.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(data => {
                if (this.sidebarState.nodeClicked) {
                    this.$ngRedux.dispatch(this.actions.changeNodeName({
                        nodeNames: {
                            newNodeName: data,
                            id: this.sidebarState.id
                        }
                    }));
                } else {
                    this.$ngRedux.dispatch(this.actions.updateRelationshipName({
                        relData: {
                            newRelName: data,
                            id: this.sidebarState.id
                        }
                    }));
                }
                // refresh
                this.$ngRedux.dispatch(this.actions.openSidebar({
                    sidebarContents: {
                        sidebarVisible: true,
                        nodeClicked: this.sidebarState.nodeClicked,
                        id: this.sidebarState.id,
                        nameTextFieldValue: data,
                        type: this.sidebarState.type,
                        minInstances: Number(this.sidebarState.minInstances),
                        maxInstances: Number(this.sidebarState.maxInstances)
                    }
                }));
            });
        // minInstances
        const nodeMinInstancesKeyUpObservable = this.nodeMinInstancesKeyUp.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(data => {
                if (this.sidebarState.nodeClicked) {
                    this.$ngRedux.dispatch(this.actions.changeMinInstances({
                        minInstances: {
                            id: this.sidebarState.id,
                            count: data
                        }
                    }));
                }
                // refresh
                this.$ngRedux.dispatch(this.actions.openSidebar({
                    sidebarContents: {
                        sidebarVisible: true,
                        nodeClicked: this.sidebarState.nodeClicked,
                        id: this.sidebarState.id,
                        nameTextFieldValue: this.sidebarState.name,
                        type: this.sidebarState.type,
                        minInstances: Number(data),
                        maxInstances: this.sidebarState.maxInstances
                    }
                }));
            });
        // maxInstances
        const nodeMaxInstancesKeyUpObservable = this.nodeMaxInstancesKeyUp.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(data => {
                if (this.sidebarState.nodeClicked) {
                    this.$ngRedux.dispatch(this.actions.changeMaxInstances({
                        maxInstances: {
                            id: this.sidebarState.id,
                            count: data
                        }
                    }));
                }
                // refresh
                this.$ngRedux.dispatch(this.actions.openSidebar({
                    sidebarContents: {
                        sidebarVisible: true,
                        nodeClicked: this.sidebarState.nodeClicked,
                        id: this.sidebarState.id,
                        nameTextFieldValue: this.sidebarState.name,
                        type: this.sidebarState.type,
                        minInstances: this.sidebarState.minInstances,
                        maxInstances: Number(data)
                    }
                }));
            });
    }

    /**
     * Implements some checks, if the values from the user are correct, and updates the nodes
     * @param $event
     */
    minInstancesChanged($event) {
        if ($event === 'inc') {
            this.$ngRedux.dispatch(this.actions.incMinInstances({
                minInstances: {
                    id: this.sidebarState.id
                }
            }));
            let number: number = this.sidebarState.minInstances;
            number += 1;
            this.sidebarState.minInstances = number;
        } else if ($event === 'dec') {
            if (this.sidebarState.minInstances === 0) {
                this.sidebarState.minInstances = 0;
            } else {
                this.$ngRedux.dispatch(this.actions.decMinInstances({
                    minInstances: {
                        id: this.sidebarState.id
                    }
                }));
                this.sidebarState.minInstances -= 1;
            }
        }
        // refresh
        this.$ngRedux.dispatch(this.actions.openSidebar({
            sidebarContents: {
                sidebarVisible: true,
                nodeClicked: this.sidebarState.nodeClicked,
                id: this.sidebarState.id,
                nameTextFieldValue: this.sidebarState.name,
                type: this.sidebarState.type,
                minInstances: this.sidebarState.minInstances,
                maxInstances: this.sidebarState.maxInstances
            }
        }));
    }

    /**
     * Implements some checks, if the values from the user are correct, and updates the nodes
     * @param $event
     */
    maxInstancesChanged($event) {
        if (!(this.sidebarState.maxInstances === '\u221E')) {
            if ($event === 'inc') {
                this.$ngRedux.dispatch(this.actions.incMaxInstances({
                    maxInstances: {
                        id: this.sidebarState.id
                    }
                }));
                this.sidebarState.maxInstances = Number.parseInt(this.sidebarState.maxInstances) + 1;
            } else if ($event === 'dec') {
                if (this.sidebarState.maxInstances === 0) {
                    this.sidebarState.maxInstances = 0;
                } else {
                    this.$ngRedux.dispatch(this.actions.decMaxInstances({
                        maxInstances: {
                            id: this.sidebarState.id
                        }
                    }));
                    this.sidebarState.maxInstances -= 1;
                }
            } else if ($event === 'inf') {
                // infinity
                this.maxInputEnabled = false;
                this.sidebarState.maxInstances = '\u221E';
                this.$ngRedux.dispatch(this.actions.changeMaxInstances({
                    maxInstances: {
                        id: this.sidebarState.id,
                        count: '\u221E'
                    }
                }));
            }
        } else {
            this.$ngRedux.dispatch(this.actions.changeMaxInstances({
                maxInstances: {
                    id: this.sidebarState.id,
                    count: 0
                }
            }));
            this.sidebarState.maxInstances = 0;
            this.maxInputEnabled = true;
        }
        // refresh
        this.$ngRedux.dispatch(this.actions.openSidebar({
            sidebarContents: {
                sidebarVisible: true,
                nodeClicked: this.sidebarState.nodeClicked,
                id: this.sidebarState.id,
                nameTextFieldValue: this.sidebarState.name,
                type: this.sidebarState.type,
                minInstances: this.sidebarState.minInstances,
                maxInstances: this.sidebarState.maxInstances
            }
        }));
    }

    /**
     * Unmarks the node or relationship template upon altering the name in the side bar. Guarantuees that upon clicking
     * the 'del' key for intentionally clearing the node name the whole node template is not deleted. Upon putting focus
     * the node template is unmarked
     * @param $event
     */
    onFocus($event): void {
        this.$ngRedux.dispatch(this.actions.sendCurrentNodeId({
            id: this.sidebarState.id,
            focus: false
        }));
    }

    /**
     * Unmarks the node or relationship template upon altering the name in the side bar. Guarantuees that upon clicking
     * the 'del' key for intentionally clearing the node name the whole node template is not deleted. Upon blurring
     * the node template is marked again
     * @param $event
     */
    onBlur($event): void {
        this.$ngRedux.dispatch(this.actions.sendCurrentNodeId({
            id: this.sidebarState.id,
            focus: true
        }));
    }

    /**
     * Navigates to the corresponding type in the management UI
     * @param $event
     */
    linkType($event: any): void {
        let typeURL;
        const qName = new QName(this.sidebarState.type);
        if (this.sidebarState.nodeClicked) {
            typeURL = this.backendService.configuration.uiURL + '#' + urlElement.NodeTypeURL +
                encodeURIComponent(encodeURIComponent(qName.nameSpace)) + '/' + qName.localName
                + urlElement.ReadMe;
        } else {
            typeURL = this.backendService.configuration.uiURL + '#' + urlElement.RelationshipTypeURL +
                encodeURIComponent(encodeURIComponent(qName.nameSpace)) + qName.localName + urlElement.ReadMe;
        }
        window.open(typeURL, '_blank');
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}
