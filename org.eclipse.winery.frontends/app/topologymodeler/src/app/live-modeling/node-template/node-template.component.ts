/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import { Component, OnDestroy, OnInit, } from '@angular/core';
import { of, Subject, Subscription } from 'rxjs';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { NodeTemplateInstance } from '../../models/container/node-template-instance.model';
import { LiveModelingService } from '../../services/live-modeling.service';
import { distinctUntilChanged, switchMap, tap } from 'rxjs/operators';
import { AdaptationAction, LiveModelingStates, NodeTemplateInstanceStates } from '../../models/enums';
import { ConfirmModalComponent } from '../modals/confirm-modal/confirm-modal.component';
import { BsModalService } from 'ngx-bootstrap';
import { WineryActions } from '../../redux/actions/winery.actions';

@Component({
    selector: 'winery-live-modeling-sidebar-node-template',
    templateUrl: './node-template.component.html',
    styleUrls: ['./node-template.component.css'],
})
export class NodeTemplateComponent implements OnInit, OnDestroy {

    subscriptions: Array<Subscription> = [];
    fetchingData = false;

    selectedNodeId: string;
    selectedNodeState: string;
    nodeTemplateInstanceData: NodeTemplateInstance;

    liveModelingState: LiveModelingStates;
    deploymentChanges: boolean;

    nodeSubject = new Subject<string>();

    objectKeys = Object.keys;
    NodeTemplateInstanceStates = NodeTemplateInstanceStates;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private liveModelingService: LiveModelingService,
                private modalService: BsModalService,
                private wineryActions: WineryActions) {
    }

    ngOnInit() {
        this.subscriptions.push(this.ngRedux.select((state) => {
            return state.wineryState.sidebarContents;
        })
            .subscribe((sidebarContents) => {
                if (sidebarContents.nodeClicked && sidebarContents.template.id) {
                    this.nodeSubject.next(sidebarContents.template.id);
                } else {
                    this.nodeSubject.next(null);
                }
            }));

        this.subscriptions.push(this.ngRedux.select((state) => {
            return state.liveModelingState.state;
        })
            .subscribe((state) => {
                this.liveModelingState = state;
            }));

        this.subscriptions.push(this.ngRedux.select((state) => {
            return state.liveModelingState.deploymentChanges;
        })
            .subscribe((deploymentChanges) => {
                this.deploymentChanges = deploymentChanges;
            }));

        this.subscriptions.push(this.nodeSubject.pipe(
            distinctUntilChanged(),
            tap((nodeId) => {
                this.selectedNodeId = nodeId;
                this.selectedNodeState = NodeTemplateInstanceStates.NOT_AVAILABLE;
            }),
            tap((_) => {
                this.fetchingData = true;
            }),
            switchMap((nodeId) => {
                return nodeId ? this.liveModelingService.fetchNodeTemplateInstanceData(nodeId) : of(null);
            })
        ).subscribe((resp) => {
            this.updateNodeInstanceData(resp);
            this.fetchingData = false;
        }));

    }

    enableControlButtons(): boolean {
        return this.selectedNodeId &&
            this.checkUpdatingData() &&
            this.checkLiveModelingState() &&
            this.checkNodeState();
    }

    checkUpdatingData(): boolean {
        return !this.fetchingData && !this.deploymentChanges;
    }

    checkLiveModelingState(): boolean {
        return this.liveModelingState === LiveModelingStates.ENABLED;
    }

    checkNodeState(): boolean {
        return this.selectedNodeState === NodeTemplateInstanceStates.NOT_AVAILABLE ||
            this.selectedNodeState === NodeTemplateInstanceStates.STARTED ||
            this.selectedNodeState === NodeTemplateInstanceStates.STOPPED ||
            this.selectedNodeState === NodeTemplateInstanceStates.DELETED;
    }

    async handleStartNode() {
        const resp = await this.openConfirmModal(
            'Start Node Instance',
            `Are you sure you want to start this node instance ${this.selectedNodeId}?
            This might affect other node instances' state too.`);
        if (resp) {
            await this.liveModelingService.adapt(this.selectedNodeId, AdaptationAction.START_NODE);
            this.unselectNodeTemplate();
        }
    }

    async handleStopNode() {
        const resp = await this.openConfirmModal(
            'Stop Node Instance',
            `Are you sure you want to stop the node instance ${this.selectedNodeId}?
            This might affect other node instances' state too.`);
        if (resp) {
            await this.liveModelingService.adapt(this.selectedNodeId, AdaptationAction.STOP_NODE);
            this.unselectNodeTemplate();
        }
    }

    async openConfirmModal(title: string, content: string, showWarning = false): Promise<boolean> {
        const initialState = { title, content, showWarning };
        const modalRef = this.modalService.show(ConfirmModalComponent, { initialState, backdrop: 'static' });
        await new Promise((resolve) => {
            const subscription = this.modalService.onHidden.subscribe((_) => {
                subscription.unsubscribe();
                resolve();
            });
        });

        return modalRef.content.confirmed;
    }

    unselectNodeTemplate() {
        this.ngRedux.dispatch(this.wineryActions.triggerSidebar({
                sidebarVisible: false,
                nodeClicked: false,
                id: '',
                nameTextFieldValue: '',
                type: '',
                properties: '',
                source: '',
                target: ''
        }));
        this.ngRedux.dispatch(this.wineryActions.sendPaletteOpened(false));
    }

    ngOnDestroy() {
        this.subscriptions.forEach((subscription) => {
            subscription.unsubscribe();
        });
    }

    private updateNodeInstanceData(nodeTemplateInstanceData: NodeTemplateInstance): void {
        if (nodeTemplateInstanceData) {
            this.nodeTemplateInstanceData = nodeTemplateInstanceData;
            this.selectedNodeState = nodeTemplateInstanceData.state;
        } else {
            this.nodeTemplateInstanceData = null;
            this.selectedNodeState = NodeTemplateInstanceStates.NOT_AVAILABLE;
        }
    }
}
