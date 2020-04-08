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

import {
    AfterViewInit, Component, ComponentRef, DoCheck, ElementRef, EventEmitter, Input, KeyValueDiffers, NgZone, OnDestroy, OnInit, Output, Renderer2, ViewChild
} from '@angular/core';
import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { WineryActions } from '../redux/actions/winery.actions';
import { EntityType, TNodeTemplate } from '../models/ttopology-template';
import { QName } from '../models/qname';
import { PropertyDefinitionType, urlElement } from '../models/enums';
import { BackendService } from '../services/backend.service';
import { GroupedNodeTypeModel } from '../models/groupedNodeTypeModel';
import { EntityTypesModel } from '../models/entityTypesModel';
import { TopologyRendererState } from '../redux/reducers/topologyRenderer.reducer';
import { TPolicy } from '../models/policiesModalData';
import { Visuals } from '../models/visuals';

import { VersionElement } from '../models/versionElement';
import { VersionsComponent } from './versions/versions.component';
import { WineryVersion } from '../../../../tosca-management/src/app/model/wineryVersion';
import { FeatureEnum } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/wineryRepository.feature.direct';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { Subscription } from 'rxjs';
import { InheritanceUtils } from '../models/InheritanceUtils';

/**
 * Every node has its own component and gets created dynamically.
 */
@Component({
    selector: 'winery-node',
    templateUrl: './node.component.html',
    styleUrls: ['./node.component.css'],
    animations: [trigger('onCreateNodeTemplateAnimation', [
        state('hidden', style({ opacity: 0, transform: 'translateX(0)' })),
        state('visible', style({ opacity: 1, transform: 'scale' })),
        transition('hidden => visible', animate('300ms', keyframes([
            style({ opacity: 0, transform: 'scale(0.2)', offset: 0 }),
            style({ opacity: 0.3, transform: 'scale(1.1)', offset: 0.7 }),
            style({ opacity: 1, transform: 'scale(1.0)', offset: 1.0 })
        ]))),
    ]),
    ]
})
export class NodeComponent implements OnInit, AfterViewInit, OnDestroy, DoCheck {

    public items: string[] = ['Item 1', 'Item 2', 'Item 3'];
    nodeClass: string;
    visibilityState = 'hidden';
    connectorEndpointVisible = false;
    startTime;
    endTime;
    longpress = false;
    makeSelectionVisible = false;
    setFlash = false;
    setMaxFlash = false;
    setMinFlash = false;
    policyTypes: any;
    policyTemplates: any;
    artifactTypes: any;
    removeZIndex: any;
    propertyDefinitionType: PropertyDefinitionType;
    policyIcons: string[];
    configEnum = FeatureEnum;
    policiesOfNode: TPolicy[];
    private policyChangeSubscription: Subscription;
    private artifactsChangedSubscription: Subscription;

    @Input() readonly: boolean;
    @Input() entityTypes: EntityTypesModel;
    @Input() dragSource: string;
    @Input() navbarButtonsState: TopologyRendererState;
    @Input() nodeTemplate: TNodeTemplate;

    @Output() sendId: EventEmitter<string>;
    @Output() askForRepaint: EventEmitter<string>;
    @Output() setDragSource: EventEmitter<any>;
    @Output() closedEndpoint: EventEmitter<string>;
    @Output() handleNodeClickedActions: EventEmitter<any>;
    @Output() updateSelectedNodes: EventEmitter<string>;
    @Output() sendSelectedRelationshipType: EventEmitter<EntityType>;
    @Output() askForRemoval: EventEmitter<string>;
    @Output() unmarkConnections: EventEmitter<string>;
    @Output() saveNodeRequirements: EventEmitter<any>;
    @Output() sendPaletteStatus: EventEmitter<any>;
    @Output() sendNodeData: EventEmitter<any>;
    @Output() relationshipTemplateIdClicked: EventEmitter<string>;
    @Output() showYamlPolicyManagementModal: EventEmitter<void>;

