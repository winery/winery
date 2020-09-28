/********************************************************************************
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
 ********************************************************************************/

import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { WineryActions } from '../redux/actions/winery.actions';
import { Observable, Subject, Subscription } from 'rxjs';

import { animate, state, style, transition, trigger } from '@angular/animations';
import { QName } from '../models/qname';
import { PropertyDefinitionType, urlElement } from '../models/enums';
import { BackendService } from '../services/backend.service';
import { isNullOrUndefined } from 'util';
import { PolicyService } from '../services/policy.service';
import { ToastrService } from 'ngx-toastr';
import { TNodeTemplate, TTopologyTemplate } from '../models/ttopology-template';
import { TagService } from '../../../../tosca-management/src/app/instance/sharedComponents/tag/tag.service';
import { TagsAPIData } from '../../../../tosca-management/src/app/instance/sharedComponents/tag/tagsAPIData';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';

/**
 * This is the right sidebar, where attributes of nodes and relationships get displayed.
 */
@Component({
    selector: 'winery-sidebar',
    templateUrl: './sidebar.component.html',
    styleUrls: ['./sidebar.component.css'],
    animations: [
        trigger('sidebarAnimationStatus', [
            state('in', style({ transform: 'translateX(0)' })),
            transition('void => *', [
                style({ transform: 'translateX(100%)' }),
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
    propertyDefinitionType: string;
    subscriptions: Array<Subscription> = [];

    attributeViewVisible = false;
    participantValue = '';
    participants: string[] = [];
    groupTypes: string[] = ['location', 'participant'];
    endpoint: string;
    attributeName: string;
    attributeValue: string;
    currentTopologyTemplate: TTopologyTemplate;
    groupOptionSelection = 'reuse';
    participantOptionSelection = 'createNewParticipant';

    @Output() sidebarDeleteButtonClicked: EventEmitter<any> = new EventEmitter<any>();
    public nodeNameKeyUp: Subject<string> = new Subject<string>();
    public nodeMinInstancesKeyUp: Subject<string> = new Subject<string>();
    public nodeMaxInstancesKeyUp: Subject<string> = new Subject<string>();
    subscription: Subscription;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                private topologyRendererActions: TopologyRendererActions,
                private alert: ToastrService,
                private tagService: TagService,
                private backendService: BackendService,
                private policyService: PolicyService) {
        this.subscriptions.push(this.ngRedux.select(currentState => currentState.wineryState.currentJsonTopology)
            .subscribe(topologyTemplate => this.currentTopologyTemplate = topologyTemplate));
    }

    deleteButtonSidebarClicked($event) {
        this.sidebarDeleteButtonClicked.emit({
            event: $event,
            clicked: true
        });
    }

    onCreateNewGroup(): void {
        this.attributeName = '';
    }

    onUseExistingGroup(): void {
        this.attributeName = '';
    }

    selectGroupType(groupType): void {
        this.attributeName = groupType;
    }

    /**
     * Closes the sidebar.
     */
    closeSidebar() {
        this.attributeViewVisible = false;
        this.ngRedux.dispatch(this.actions.openSidebar({
            sidebarContents: {
                sidebarVisible: false,
                nodeClicked: false,
                id: '',
                nameTextFieldValue: '',
                type: '',
                minInstances: -1,
                maxInstances: -1,
                properties: '',
                groupViewVisible: false,
                group: null
            }
        }));
    }

    private getParticipants(): void {
        this.backendService.requestTopologyTemplateAndVisuals().subscribe(
            data => {
                this.retrieveParticipantsFromNodeTemplates(data[0].nodeTemplates);
            }
        );
    }

    private retrieveParticipantsFromNodeTemplates(nodeTemplates: TNodeTemplate[]): void {
        const wineryExtensionNamespace = '{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}';

        for (const nodeTemplate of nodeTemplates) {
            const participantValue = nodeTemplate.otherAttributes[wineryExtensionNamespace + 'participant'];
            if (participantValue) {
                if (!this.isParticipantExisting(participantValue)) {
                    const csvValue = participantValue.split(',');
                    for (const val of csvValue) {
                        if (this.participants.findIndex(part => part === val) < 0) {
                            this.participants.push(val);
                        }
                    }
                }
            }
        }
    }

    private isParticipantExisting(participantValue): boolean {
        return !(this.participants.findIndex(participant => participant === participantValue) < 0);
    }

    selectParticipant(participantValue): void {
        this.participantValue = participantValue;
        this.attributeValue = participantValue;
    }

    clearParticipant(): void {
        this.participantValue = '';
    }

    /**
     * CSS styling for "infinity button"
     */
    getInfinityButtonStyle(): string {
        return !this.maxInputEnabled ? '#ffc0c0' : 'rgb(240, 240, 240)';
    }

    findOutPropertyDefinitionTypeForProperties(): string {
        // if PropertiesDefinition doesn't exist then it must be of type NONE
        if (isNullOrUndefined(this.sidebarState.properties)) {
            this.propertyDefinitionType = PropertyDefinitionType.NONE;
        } else {
            // if no XML element inside PropertiesDefinition then it must be of type Key Value
            if (!this.sidebarState.properties.element) {
                this.propertyDefinitionType = PropertyDefinitionType.KV;
            } else {
                // else we have XML
                this.propertyDefinitionType = PropertyDefinitionType.XML;
            }
        }
        return this.propertyDefinitionType;
    }

    /**
     * Angular lifecycle event.
     * initializes the sidebar with the correct data, also implements debounce time for a smooth user experience
     */
    ngOnInit() {
        this.sidebarSubscription = this.ngRedux.select(wineryState => wineryState.wineryState.sidebarContents)
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
                    this.ngRedux.dispatch(this.actions.changeNodeName({
                        nodeNames: {
                            newNodeName: data,
                            id: this.sidebarState.id
                        }
                    }));
                } else {
                    this.ngRedux.dispatch(this.actions.updateRelationshipName({
                        relData: {
                            newRelName: data,
                            id: this.sidebarState.id,
                            properties: this.sidebarState.properties,
                            source: this.sidebarState.source,
                            target: this.sidebarState.target
                        }
                    }));
                }
                // refresh
                this.ngRedux.dispatch(this.actions.openSidebar({
                    sidebarContents: {
                        sidebarVisible: true,
                        nodeClicked: this.sidebarState.nodeClicked,
                        id: this.sidebarState.id,
                        nameTextFieldValue: data,
                        type: this.sidebarState.type,
                        minInstances: Number(this.sidebarState.minInstances),
                        maxInstances: Number(this.sidebarState.maxInstances),
                        properties: this.sidebarState.properties,
                        relationshipTemplate: this.sidebarState.relationshipTemplate,
                        source: this.sidebarState.source,
                        target: this.sidebarState.target
                    }
                }));
            });

        // minInstances
        const nodeMinInstancesKeyUpObservable = this.nodeMinInstancesKeyUp.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(data => {
                if (this.sidebarState.nodeClicked) {
                    this.ngRedux.dispatch(this.actions.changeMinInstances({
                        minInstances: {
                            id: this.sidebarState.id,
                            count: data
                        }
                    }));
                }
                // refresh
                this.ngRedux.dispatch(this.actions.openSidebar({
                    sidebarContents: {
                        sidebarVisible: true,
                        nodeClicked: this.sidebarState.nodeClicked,
                        id: this.sidebarState.id,
                        nameTextFieldValue: this.sidebarState.name,
                        type: this.sidebarState.type,
                        minInstances: Number(data),
                        maxInstances: this.sidebarState.maxInstances,
                        properties: this.sidebarState.properties,
                        relationshipTemplate: this.sidebarState.relationshipTemplate,
                        source: this.sidebarState.source,
                        target: this.sidebarState.target
                    }
                }));
            });
        // maxInstances
        const nodeMaxInstancesKeyUpObservable = this.nodeMaxInstancesKeyUp.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(data => {
                if (this.sidebarState.nodeClicked) {
                    this.ngRedux.dispatch(this.actions.changeMaxInstances({
                        maxInstances: {
                            id: this.sidebarState.id,
                            count: data
                        }
                    }));
                }
                // refresh
                this.ngRedux.dispatch(this.actions.openSidebar({
                    sidebarContents: {
                        sidebarVisible: true,
                        nodeClicked: this.sidebarState.nodeClicked,
                        id: this.sidebarState.id,
                        nameTextFieldValue: this.sidebarState.name,
                        type: this.sidebarState.type,
                        minInstances: this.sidebarState.minInstances,
                        maxInstances: Number(data),
                        properties: this.sidebarState.properties,
                        relationshipTemplate: this.sidebarState.relationshipTemplate,
                        source: this.sidebarState.source,
                        target: this.sidebarState.target
                    }
                }));
            });
    }

    addToGroup(): void {
        this.backendService.requestTopologyTemplateAndVisuals().subscribe(
            data => {
                data[0].nodeTemplates.forEach(nodeTemplate => {
                    this.currentTopologyTemplate.nodeTemplates.forEach(currentNodeTemplate => {
                        if (currentNodeTemplate.id === nodeTemplate.id) {
                            currentNodeTemplate.otherAttributes = nodeTemplate.otherAttributes;
                        }
                    });
                });
            }
        );
        this.attributeViewVisible = true;
        this.sidebarState.groupViewVisible = false;
        this.getParticipants();
    }

    submitNewAttribute(): void {
        if (this.attributeName === 'participant') {
            this.submitTag().subscribe(tagResponse => {
                this.alert.success('Successfully added selected Node Templates to participant ' + this.attributeValue);
                this.submitWineryAttribute().subscribe(attributeResponse => {
                    this.ngRedux.dispatch(this.topologyRendererActions.updateGroupView());
                    this.resetGroupConfig();
                    this.alert.success('Saved Topology Template');
                });
            });
        } else {
            this.submitWineryAttribute().subscribe(attributeResponse => {
                this.ngRedux.dispatch(this.topologyRendererActions.updateGroupView());
                this.resetGroupConfig();
                this.alert.success('Saved Topology Template');
            });
        }
        this.getParticipants();
    }

    private resetGroupConfig(): void {
        this.endpoint = '';
        this.attributeName = '';
        this.attributeValue = '';
        this.sidebarState.groupViewVisible = false;
        this.sidebarState.group = [];
        this.attributeViewVisible = false;
        this.participantValue = '';
    }

    private submitTag(): Observable<any> {
        const participantTag: TagsAPIData = new TagsAPIData();
        participantTag.name = this.attributeValue;
        participantTag.value = this.endpoint;
        return this.tagService.postTag(participantTag, this.backendService.serviceTemplateURL + '/tags');
    }

    private submitWineryAttribute(): Observable<any> {
        for (const selectedNode of this.sidebarState.group) {
            for (const nodeTemplate of this.currentTopologyTemplate.nodeTemplates) {
                if (selectedNode.id === nodeTemplate.id) {
                    // check if there were previously stored values and overwrite or add to comma-separated list of groups!
                    const attributeNodeTemplate = nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute(this.attributeName, this.attributeValue);
                    this.currentTopologyTemplate.nodeTemplates[this.currentTopologyTemplate.nodeTemplates.findIndex(element => element.id === nodeTemplate.id)]
                        = attributeNodeTemplate;
                }
            }
        }
        return this.backendService.saveTopologyTemplate(this.currentTopologyTemplate);

    }

    /**
     * Implements some checks, if the values from the user are correct, and updates the nodes
     * @param $event
     */
    minInstancesChanged($event) {
        if ($event === 'inc') {
            this.ngRedux.dispatch(this.actions.incMinInstances({
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
                this.ngRedux.dispatch(this.actions.decMinInstances({
                    minInstances: {
                        id: this.sidebarState.id
                    }
                }));
                this.sidebarState.minInstances -= 1;
            }
        }
        // refresh
        this.ngRedux.dispatch(this.actions.openSidebar({
            sidebarContents: {
                sidebarVisible: true,
                nodeClicked: this.sidebarState.nodeClicked,
                id: this.sidebarState.id,
                nameTextFieldValue: this.sidebarState.name,
                type: this.sidebarState.type,
                minInstances: this.sidebarState.minInstances,
                maxInstances: this.sidebarState.maxInstances,
                properties: this.sidebarState.properties,
                relationshipTemplate: this.sidebarState.relationshipTemplate,
                source: this.sidebarState.source,
                target: this.sidebarState.target
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
                this.ngRedux.dispatch(this.actions.incMaxInstances({
                    maxInstances: {
                        id: this.sidebarState.id
                    }
                }));
                this.sidebarState.maxInstances = Number.parseInt(this.sidebarState.maxInstances, 10) + 1;
            } else if ($event === 'dec') {
                if (this.sidebarState.maxInstances === 0) {
                    this.sidebarState.maxInstances = 0;
                } else {
                    this.ngRedux.dispatch(this.actions.decMaxInstances({
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
                this.ngRedux.dispatch(this.actions.changeMaxInstances({
                    maxInstances: {
                        id: this.sidebarState.id,
                        count: '\u221E'
                    }
                }));
            }
        } else {
            this.ngRedux.dispatch(this.actions.changeMaxInstances({
                maxInstances: {
                    id: this.sidebarState.id,
                    count: 0
                }
            }));
            this.sidebarState.maxInstances = 0;
            this.maxInputEnabled = true;
        }
        // refresh
        this.ngRedux.dispatch(this.actions.openSidebar({
            sidebarContents: {
                sidebarVisible: true,
                nodeClicked: this.sidebarState.nodeClicked,
                id: this.sidebarState.id,
                nameTextFieldValue: this.sidebarState.name,
                type: this.sidebarState.type,
                minInstances: this.sidebarState.minInstances,
                maxInstances: this.sidebarState.maxInstances,
                properties: this.sidebarState.properties,
                relationshipTemplate: this.sidebarState.relationshipTemplate,
                source: this.sidebarState.source,
                target: this.sidebarState.target
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
        this.ngRedux.dispatch(this.actions.sendCurrentNodeId({
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
        this.ngRedux.dispatch(this.actions.sendCurrentNodeId({
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

    addNewPolicy(nodeData: any) {
        const currentNodeData = { ...this.sidebarState.relationshipTemplate, ...nodeData };
        this.policyService.addNewPolicyToRelationship(currentNodeData);
    }
}