    @ViewChild('versionModal') versionModal: VersionsComponent;
    previousPosition: any;
    currentPosition: any;
    nodeRef: ComponentRef<Component>;
    unbindMouseMove: Function;
    currentNodeId: string;
    flashTimer = 300;
    parentEl: string;
    popoverHtml = `<div class="">Open NodeType in a separate tab</div>`;
    // differ object for detecting changes made to the nodeTemplate object for DoCheck
    differ: any;

    newerVersions: WineryVersion[];
    newerVersionExist: boolean;
    newVersionElement: VersionElement;

    constructor(private zone: NgZone,
                private $ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                public elRef: ElementRef,
                private backendService: BackendService,
                private renderer: Renderer2,
                private configurationService: WineryRepositoryConfigurationService,
                private differs: KeyValueDiffers) {
        this.sendId = new EventEmitter();
        this.askForRepaint = new EventEmitter();
        this.setDragSource = new EventEmitter();
        this.closedEndpoint = new EventEmitter();
        this.handleNodeClickedActions = new EventEmitter();
        this.updateSelectedNodes = new EventEmitter();
        this.sendSelectedRelationshipType = new EventEmitter();
        this.askForRemoval = new EventEmitter();
        this.unmarkConnections = new EventEmitter();
        this.saveNodeRequirements = new EventEmitter();
        this.sendPaletteStatus = new EventEmitter();
        this.sendNodeData = new EventEmitter();
        this.relationshipTemplateIdClicked = new EventEmitter<string>();
        this.showYamlPolicyManagementModal = new EventEmitter<void>();

        // update node's policies if the list is changed
        if (configurationService.isYaml()) {
            this.policyChangeSubscription = $ngRedux.select(wineryState => wineryState.wineryState.currentJsonTopology.policies)
                .subscribe(policies => {
                    if (this.entityTypes) {
                        this.entityTypes.yamlPolicies = policies.policy;
                        this.policiesOfNode = this.getAllowedPolicies();
                    }
                });

            this.artifactsChangedSubscription = $ngRedux.select(wineryState => wineryState
                .wineryState
                .currentJsonTopology
                .nodeTemplates
                .find(nt => {
                    return this.nodeTemplate && nt.id === this.nodeTemplate.id;
                })
            ).subscribe(nodeTemplate => {
                if (this.nodeTemplate && nodeTemplate) {
                    this.nodeTemplate.artifacts = nodeTemplate.artifacts;
                }
            });
        }
        this.$ngRedux.subscribe(() => this.setPolicyIcons());
    }

    /**
     *  Parse the localName of the NodeType
     */
    get nodeTypeLocalName() {
        return this.nodeTemplate.type.split('}').pop();
    }

    public addItem(): void {
        this.items.push(`Items ${this.items.length + 1}`);
    }

    /**
     * This function determines which kind of properties the nodeType embodies.
     * We have 4 possibilities: none, XML element, Key value pairs or yaml-datatypes.
     */
    findOutPropertyDefinitionTypeForProperties(type: string, groupedNodeTypes: Array<GroupedNodeTypeModel>): void {
        let propertyDefinitionTypeAssigned: boolean;
        groupedNodeTypes.some(nameSpace => {
            nameSpace.children.some(nodeTypeVar => {
                if (nodeTypeVar.id === type) {
                    const node = nodeTypeVar.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0];
                    // if PropertiesDefinition doesn't exist then it must be of type NONE
                    if (!node.properties && node.derivedFrom) {
                        // check all parents; property definition types
                        propertyDefinitionTypeAssigned = this.checkParentPropertyDefinitions(node.derivedFrom.typeRef);
                    } else if (!node.properties) {
                        this.propertyDefinitionType = PropertyDefinitionType.NONE;
                        propertyDefinitionTypeAssigned = true;
                    } else {
                        // if no XML element inside PropertiesDefinition then it must be of type Key Value
                        if (!node.properties.element) {
                            this.propertyDefinitionType = this.configurationService.isYaml() ?
                                PropertyDefinitionType.YAML : PropertyDefinitionType.KV;
                            propertyDefinitionTypeAssigned = true;
                        } else {
                            // else we have XML
                            this.propertyDefinitionType = PropertyDefinitionType.XML;
                            propertyDefinitionTypeAssigned = true;
                        }
                    }
                    return true;
                }
            });
            if (propertyDefinitionTypeAssigned) {
                return true;
            }
        });
    }

    checkParentPropertyDefinitions(parentType: string): boolean {
        let parentFound = false;
        this.entityTypes.unGroupedNodeTypes.forEach(entry => {
            if (entry.qName === parentType) {
                parentFound = true;
                const node = entry.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0];
                if (!node.properties && node.derivedFrom) {
                    this.checkParentPropertyDefinitions(node.derivedFrom.typeRef);
                } else if (!node.properties) {
                    this.propertyDefinitionType = PropertyDefinitionType.NONE;
                    return true;
                } else {
                    if (!node.properties.element) {
                        this.propertyDefinitionType = PropertyDefinitionType.KV;
                    } else {
                        this.propertyDefinitionType = PropertyDefinitionType.XML;
                    }
                    return true;
                }
            }
        });

        if (!parentFound) {
            this.propertyDefinitionType = PropertyDefinitionType.NONE;
            return true;
        }

        return true;
    }

    /**
     * Angular lifecycle event.
     */
    ngOnInit() {
        this.differ = this.differs.find([]).create();
        if (this.nodeTemplate.visuals && this.nodeTemplate.visuals.pattern) {
            this.nodeClass = 'pattern';
        } else {
            this.nodeClass = 'nodeTemplate';
        }

        if (this.configurationService.isYaml()) {
            this.policiesOfNode = this.getAllowedPolicies();
        } else {
            this.setPolicyIcons();
        }

        this.addNewVersions(new QName(this.nodeTemplate.type));
    }

    /**
     * Angular lifecycle event.
     */
    ngDoCheck() {
        const nodeTemplateChanges = this.differ.diff(this.entityTypes);
        if (nodeTemplateChanges) {
            if (this.entityTypes.groupedNodeTypes) {
                this.findOutPropertyDefinitionTypeForProperties(this.nodeTemplate.type, this.entityTypes.groupedNodeTypes);
            }
        }
    }

    /**
     * Get the icons of the policies.
     */
    setPolicyIcons() {
        if (this.nodeTemplate.policies && this.nodeTemplate.policies.policy) {
            this.policyIcons = [];
            const list: TPolicy[] = this.nodeTemplate.policies.policy;

            for (const value of list) {
                let visual: Visuals;
                if (value.policyRef) {
                    visual = this.entityTypes.policyTemplateVisuals
                        .find(policyVisual => policyVisual.typeId === value.policyRef);
                }

                if (!visual) {
                    visual = this.entityTypes.policyTypeVisuals.find(
                        policyTypeVisual => policyTypeVisual.typeId === value.policyType
                    );
                }

                if (visual && visual.imageUrl) {
                    this.policyIcons.push(visual.imageUrl);
                }
            }

            if (this.policyIcons.length === 0) {
                this.policyIcons = null;
            }
        }
    }

    onRelationshipTemplateIdClicked(id: string) {
        this.relationshipTemplateIdClicked.emit(id);
    }

    /**
     * Triggered when opening a modal to send node data to the canvas for handling the addition of modal data.
     */
    sendToggleAction(nodeData: any): void {
        const currentNodeData = { ...this.nodeTemplate, ...nodeData };
        this.sendNodeData.emit(currentNodeData);
    }

    /**
     * Angular lifecycle event.
     */
    ngAfterViewInit(): void {
        this.sendId.emit(this.nodeTemplate.id);

        this.visibilityState = 'visible';
    }

    /**
     * Stops the event propagation to the canvas etc. and repaints.
     */
    repaint($event) {
        $event.stopPropagation();
        setTimeout(() => this.askForRepaint.emit('Repaint'), 1);
    }

    /**
     * Sets the current type of a node.
     */
    passCurrentType($event): void {
        $event.stopPropagation();
        $event.preventDefault();
        let currentType: string;
        try {
            currentType = $event.srcElement.innerText.replace(/\n/g, '').replace(/\s+/g, '');
        } catch (e) {
            currentType = $event.target.innerText.replace(/\n/g, '').replace(/\s+/g, '');
        }
        this.entityTypes.relationshipTypes.some(relType => {
            if (relType.qName.includes(currentType)) {
                this.sendSelectedRelationshipType.emit(relType);
                return true;
            }
        });
    }

    /**
     * Handler for mousedown events, toggles visibility of node attributes
     */
    mouseDownHandler($event): void {
        this.unmarkConnections.emit();
        this.startTime = new Date().getTime();
        this.repaint(new Event('repaint'));
        const focusNodeData = {
            id: this.nodeTemplate.id,
            ctrlKey: $event.ctrlKey
        };
        this.handleNodeClickedActions.emit(focusNodeData);
        try {
            this.parentEl = $event.srcElement.parentElement.className;
        } catch (e) {
            this.parentEl = $event.target.parentElement.className;
        }
        if (this.parentEl !== 'accordion-toggle' && this.parentEl !== 'ng-tns-c6-2' && this.parentEl) {
            const offsetLeft = this.elRef.nativeElement.firstChild.offsetLeft;
            const offsetTop = this.elRef.nativeElement.firstChild.offsetTop;
            this.previousPosition = {
                x: offsetLeft,
                y: offsetTop
            };
            this.zone.runOutsideAngular(() => {
                this.unbindMouseMove = this.renderer.listen(this.elRef.nativeElement, 'mousemove', (event) => this.mouseMove(event));
            });
        }
    }

    /**
     * If a node is moved, this saves the current position of the node into the store.
     */
    mouseMove($event): void {
        const offsetLeft = this.elRef.nativeElement.firstChild.offsetLeft;
        const offsetTop = this.elRef.nativeElement.firstChild.offsetTop;
        this.currentPosition = {
            x: offsetLeft,
            y: offsetTop
        };
    }

    /**
     * Checks if it was a click or a drag operation on the node.
     */
    mouseUpHandler($event): void {
        // mouseup
        this.endTime = new Date().getTime();
        this.testTimeDifference($event);
        if (this.previousPosition !== undefined && this.currentPosition !== undefined) {
            const differenceY = this.previousPosition.y - this.currentPosition.y;
            const differenceX = this.previousPosition.x - this.currentPosition.x;
            if (Math.abs(differenceX) > 2 || Math.abs(differenceY) > 2) {
                this.updateSelectedNodes.emit();
            }
        }
        if (this.unbindMouseMove) {
            this.unbindMouseMove();
        }
    }

    /**
     * CSS flash effect.
     */
    flash(flashType: string): void {
        if (flashType === 'name') {
            this.setFlash = true;
            setTimeout(() => this.setFlash = false, this.flashTimer);
        } else if (flashType === 'min') {
            this.setMinFlash = true;
            setTimeout(() => this.setMinFlash = false, this.flashTimer);
        } else if (flashType === 'max') {
            this.setMaxFlash = true;
            setTimeout(() => this.setMaxFlash = false, this.flashTimer);
        }
    }

    /**
     * If it was a click operation, close the connector endpoints for relations
     */
    closeConnectorEndpoints($event): void {
        $event.stopPropagation();
        if (!this.longpress && !$event.ctrlKey) {
            this.closedEndpoint.emit(this.nodeTemplate.id);
            this.repaint(new Event('repaint'));
        }
    }

    /**
     * Creates a dragoperation for nodes
     */
    makeSource($event): void {
        const dragSourceInfo = {
            dragSource: this.dragSource,
            nodeId: this.nodeTemplate.id
        };
        this.setDragSource.emit(dragSourceInfo);
    }

    /**
     * Only display the sidebar if the click is no longpress (drag)
     */
    openSidebar($event): void {
        $event.stopPropagation();
        // close sidebar when longpressing a node template
        if (this.longpress) {
            this.sendPaletteStatus.emit('close Sidebar');
            console.log('closing sidebar from node');
            this.$ngRedux.dispatch(this.actions.openSidebar({
                sidebarContents: {
                    visible: false,
                    nodeClicked: true,
                    nodeTemplate: {
                        id: '',
                        name: '',
                        type: '',
                        properties: {},
                    },
                    minInstances: -1,
                    maxInstances: -1
                }
            }));
        } else {
            this.$ngRedux.dispatch(this.actions.openSidebar({
                sidebarContents: {
                    visible: true,
                    nodeClicked: true,
                    nodeTemplate: this.nodeTemplate,
                    // special handling for instance restrictions due to infinity
                    minInstances: this.nodeTemplate.minInstances,
                    maxInstances: this.nodeTemplate.maxInstances,
                }
            }));
        }
    }

    /**
     * Navigates to the corresponding node type in the management UI
     *  $event
     */
    linkType($event: any): void {
        const qName = new QName(this.nodeTemplate.type);
        const typeURL = this.backendService.configuration.uiURL + urlElement.NodeTypeURL +
            encodeURIComponent(encodeURIComponent(qName.nameSpace)) + '/' + qName.localName
            + urlElement.ReadMe;
        window.open(typeURL, '_blank');
    }

    /**
     * Displays a box of the whole text if the text doesn't fit in the original element
     */
    isEllipsisActive(cell) {
        return (cell.offsetWidth < cell.scrollWidth);
    }

    /**
     * Angular lifecycle event.
     */
    ngOnDestroy(): void {
        this.askForRemoval.emit(this.nodeTemplate.id);
        if (this.nodeRef) {
            this.nodeRef.destroy();
        }

        if (this.policyChangeSubscription) {
            this.policyChangeSubscription.unsubscribe();
        }

        if (this.artifactsChangedSubscription) {
            this.artifactsChangedSubscription.unsubscribe();
        }
    }

    /**
     * Checks if it was a click or a drag operation on the node.
     *  $event
     */
    private testTimeDifference($event): void {
        if ((this.endTime - this.startTime) < 200) {
            this.longpress = false;
        } else if (this.endTime - this.startTime >= 200) {
            this.longpress = true;
        }
    }

    /**
     * Adding all newer Versions of Node Type
     */
    private addNewVersions(currentQname: QName): void {
        this.newerVersions = new Array<WineryVersion>();
        this.newerVersionExist = true;
        let index: number;
        const currentVersionElement = this.entityTypes.versionElements.find(versionElement => {

            return versionElement.qName === currentQname.qName;
        });

        if (currentVersionElement) {
            currentVersionElement.versions.find((version, indexNumber) => {
                if (version.currentVersion) {
                    index = indexNumber;
                }
                return version.currentVersion;
            });

            this.newerVersions = currentVersionElement.versions.slice(0, index);
            this.newVersionElement = new VersionElement(currentQname.qName, this.newerVersions);
        }

    }

    public openVersionModal() {
        this.versionModal.open();
    }

    private getAllowedPolicies() {
        // get the ancestry of the node type
        const nodeTypeAncestry = InheritanceUtils.getInheritanceAncestry(this.nodeTemplate.type, this.entityTypes.unGroupedNodeTypes);
        const result = [];
        // check each potential yaml policy
        this.entityTypes.yamlPolicies.forEach(policy => {
            // get the node types allowed as targets to this current policy
            const allowedNodeTypes = InheritanceUtils.getEffectiveTargetsOfYamlPolicyType(policy.policyType, this.entityTypes.policyTypes);

            if (allowedNodeTypes.length > 0) {
                // if the two sets of node types intersect, the current policy is allowed.
                if (allowedNodeTypes.some(nodeTypeQName => nodeTypeAncestry.some(ntAncestor => ntAncestor.qName === nodeTypeQName))) {
                    result.push(policy);
                }
            } else {
                // also, if the allowedNodeTypes array is empty, then all node types are allowed!
                result.push(policy);
            }
        });

        return result;
    }

    handleShowYamlPolicyManagementModal() {
        this.showYamlPolicyManagementModal.emit();
    }
}
