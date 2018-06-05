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
import {
    AfterViewInit, Component, DoCheck, ElementRef, HostListener, Input, KeyValueDiffers, NgZone, OnDestroy, OnInit,
    QueryList, Renderer2, ViewChild, ViewChildren
} from '@angular/core';
import { JsPlumbService } from '../services/jsPlumbService';
import { EntityType, TNodeTemplate, TRelationshipTemplate } from '../models/ttopology-template';
import { LayoutDirective } from '../layout/layout.directive';
import { WineryActions } from '../redux/actions/winery.actions';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { ButtonsStateModel } from '../models/buttonsState.model';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { NodeComponent } from '../node/node.component';
import { Hotkey, HotkeysService } from 'angular2-hotkeys';
import { ModalDirective } from 'ngx-bootstrap';
import { GridTemplate } from 'app/models/gridTemplate';
import { Subscription } from 'rxjs/Subscription';
import { CapabilitiesModalData } from '../models/capabilitiesModalData';
import { RequirementsModalData } from '../models/requirementsModalData';
import { NodeIdAndFocusModel } from '../models/nodeIdAndFocusModel';
import { ToggleModalDataModel } from '../models/toggleModalDataModel';
import { WineryAlertService } from '../winery-alert/winery-alert.service';
import { BackendService } from '../services/backend.service';
import { hostURL } from '../models/configuration';
import { CapabilityModel } from '../models/capabilityModel';
import { isNullOrUndefined } from 'util';
import { RequirementModel } from '../models/requirementModel';
import { EntityTypesModel } from '../models/entityTypesModel';
import { ExistsService } from '../services/exists.service';
import { ModalVariant, ModalVariantAndState } from './entities-modal/modal-model';
import { align, toggleModalType } from '../models/enums';
import { QName } from '../models/qname';
import { ImportTopologyModalData } from '../models/importTopologyModalData';
import { ImportTopologyService } from '../services/import-topology.service';
import { ReqCapService } from '../services/req-cap.service';
import { SplitMatchTopologyService } from '../services/split-match-topology.service';
import { DifferenceStates, VersionUtils } from '../models/ToscaDiff';
import { ErrorHandlerService } from '../services/error-handler.service';
import { DragSource } from '../models/DragSource';

@Component({
    selector: 'winery-canvas',
    providers: [LayoutDirective],
    templateUrl: './canvas.component.html',
    styleUrls: ['./canvas.component.css']
})
export class CanvasComponent implements OnInit, OnDestroy, AfterViewInit, DoCheck {

    @ViewChildren(NodeComponent) nodeComponentChildren: QueryList<NodeComponent>;
    @ViewChildren('KVTextareas') KVTextareas: QueryList<any>;
    @ViewChildren('XMLTextareas') xmlTextareas: QueryList<any>;
    @ViewChild('nodes') child: ElementRef;
    @ViewChild('selection') selection: ElementRef;
    @ViewChild('capabilitiesModal') capabilitiesModal: ModalDirective;
    @ViewChild('requirementsModal') requirementsModal: ModalDirective;
    @ViewChild('importTopologyModal') importTopologyModal: ModalDirective;
    @Input() entityTypes: EntityTypesModel;
    @Input() relationshipTypes: Array<EntityType> = [];
    @Input() diffMode = false;

    readonly draggingThreshold = 300;
    readonly newNodePositionOffsetX = 108;
    readonly newNodePositionOffsetY = 30;

    allNodeTemplates: Array<TNodeTemplate> = [];
    allRelationshipTemplates: Array<TRelationshipTemplate> = [];
    navbarButtonsState: ButtonsStateModel;
    selectedNodes: Array<TNodeTemplate> = [];
    // current data emitted from a node
    currentModalData: any;
    dragSourceActive = false;
    selectedRelationshipType: EntityType;
    nodeChildrenIdArray: Array<string>;
    nodeChildrenArray: Array<NodeComponent>;
    jsPlumbBindConnection = false;
    newNode: TNodeTemplate;
    paletteOpened: boolean;
    newJsPlumbInstance: any;
    gridTemplate: GridTemplate;
    allNodesIds: Array<string> = [];
    dragSourceInfos: DragSource;
    longPress: boolean;
    startTime: number;
    endTime: number;
    subscriptions: Array<Subscription> = [];
    // unbind mouse move and up functions
    unbindMouseActions: Array<Function> = [];

    // variables which hold their corresponding modal data
    capabilities: CapabilitiesModalData;
    requirements: RequirementsModalData;
    importTopologyData: ImportTopologyModalData;

    indexOfNewNode: number;
    targetNodes: Array<string> = [];

    // used for Angular DoCheck Lifecycle hook
    differ: any;

    // scroll offset
    scrollOffset = 0;

    // modalVariantAndState is passed to the entities-modal component and tells it which modal to render
    modalData: ModalVariantAndState = {
        modalVisible: true,
        modalVariant: ModalVariant.None,
        modalTitle: 'none'
    };

    // used to display the correct modal sort
    showCurrentRequirement: boolean;
    showCurrentCapability: boolean;

    showDefaultProperties: boolean;
    hideNavBarAndPalette = false;

    // holds all Id's of the topology template
    allIds: Array<String> = [];

    // determines if a warning in the modal of having duplicate Id's is shown
    duplicateId = false;

    private longPressing: boolean;

    constructor(private jsPlumbService: JsPlumbService,
                private eref: ElementRef,
                private layoutDirective: LayoutDirective,
                private ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                private topologyRendererActions: TopologyRendererActions,
                private zone: NgZone,
                private hotkeysService: HotkeysService,
                private renderer: Renderer2,
                private alert: WineryAlertService,
                private differs: KeyValueDiffers,
                private backendService: BackendService,
                private importTopologyService: ImportTopologyService,
                private existsService: ExistsService,
                private splitMatchService: SplitMatchTopologyService,
                private reqCapService: ReqCapService,
                private errorHandler: ErrorHandlerService) {
        this.newJsPlumbInstance = this.jsPlumbService.getJsPlumbInstance();
        this.newJsPlumbInstance.setContainer('container');
        console.log(this.newJsPlumbInstance);
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.currentJsonTopology.nodeTemplates)
            .subscribe(currentNodes => this.updateNodes(currentNodes)));
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.currentJsonTopology.relationshipTemplates)
            .subscribe(currentRelationships => this.updateRelationships(currentRelationships)));
        this.subscriptions.push(this.ngRedux.select(state => state.topologyRendererState)
            .subscribe(currentButtonsState => this.setButtonsState(currentButtonsState)));
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.currentNodeData)
            .subscribe(currentNodeData => this.toggleMarkNode(currentNodeData)));
        this.gridTemplate = new GridTemplate(100, false, false, 30);
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.currentPaletteOpenedState)
            .subscribe(currentPaletteOpened => this.setPaletteState(currentPaletteOpened)));
        this.hotkeysService.add(new Hotkey('ctrl+a', (event: KeyboardEvent): boolean => {
            event.stopPropagation();
            this.allNodeTemplates.forEach(node => this.enhanceDragSelection(node.id));
            return false; // Prevent bubbling
        }));
        this.capabilities = new CapabilitiesModalData();
        this.requirements = new RequirementsModalData();
        this.importTopologyData = new ImportTopologyModalData();
    }

    /**
     * Needed for the optimal user experience when dragging a selection box.
     * Upon detecting a long mouse down the navbar and the palette fade out for maximum dragging space.
     * Resets the values.
     */
    @HostListener('mouseup')
    onMouseUp() {
        this.longPressing = false;
    }

    /**
     * Needed for the optimal user experience when dragging a selection box.
     * Upon detecting a long mouse down the navbar and the palette fade out for maximum dragging space.
     * Sets the values upon detecting a long mouse down press.
     */
    @HostListener('mousedown', ['$event'])
    onMouseDown(event) {
        // don't do right/middle clicks
        if (event.which !== 1) {
            return;
        }
        this.longPressing = false;
        setTimeout(() => this.longPressing = true, 250);
    }

    /**
     * Gets called if nodes get deleted, created, or node attributes are updated and calls the
     * correct handler.
     * @param currentNodes  List of all displayed nodes.
     */
    updateNodes(currentNodes: Array<TNodeTemplate>): void {
        const storeNodesLength = currentNodes.length;
        const localCopyNodesLength = this.allNodeTemplates.length;
        if (storeNodesLength !== localCopyNodesLength) {
            const difference = currentNodes.length - this.allNodeTemplates.length;
            if (difference === 1 && this.paletteOpened) {
                this.handleNewNode(currentNodes);
            } else if (difference < 0) {
                this.handleDeletedNodes(currentNodes);
            } else if (difference === 1 && !this.paletteOpened) {
                this.allNodeTemplates.push(currentNodes[currentNodes.length - 1]);
                this.revalidateContainer();
            } else {
                this.allNodeTemplates = currentNodes;
            }
        } else if (storeNodesLength !== 0 && localCopyNodesLength !== 0) {
            this.updateNodeAttributes(currentNodes);
        }
        this.allNodesIds = this.allNodeTemplates.map(node => node.id);
    }

    /**
     * Executed when a node is short clicked triggering the sidebar, focusing on the name input field and
     * upon unfocusing the input field blurs away
     * @param currentNodeData - holds the node id and a focus boolean value which determines the marking or unmarking
     *     of the node
     */
    toggleMarkNode(currentNodeData: NodeIdAndFocusModel) {
        if (this.nodeChildrenArray) {
            this.nodeChildrenArray.forEach(node => {
                if (node.nodeTemplate.id === currentNodeData.id) {
                    if (currentNodeData.focus === true) {
                        node.makeSelectionVisible = true;
                    } else {
                        node.makeSelectionVisible = false;
                    }
                }
            });
        }
    }

    /**
     * Setter for PaletteState, triggered by a redux store change and getting latest value
     * @param currentPaletteOpened
     */
    setPaletteState(currentPaletteOpened: boolean): void {
        if (currentPaletteOpened) {
            this.paletteOpened = currentPaletteOpened;
            this.gridTemplate.marginLeft = 300;
        } else {
            this.gridTemplate.marginLeft = 30;
        }
    }

    /**
     * Gets all ID's of the topology template and saves them in an array
     */
    private getAllIds(): void {
        this.allIds = [];
        // get all Id's of the node templates
        this.allNodeTemplates.forEach(node => {
            if (this.allIds.length > 0) {
                this.setId(node.id);
                if (node.requirements) {
                    if (node.requirements.requirement) {
                        node.requirements.requirement.forEach(req => {
                            this.setId(req.id);
                        });
                    }
                }
                if (node.capabilities) {
                    if (node.capabilities.capability) {
                        node.capabilities.capability.forEach(cap => {
                            this.setId(cap.id);
                        });
                    }
                }
            } else {
                this.allIds.push(node.id);
            }
        });
        // get all relationship Id's
        this.allRelationshipTemplates.forEach(rel => {
            this.setId(rel.id);
        });
    }

    /**
     * Checks if the id is already in the array, if not the id is added
     */
    private setId(idOfElement: string): void {
        if (!this.allIds.find(id => id === idOfElement)) {
            this.allIds.push(idOfElement);
        }
    }

    /**
     * This modal handler gets triggered by the node component
     * @param currentNodeData - this holds the corresponding node template information and the information which modal
     *     to show
     */
    public toggleModalHandler(currentNodeData: ToggleModalDataModel) {
        this.currentModalData = currentNodeData;
        this.modalData.modalVisible = true;
        this.duplicateId = false;
        this.getAllIds();
        switch (currentNodeData.currentNodePart) {
            case toggleModalType.DeploymentArtifacts:
                this.modalData.modalVariant = ModalVariant.DeploymentArtifacts;
                this.modalData.modalTitle = 'Deployment Artifact';
                break;
            case toggleModalType.Policies:
                this.modalData.modalVariant = ModalVariant.Policies;
                this.modalData.modalTitle = 'Policy';
                break;
            case toggleModalType.Requirements:
                this.modalData.modalVariant = ModalVariant.Other;
                this.modalData.modalVisible = false;
                this.resetRequirements();
                this.requirements.requirements = currentNodeData.requirements;
                this.requirements.nodeId = currentNodeData.id;
                // if a requirement in the table is clicked show the data in the modal
                if (!isNullOrUndefined(currentNodeData.currentRequirement)) {
                    this.showCurrentRequirement = true;
                    this.requirements.reqId = currentNodeData.currentRequirement.id;
                    this.requirements.oldReqId = currentNodeData.currentRequirement.id;
                    this.requirements.reqDefinitionName = currentNodeData.currentRequirement.name;
                    this.requirements.reqQName = currentNodeData.currentRequirement.type;
                    this.requirements.reqQNameLocalName = new QName(currentNodeData.currentRequirement.type).localName;
                    // check which propertyType is defined by checking with the defined requirement type property types
                    // from the repository
                    this.entityTypes.requirementTypes.some(reqType => {
                        if (currentNodeData.currentRequirement.type === reqType.qName) {
                            // if any is defined with at least one element it's a KV property, sets default values if
                            // there aren't any in the node template
                            if (reqType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].any.length > 0) {
                                this.requirements.propertyType = 'KV';
                                if (currentNodeData.currentRequirement.properties) {
                                    if (currentNodeData.currentRequirement.properties.kvproperties) {
                                        this.requirements.properties = currentNodeData.currentRequirement.properties.kvproperties;
                                        return true;
                                    } else {
                                        this.requirements.properties = this.setKVProperties(reqType);
                                        this.setDefaultReqKVProperties();
                                        return true;
                                    }
                                } else {
                                    this.requirements.properties = this.setKVProperties(reqType);
                                    this.setDefaultReqKVProperties();
                                    return true;
                                }
                                // if propertiesDefinition is defined it's a XML property
                            } else if (reqType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition) {
                                if (reqType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element) {
                                    this.requirements.propertyType = 'XML';
                                    const defaultXML = reqType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element;
                                    if (currentNodeData.currentRequirement.properties) {
                                        if (currentNodeData.currentRequirement.properties.any) {
                                            this.requirements.properties = currentNodeData.currentRequirement.properties.any;
                                            return true;
                                        } else {
                                            this.requirements.properties = defaultXML;
                                            this.setDefaultReqKVProperties();
                                            return true;
                                        }
                                    } else {
                                        this.requirements.properties = defaultXML;
                                        this.setDefaultReqKVProperties();
                                        return true;
                                    }
                                }
                            } else {
                                // else no properties
                                this.requirements.propertyType = '';
                                return true;
                            }
                        }
                    });
                } else {
                    this.showCurrentRequirement = false;
                    try {
                        // request all valid requirement types for that node type for display as name select options in
                        // the modal
                        this.reqCapService.requestRequirementDefinitionsOfNodeType(currentNodeData.type).subscribe(data => {
                            this.requirements.reqDefinitionNames = [];
                            this.requirements.reqDefinitionName = '';
                            for (const reqType of data) {
                                const qNameOfType = new QName(reqType.requirementType);
                                this.requirements.reqDefinitionNames.push(qNameOfType.localName);
                            }
                        });
                    } catch (e) {
                        this.requirements.requirements = '';
                    }
                }
                this.requirementsModal.show();
                break;
            case toggleModalType.Capabilities:
                this.modalData.modalVariant = ModalVariant.Other;
                this.modalData.modalVisible = false;
                this.resetCapabilities();
                this.capabilities.capabilities = currentNodeData.capabilities;
                this.capabilities.nodeId = currentNodeData.id;
                // if a capability in the table is clicked show the data in the modal
                if (!isNullOrUndefined(currentNodeData.currentCapability)) {
                    this.showCurrentCapability = true;
                    this.capabilities.capId = currentNodeData.currentCapability.id;
                    this.capabilities.oldCapId = currentNodeData.currentCapability.id;
                    this.capabilities.capDefinitionName = currentNodeData.currentCapability.name;
                    this.capabilities.capQName = currentNodeData.currentCapability.type;
                    this.capabilities.capQNameLocalName = new QName(currentNodeData.currentCapability.type).localName;
                    // check which propertyType is defined by checking with the defined capability type property types
                    // from the repository
                    this.entityTypes.capabilityTypes.some(capType => {
                        if (currentNodeData.currentCapability.type === capType.qName) {
                            // if any is defined with at least one element it's a KV property, sets default values if
                            // there aren't any in the node template
                            if (capType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].any.length > 0) {
                                this.capabilities.propertyType = 'KV';
                                if (currentNodeData.currentCapability.properties) {
                                    if (currentNodeData.currentCapability.properties.kvproperties) {
                                        this.capabilities.properties = currentNodeData.currentCapability.properties.kvproperties;
                                        return true;
                                    } else {
                                        this.capabilities.properties = this.setKVProperties(capType);
                                        this.setDefaultCapKVProperties();
                                        return true;
                                    }
                                } else {
                                    this.capabilities.properties = this.setKVProperties(capType);
                                    this.setDefaultCapKVProperties();
                                    return true;
                                }
                                // if propertiesDefinition is defined it's a XML property
                            } else if (capType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition) {
                                if (capType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element) {
                                    this.capabilities.propertyType = 'XML';
                                    const defaultXML = capType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element;
                                    if (currentNodeData.currentCapability.properties) {
                                        if (currentNodeData.currentCapability.properties.any) {
                                            this.capabilities.properties = currentNodeData.currentCapability.properties.any;
                                            return true;
                                        } else {
                                            this.capabilities.properties = defaultXML;
                                            this.setDefaultCapXMLProperties();
                                            return true;
                                        }
                                    } else {
                                        this.capabilities.properties = defaultXML;
                                        this.setDefaultCapXMLProperties();
                                        return true;
                                    }
                                }
                            } else {
                                // else no properties
                                this.capabilities.propertyType = '';
                                return true;
                            }
                        }
                    });
                } else {
                    this.showCurrentCapability = false;
                    try {
                        // request all valid capability types for that node type for display as name select options in
                        // the modal
                        this.reqCapService.requestCapabilityDefinitionsOfNodeType(currentNodeData.type).subscribe(data => {
                            this.capabilities.capDefinitionNames = [];
                            this.capabilities.capDefinitionName = '';
                            for (const capType of data) {
                                const qNameOfType = new QName(capType.capabilityType);
                                this.capabilities.capDefinitionNames.push(qNameOfType.localName);
                            }
                        });
                    } catch (e) {
                        this.capabilities.capabilities = '';
                    }
                }
                this.capabilitiesModal.show();
                break;
        }
    }

    /**
     * This function sets the capability default KV properties
     */
    setDefaultCapKVProperties(): void {
        this.capabilities.capabilities.capability.some(cap => {
            if (cap.id === this.currentModalData.currentCapability.id) {
                cap.properties = {
                    kvproperties:
                    this.capabilities.properties
                };
            }
        });
    }

    /**
     * This function sets the requirement default KV properties
     */
    setDefaultReqKVProperties(): void {
        this.requirements.requirements.requirement.some(req => {
            if (req.id === this.currentModalData.currentRequirement.id) {
                req.properties = {
                    kvproperties:
                    this.requirements.properties
                };
            }
        });
    }

    /**
     * This function sets the capability default XML properties
     */
    setDefaultCapXMLProperties(): void {
        this.capabilities.capabilities.capability.some(cap => {
            if (cap.id === this.currentModalData.currentCapability.id) {
                cap.properties = {
                    any: this.capabilities.properties
                };
            }
        });
    }

    /**
     * This function sets the requirement default XML properties
     */
    setDefaultReqXMLProperties(): void {
        this.requirements.requirements.requirement.some(req => {
            if (req.id === this.currentModalData.currentCapability.id) {
                req.properties = {
                    any: this.requirements.properties
                };
            }
        });
    }

    /**
     * This function sets the node's KV properties
     * @param any type : the element type, e.g. capabilityType, requirementType etc.
     * @returns newKVProperties     KV Properties as Object
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
     * Gets called from the modal to update all the capability data
     */
    updateCaps(): void {
        let currentIndex;
        // search for the kv property index within the requirement object of the requirements array of the current
        // requirement
        this.capabilities.capabilities.capability.some((cap, index) => {
            if (cap.id === this.capabilities.oldCapId) {
                currentIndex = index;
                return true;
            }
        });
        if (this.capabilities.propertyType === 'KV') {
            this.KVTextareas.forEach(txtArea => {
                const keyOfChangedTextArea = txtArea.nativeElement.parentElement.innerText.replace(/\s/g, '');
                this.capabilities.capabilities.capability[currentIndex].properties.kvproperties[keyOfChangedTextArea] = txtArea.nativeElement.value;
            });
        } else if (this.capabilities.propertyType === 'XML') {
            this.xmlTextareas.forEach(xmlTextArea => {
                this.capabilities.capabilities.capability[currentIndex].properties.any = xmlTextArea.nativeElement.value;
            });
        }
        this.capabilities.capabilities.capability[currentIndex].id = this.capabilities.capId;
        const newCapabilityData = this.capabilities.capabilities;
        newCapabilityData.nodeId = this.capabilities.nodeId;
        this.ngRedux.dispatch(this.actions.setCapability(newCapabilityData));
        this.resetCapabilities();
        this.capabilitiesModal.hide();
    }

    /**
     * Gets called from the modal to update all the requirement data
     */
    updateReqs(): void {
        let currentIndex;
        // search for the kv property index within the requirement object of the requirements array of the current
        // requirement
        this.requirements.requirements.requirement.some((req, index) => {
            if (req.id === this.requirements.oldReqId) {
                currentIndex = index;
                return true;
            }
        });
        if (this.requirements.propertyType === 'KV') {
            this.KVTextareas.forEach(txtArea => {
                const keyOfChangedTextArea = txtArea.nativeElement.parentElement.innerText.replace(/\s/g, '');
                this.requirements.requirements.requirement[currentIndex].properties.kvproperties[keyOfChangedTextArea] = txtArea.nativeElement.value;
            });
        } else if (this.requirements.propertyType === 'XML') {
            this.xmlTextareas.forEach(xmlTextArea => {
                this.requirements.requirements.requirement[currentIndex].properties.any = xmlTextArea.nativeElement.value;
            });
        }
        this.requirements.requirements.requirement[currentIndex].id = this.requirements.reqId;
        const newRequirementData = this.requirements.requirements;
        newRequirementData.nodeId = this.requirements.nodeId;
        this.ngRedux.dispatch(this.actions.setRequirement(newRequirementData));
        this.resetRequirements();
        this.requirementsModal.hide();
    }

    getHostUrl(): string {
        return hostURL;
    }

    /**
     * Saves a capability template to the model and gets pushed into the Redux store of the application
     */
    saveCapabilityToModel(): void {
        const newCapability = new CapabilityModel();
        newCapability.any = [];
        newCapability.documentation = [];
        newCapability.id = this.capabilities.capId;
        newCapability.name = this.capabilities.capQName.substring(this.capabilities.capQName.indexOf('}') + 1);
        newCapability.otherAttributes = {};
        newCapability.type = this.capabilities.capQName;
        // case that a capability type with KV properties was chosen in the model and the default KV properties are
        // shown and modified by the user
        if (this.capabilities.propertyType === 'KV') {
            // get all values from the KV property textareas
            this.KVTextareas.forEach(txtArea => {
                const keyOfChangedTextArea = txtArea.nativeElement.parentElement.innerText.replace(/\s/g, '');
                this.capabilities.properties[keyOfChangedTextArea] = txtArea.nativeElement.value;
            });
            newCapability.properties = {
                kvproperties: this.capabilities.properties
            };
            // case that a capability type with XML properties was chosen in the model and the default XML properties
            // are shown and modified by the user
        } else if (this.capabilities.propertyType === 'XML') {
            this.xmlTextareas.forEach(xmlTextArea => {
                this.capabilities.properties = xmlTextArea.nativeElement.value;
            });
            newCapability.properties = {
                any: this.capabilities.properties
            };
        }
        // case when there are no capabilities on the node template
        if (isNullOrUndefined(this.capabilities.capabilities)) {
            const capabilityArray: Array<CapabilityModel> = [];
            this.capabilities.capabilities = {
                capability: capabilityArray
            };
        }
        this.capabilities.capabilities.capability.push(newCapability);
        const newCapabilityData = this.capabilities.capabilities;
        newCapabilityData.nodeId = this.capabilities.nodeId;
        this.ngRedux.dispatch(this.actions.setCapability(newCapabilityData));
        this.resetCapabilities();
        this.capabilitiesModal.hide();
    }

    /**
     * Auto-completes other capability relevant values when a capability name has been selected in the modal
     */
    onChangeCapDefinitionName(capName: string) {
        this.entityTypes.capabilityTypes.some(cap => {
            if (cap.name === capName) {
                this.capabilities.capType = cap.namespace;
                this.capabilities.capQName = cap.qName;
                this.capabilities.capQNameLocalName = new QName(cap.qName).localName;
                // check which propertyType is defined by checking with the defined capability types from the
                // repository
                // if any is defined with at least one element it's a KV property, sets default values if there aren't
                // any in the node template
                if (cap.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].any.length > 0) {
                    this.capabilities.propertyType = 'KV';
                    this.showDefaultProperties = true;
                    this.capabilities.properties = this.setKVProperties(cap);
                    // if propertiesDefinition is defined it's a XML property
                } else if (cap.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition) {
                    if (cap.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element) {
                        this.capabilities.propertyType = 'XML';
                        this.showDefaultProperties = true;
                        this.capabilities.properties = cap.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element;
                    }
                } else {
                    // else no properties
                    this.capabilities.propertyType = '';
                    this.showDefaultProperties = false;
                }
                return true;
            }
        });
    }

    /**
     * saves the typed in capability id from the modal
     */
    onChangeCapId(capId: string) {
        if (!this.allIds.find(id => id === capId)) {
            this.capabilities.capId = capId;
            this.duplicateId = false;
        } else {
            this.duplicateId = true;
        }
    }

    /**
     * Deletes a capability from the winery store
     */
    deleteCapability() {
        const capabilities = {
            nodeId: this.currentModalData.id,
            capability: this.currentModalData.capabilities.capability.filter(req => req.id !== this.currentModalData.currentCapability.id)
        };
        this.ngRedux.dispatch(this.actions.setCapability(capabilities));
        this.resetCapabilities();
        this.capabilitiesModal.hide();
    }

    /**
     * Saves a requirement template to the model and gets pushed into the Redux store of the application
     */
    saveRequirementsToModel(): void {
        const newRequirement = new RequirementModel();
        newRequirement.any = [];
        newRequirement.documentation = [];
        newRequirement.id = this.requirements.reqId;
        newRequirement.name = this.requirements.reqQName.substring(this.requirements.reqQName.indexOf('}') + 1);
        newRequirement.otherAttributes = {};
        newRequirement.type = this.requirements.reqQName;
        // case that a requirement type with KV properties was chosen in the model and the default KV properties are
        // shown and modified by the user
        if (this.requirements.propertyType === 'KV') {
            // get all values from the KV property textareas
            this.KVTextareas.forEach(txtArea => {
                const keyOfChangedTextArea = txtArea.nativeElement.parentElement.innerText.replace(/\s/g, '');
                this.requirements.properties[keyOfChangedTextArea] = txtArea.nativeElement.value;
            });
            newRequirement.properties = {
                kvproperties: this.requirements.properties
            };
            // case that a requirement type with XML properties was chosen in the model and the default XML properties
            // are shown and modified by the user
        } else if (this.requirements.propertyType === 'XML') {
            this.xmlTextareas.forEach(xmlTextArea => {
                this.requirements.properties = xmlTextArea.nativeElement.value;
            });
            newRequirement.properties = {
                any: this.requirements.properties
            };
        }
        // case when there are no requirements on the node template
        if (isNullOrUndefined(this.requirements.requirements)) {
            const requirementsArray: Array<RequirementModel> = [];
            this.requirements.requirements = {
                requirement: requirementsArray
            };
        }
        this.requirements.requirements.requirement.push(newRequirement);
        const newRequirementData = this.requirements.requirements;
        newRequirementData.nodeId = this.requirements.nodeId;
        this.ngRedux.dispatch(this.actions.setRequirement(newRequirementData));
        this.resetRequirements();
        this.requirementsModal.hide();
    }

    /**
     * Auto-completes other requirement relevant values when a requirement name has been selected in the modal
     */
    onChangeReqDefinitionName(reqName: string): void {
        this.entityTypes.requirementTypes.some(req => {
            if (req.name === reqName) {
                // this.requirements.reqId = req.id;
                this.requirements.reqType = req.namespace;
                this.requirements.reqQName = req.qName;
                this.requirements.reqQNameLocalName = new QName(req.qName).localName;
                // check which propertyType is defined by checking with the defined requirement types from the
                // repository if any is defined with at least one element it's a KV property, sets default values if
                // there aren't any in the node template
                if (req.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].any.length > 0) {
                    this.requirements.propertyType = 'KV';
                    this.showDefaultProperties = true;
                    this.requirements.properties = this.setKVProperties(req);
                    return true;
                    // if propertiesDefinition is defined it's a XML property
                } else if (req.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition) {
                    if (req.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element) {
                        this.requirements.propertyType = 'XML';
                        this.showDefaultProperties = true;
                        this.requirements.properties = req.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element;
                    }
                } else {
                    // else no properties
                    this.requirements.propertyType = '';
                    this.showDefaultProperties = false;
                }
                return true;
            }
        });
    }

    /**
     * saves the typed in requirement id from the modal
     */
    onChangeReqId(reqId: string) {
        if (!this.allIds.find(id => id === reqId)) {
            this.requirements.reqId = reqId;
            this.duplicateId = false;
        } else {
            this.duplicateId = true;
        }
    }

    /**
     * Deletes a requirement from the winery store
     */
    deleteRequirement() {
        const requirements = {
            nodeId: this.currentModalData.id,
            requirement: this.currentModalData.requirements.requirement.filter(req => req.id !== this.currentModalData.currentRequirement.id)
        };
        this.ngRedux.dispatch(this.actions.setRequirement(requirements));
        this.resetRequirements();
        this.requirementsModal.hide();
    }

    /**
     * Resets the requirements modal data
     */
    resetRequirements(): void {
        this.requirements.reqId = '';
        this.requirements.oldReqId = '';
        this.requirements.reqDefinitionName = '';
        this.requirements.reqDefinitionNames = [];
        this.requirements.reqType = '';
        this.requirements.reqQName = '';
        this.requirements.reqQNameLocalName = '';
        this.requirements.nodeId = '';
        this.requirements.propertyType = '';
        this.requirements.requirements = null;
        this.requirements.properties = null;
    }

    /**
     * Closes the requirements modal
     */
    closeAndResetRequirements(): void {
        this.requirementsModal.hide();
        this.resetRequirements();
    }

    /**
     * Resets the capabilities modal data
     */
    resetCapabilities(): void {
        this.capabilities.capId = '';
        this.capabilities.oldCapId = '';
        this.capabilities.capDefinitionName = '';
        this.capabilities.capDefinitionNames = [];
        this.capabilities.capType = '';
        this.capabilities.capQNameLocalName = '';
        this.capabilities.capQName = '';
        this.capabilities.nodeId = '';
        this.capabilities.propertyType = '';
        this.capabilities.capabilities = null;
        this.capabilities.properties = null;
    }

    /**
     * Closes the capabilities modal
     */
    closeAndResetCapabilities(): void {
        this.capabilitiesModal.hide();
        this.resetCapabilities();
    }

    /**
     * New nodes can be dragged directly from the palette,
     * adds the node to the internal representation
     * @param event  The html event.
     */
    moveNewNode(event): void {
        const x = (event.clientX - this.newNodePositionOffsetX).toString();
        const y = (event.clientY - this.newNodePositionOffsetY).toString();
        this.allNodeTemplates[this.indexOfNewNode].x = x;
        this.allNodeTemplates[this.indexOfNewNode].y = y;
    }

    /**
     * Repositions the new node and repaints the screen
     * @param $event  The html event.
     */
    positionNewNode(): void {
        setTimeout(() => this.updateSelectedNodes(), 1);
        this.unbindAll();
        this.revalidateContainer();
    }

    /**
     * Gets called if relationships get created, loaded from the server/ a JSON, deleted or updated and calls the
     * correct handler.
     * @param currentRelationships  List of all displayed relationships.
     */
    updateRelationships(currentRelationships: Array<TRelationshipTemplate>): void {
        // workaround for a jsPlumb connection bug, where upon loading node templates without relationships no
        // creation of relationships possible; delete the dummy relationship upon creating a new one
        if (this.newJsPlumbInstance.getAllConnections().length === 2 && this.allRelationshipTemplates.length === 0) {
            this.newJsPlumbInstance.deleteConnection(this.newJsPlumbInstance.getAllConnections()[0]);
        }
        const localRelationshipsCopyLength = this.allRelationshipTemplates.length;
        const storeRelationshipsLength = currentRelationships.length;
        if (storeRelationshipsLength !== localRelationshipsCopyLength) {
            const difference = storeRelationshipsLength - localRelationshipsCopyLength;
            if (difference === 1) {
                this.handleNewRelationship(currentRelationships);
            } else if (difference > 0 || difference < 0) {
                this.allRelationshipTemplates = currentRelationships;
            }
        } else if (storeRelationshipsLength !== 0 && localRelationshipsCopyLength !== 0) {
            this.updateRelName(currentRelationships);
        }
    }

    /**
     * Handler for new relations, adds it to the internal representation
     * @param currentRelationships  List of all displayed relations.
     */
    handleNewRelationship(currentRelationships: Array<TRelationshipTemplate>): void {
        const newRel = currentRelationships[currentRelationships.length - 1];
        this.allRelationshipTemplates.push(newRel);
        this.manageRelationships(newRel);
    }

    /**
     * Implements some checks if name of relation gets updated
     * @param currentRelationships  List of all displayed relations.
     */
    updateRelName(currentRelationships: Array<TRelationshipTemplate>): void {
        this.allRelationshipTemplates.some(rel => {
            const conn = currentRelationships.find(el => el.id === rel.id);
            if (conn) {
                if (rel.name !== conn.name) {
                    rel.name = conn.name;
                    return true;
                }
            }
        });
    }

    /**
     * Handler for the layout buttons.
     * @param currentButtonsState  Representation of all possible buttons.
     */
    setButtonsState(currentButtonsState: ButtonsStateModel): void {
        if (currentButtonsState) {
            this.navbarButtonsState = currentButtonsState;
            this.revalidateContainer();
            const alignmentButtonLayout = this.navbarButtonsState.buttonsState.layoutButton;
            const alignmentButtonAlignH = this.navbarButtonsState.buttonsState.alignHButton;
            const alignmentButtonAlignV = this.navbarButtonsState.buttonsState.alignVButton;
            const importTopologyButton = this.navbarButtonsState.buttonsState.importTopologyButton;
            const splitTopologyButton = this.navbarButtonsState.buttonsState.splitTopologyButton;
            const matchTopologyButton = this.navbarButtonsState.buttonsState.matchTopologyButton;
            let selectedNodes;
            if (alignmentButtonLayout) {
                this.layoutDirective.layoutNodes(this.nodeChildrenArray, this.allRelationshipTemplates);
                this.ngRedux.dispatch(this.topologyRendererActions.executeLayout());
                selectedNodes = false;
            } else if (alignmentButtonAlignH) {
                if (this.selectedNodes.length >= 1) {
                    this.layoutDirective.align(this.nodeChildrenArray, this.selectedNodes, align.Horizontal);
                    selectedNodes = true;
                } else {
                    this.layoutDirective.align(this.nodeChildrenArray, this.allNodeTemplates, align.Horizontal);
                    selectedNodes = false;
                }
                this.ngRedux.dispatch(this.topologyRendererActions.executeAlignH());
            } else if (alignmentButtonAlignV) {
                if (this.selectedNodes.length >= 1) {
                    this.layoutDirective.align(this.nodeChildrenArray, this.selectedNodes, align.Vertical);
                    selectedNodes = true;
                } else {
                    this.layoutDirective.align(this.nodeChildrenArray, this.allNodeTemplates, align.Vertical);
                }
                this.ngRedux.dispatch(this.topologyRendererActions.executeAlignV());
            } else if (importTopologyButton) {
                if (!this.importTopologyData.allTopologyTemplates) {
                    this.importTopologyData.allTopologyTemplates = [];
                    this.backendService.requestAllTopologyTemplates().subscribe(allServiceTemplates => {
                        for (const serviceTemplate of allServiceTemplates) {
                            this.importTopologyData.allTopologyTemplates.push(serviceTemplate);
                        }
                    });
                }
                this.ngRedux.dispatch(this.topologyRendererActions.importTopology());
                this.importTopologyModal.show();
            } else if (splitTopologyButton) {
                this.splitMatchService.splitTopology(this.backendService, this.ngRedux, this.topologyRendererActions, this.errorHandler);
            } else if (matchTopologyButton) {
                this.splitMatchService.matchTopology(this.backendService, this.ngRedux, this.topologyRendererActions, this.errorHandler);
            }
            setTimeout(() => {
                if (selectedNodes === true) {
                    this.updateSelectedNodes();
                } else {
                    this.updateAllNodes();
                }
                this.revalidateContainer();
            }, 1);
        }
    }

    /**
     * Reacts on selection of a topology template in the import topology modal
     */
    onChangeTopologyTemplate(selectedTopologyTemplateId: string): void {
        this.importTopologyData.selectedTopologyTemplateId = selectedTopologyTemplateId;
    }

    /**
     * Closes the import Topology modal
     */
    closeImportTopology(): void {
        this.importTopologyData.selectedTopologyTemplateId = null;
        this.importTopologyData.topologySelected = false;
        this.importTopologyModal.hide();
    }

    /**
     * REST Call to the backend to get the selected topology
     * After a window reload, the topology is added
     */
    importTopology(): void {
        let selectedTopologyTemplate;
        this.importTopologyData.topologySelected = true;
        this.importTopologyData.allTopologyTemplates.some(topologyTemplate => {
            if (topologyTemplate.id === this.importTopologyData.selectedTopologyTemplateId) {
                selectedTopologyTemplate = topologyTemplate;
                return true;
            }
        });
        this.importTopologyService.importTopologyTemplate(selectedTopologyTemplate.qName, this.backendService, this.errorHandler);
        this.importTopologyData.selectedTopologyTemplateId = null;
    }

    /**
     * Revalidates the offsets and other data of the container in the DOM.
     */
    public revalidateContainer(): void {
        setTimeout(() => {
            this.newJsPlumbInstance.revalidate('container');
            this.newJsPlumbInstance.repaintEverything();
        }, 1);
    }

    /**
     * Updates the internal representation of all nodes with the actual dom information.
     */
    updateAllNodes(): void {
        if (this.allNodeTemplates.length > 0 && this.child) {
            for (const nodeTemplate of this.child.nativeElement.children) {
                this.setNewCoordinates(nodeTemplate);
            }
        }
    }

    /**
     * Matches coordinates from the DOM elements with the internal representation.
     * @param nodeTemplate  Node Element (DOM).
     */
    setNewCoordinates(nodeTemplate: any): void {
        let nodeIndex;
        this.allNodeTemplates.some((node, index) => {
            if (node.id === nodeTemplate.firstChild.nextElementSibling.id) {
                nodeIndex = index;
                return true;
            }
        });
        const nodeCoordinates = {
            id: nodeTemplate.firstChild.nextElementSibling.id,
            x: nodeTemplate.firstChild.nextElementSibling.offsetLeft.toString(),
            y: nodeTemplate.firstChild.nextElementSibling.offsetTop.toString()
        };
        this.allNodeTemplates[nodeIndex].x = nodeCoordinates.x;
        this.allNodeTemplates[nodeIndex].y = nodeCoordinates.y;
        this.ngRedux.dispatch(this.actions.updateNodeCoordinates(nodeCoordinates));
    }

    /**
     * Updates the internal representation of the selected nodes with the actual dom information
     */
    updateSelectedNodes(): void {
        if (this.selectedNodes.length > 0 && this.child) {
            for (const nodeTemplate of this.child.nativeElement.children) {
                if (this.selectedNodes.some(node => node.id === nodeTemplate.firstChild.nextElementSibling.id)) {
                    this.setNewCoordinates(nodeTemplate);
                }
            }
        }
    }

    /**
     * Paints new relationships between nodes
     * @param newRelationship
     */
    paintRelationship(newRelationship: TRelationshipTemplate) {
        const allJsPlumbRelationships = this.newJsPlumbInstance.getAllConnections();
        if (!allJsPlumbRelationships.some(rel => rel.id === newRelationship.id)) {
            let labelString = (isNullOrUndefined(newRelationship.state) ? '' : newRelationship.state + '<br>')
                // why not use name -> save the type's id into the name (without management version)
                + newRelationship.name;

            if (labelString.startsWith('con')) {
                // Workaround to support old topology templates with the real name
                labelString = newRelationship.type.substring(newRelationship.type.indexOf('}') + 1);
            }

            const border = isNullOrUndefined(newRelationship.state)
                ? '#fafafa' : VersionUtils.getElementColorByDiffState(newRelationship.state);
            const conn = this.newJsPlumbInstance.connect({
                source: newRelationship.sourceElement.ref,
                target: newRelationship.targetElement.ref,
                overlays: [['Arrow', { width: 15, length: 15, location: 1, id: 'arrow', direction: 1 }],
                    ['Label', {
                        label: labelString,
                        id: 'label',
                        labelStyle: {
                            font: '11px Roboto, sans-serif',
                            color: '#212121',
                            fill: '#efefef',
                            borderStyle: border,
                            borderWidth: 1,
                            padding: '3px'
                        }
                    }]
                ],
            });
            setTimeout(() => this.handleRelSideBar(conn, newRelationship), 1);

            if (!isNullOrUndefined(newRelationship.state)) {
                setTimeout(() => {
                    conn.addType(newRelationship.state.toString().toLowerCase());
                    this.revalidateContainer();
                }, 1);
            }
        }
    }

    /**
     * Resets and (re)paints all jsplumb elements
     * @param newRelationship
     */
    manageRelationships(newRelationship: TRelationshipTemplate): void {
        setTimeout(() => this.paintRelationship(newRelationship), 1);
        this.resetDragSource('');
        this.revalidateContainer();
    }

    /**
     * Resets JSPlumb drag source which marks the area where a connection can be dragged from
     * @param nodeId
     */
    resetDragSource(nodeId: string): void {
        if (this.dragSourceInfos) {
            if (this.newJsPlumbInstance.isTarget(this.targetNodes)) {
                this.newJsPlumbInstance.unmakeTarget(this.targetNodes);
            }
            this.targetNodes = [];
            if (this.dragSourceInfos.nodeId !== nodeId) {
                this.newJsPlumbInstance.removeAllEndpoints(this.dragSourceInfos.dragSource);
                if (this.dragSourceInfos.dragSource) {
                    if (this.newJsPlumbInstance.isSource(this.dragSourceInfos.dragSource)) {
                        this.newJsPlumbInstance.unmakeSource(this.dragSourceInfos.dragSource);
                    }
                }
                const indexOfNode = this.nodeChildrenIdArray.indexOf(this.dragSourceInfos.nodeId);
                if (this.nodeChildrenArray[indexOfNode]) {
                    this.nodeChildrenArray[indexOfNode].connectorEndpointVisible = false;
                    this.revalidateContainer();
                }
                this.dragSourceActive = false;
                this.dragSourceInfos = null;
            }
        }
    }

    /**
     * Upon clicking on a node template the connector endpoint/area from which connections can be dragged, is toggled
     * and the connector endpoints from the other node templates are closed, so only the connector endpoint from the
     * node template which was clicked on is visible
     * @param nodeId
     */
    toggleClosedEndpoint(nodeId: string): void {
        const node = this.nodeChildrenArray.find((nodeTemplate => nodeTemplate.nodeTemplate.id === nodeId));
        node.connectorEndpointVisible = !node.connectorEndpointVisible;
        if (node.connectorEndpointVisible === true) {
            this.dragSourceActive = false;
            this.resetDragSource(nodeId);
            this.nodeChildrenArray.some(currentNode => {
                if (currentNode.nodeTemplate.id !== nodeId) {
                    if (currentNode.connectorEndpointVisible === true) {
                        currentNode.connectorEndpointVisible = false;
                        return true;
                    }
                }
            });
        }
    }

    /**
     * Sets drag source which marks the area where a connection can be dragged from and binds to the connections
     * listener
     * @param dragSourceInfo
     */
    setDragSource(dragSourceInfo: DragSource): void {
        const nodeArrayLength = this.allNodeTemplates.length;
        const currentNodeIsSource = this.newJsPlumbInstance.isSource(dragSourceInfo.dragSource);
        if (!this.dragSourceActive && !currentNodeIsSource && nodeArrayLength > 1) {
            this.newJsPlumbInstance.makeSource(dragSourceInfo.dragSource, {
                connectorOverlays: [
                    ['Arrow', { location: 1 }],
                ],
            });
            this.dragSourceInfos = dragSourceInfo;
            this.targetNodes = this.allNodesIds.filter(nodeId => nodeId !== this.dragSourceInfos.nodeId);
            if (this.targetNodes.length > 0) {
                this.newJsPlumbInstance.makeTarget(this.targetNodes);
                this.dragSourceActive = true;
                this.bindConnection();
            }
        }
    }

    /**
     * Handler for the DEL-Key - removes a node and resets everything associated with that deleted node
     * @param event Keyboard event.
     */
    @HostListener('document:keydown.delete', ['$event'])
    handleDeleteKeyEvent(event: KeyboardEvent) {
        this.unbindConnection();
        // if name, min or max instances has changed, do not delete the node.
        if (this.selectedNodes.length > 0) {
            let selectedNodeSideBarVisible = false;
            this.nodeChildrenArray.forEach(node => {
                if (node.makeSelectionVisible === true) {
                    if (!selectedNodeSideBarVisible) {
                        this.hideSidebar();
                    }
                    selectedNodeSideBarVisible = true;
                    this.newJsPlumbInstance.deleteConnectionsForElement(node.nodeTemplate.id);
                    this.newJsPlumbInstance.removeAllEndpoints(node.nodeTemplate.id);
                    this.newJsPlumbInstance.removeFromAllPosses(node.nodeTemplate.id);
                    if (node.connectorEndpointVisible === true) {
                        if (this.newJsPlumbInstance.isSource(node.dragSource)) {
                            this.newJsPlumbInstance.unmakeSource(node.dragSource);
                        }
                    }
                    this.ngRedux.dispatch(this.actions.deleteNodeTemplate(node.nodeTemplate.id));
                }
            });
            this.selectedNodes.length = 0;
        } else {
            if (this.newJsPlumbInstance.getAllConnections().length > 0) {
                for (const con of this.newJsPlumbInstance.getAllConnections()) {
                    if (con.hasType('marked')) {
                        this.ngRedux.dispatch(this.actions.deleteRelationshipTemplate(con.id));
                        this.newJsPlumbInstance.deleteConnection(con);
                        this.hideSidebar();
                    }
                }
            }
        }

    }

    /**
     * Removes the selected Nodes from JSPlumb and internally.
     */
    clearSelectedNodes(): void {
        if (this.selectedNodes.length > 0) {
            this.nodeChildrenArray.forEach(node => {
                if (this.selectedNodes.find(selectedNode => selectedNode.id === node.nodeTemplate.id)) {
                    node.makeSelectionVisible = false;
                }
            });
            this.newJsPlumbInstance.removeFromAllPosses(this.selectedNodes.map(node => node.id));
            this.selectedNodes = [];
        }
    }

    /**
     * Creates a new selection box and removes the old selections.
     * @param $event
     */
    showSelectionRange($event: any) {
        this.gridTemplate.crosshair = true;
        this.ngRedux.dispatch(this.actions.sendPaletteOpened(false));
        this.hideSidebar();
        this.clearSelectedNodes();
        this.nodeChildrenArray.forEach(node => node.makeSelectionVisible = false);
        this.gridTemplate.pageX = $event.pageX;
        this.gridTemplate.pageY = $event.pageY;
        this.gridTemplate.initialW = $event.pageX;
        this.gridTemplate.initialH = $event.pageY;
        this.zone.run(() => {
            this.unbindMouseActions.push(this.renderer.listen(this.eref.nativeElement, 'mousemove', (event) =>
                this.openSelector(event)));
            this.unbindMouseActions.push(this.renderer.listen(this.eref.nativeElement, 'mouseup', (event) =>
                this.selectElements(event)));
        });
    }

    /**
     * Opens the selection box
     * @param $event
     */
    openSelector($event: any) {
        if (this.longPressing) {
            if (this.hideNavBarAndPalette === false) {
                this.ngRedux.dispatch(this.actions.hideNavBarAndPalette(true));
            }
            this.hideNavBarAndPalette = true;
        }
        this.gridTemplate.marginLeft = 0;
        this.gridTemplate.selectionActive = true;
        this.gridTemplate.selectionWidth = Math.abs(this.gridTemplate.initialW - $event.pageX);
        this.gridTemplate.selectionHeight = Math.abs(this.gridTemplate.initialH - $event.pageY);
        if ($event.pageX <= this.gridTemplate.initialW && $event.pageY >= this.gridTemplate.initialH) {
            this.gridTemplate.pageX = $event.pageX;
        } else if ($event.pageY <= this.gridTemplate.initialH && $event.pageX >= this.gridTemplate.initialW) {
            this.gridTemplate.pageY = $event.pageY;
        } else if ($event.pageY < this.gridTemplate.initialH && $event.pageX < this.gridTemplate.initialW) {
            this.gridTemplate.pageX = $event.pageX;
            this.gridTemplate.pageY = $event.pageY;
        }
    }

    /**
     * Selects the elements that are within the selection box.
     * @param $event
     */
    selectElements($event: any) {
        if (this.hideNavBarAndPalette) {
            this.hideNavBarAndPalette = false;
            this.ngRedux.dispatch(this.actions.hideNavBarAndPalette(false));
        }
        this.gridTemplate.marginLeft = 30;
        const aElem = this.selection.nativeElement;
        for (const node of this.child.nativeElement.children) {
            const bElem = node.firstChild;
            const result = this.isObjectInSelection(aElem, bElem);
            if (result) {
                this.enhanceDragSelection(node.firstChild.nextElementSibling.id);
            }
        }
        this.unbindAll();
        this.gridTemplate.selectionActive = false;
        this.gridTemplate.selectionWidth = 0;
        this.gridTemplate.selectionHeight = 0;
        this.gridTemplate.crosshair = false;
        // This is just a hack for firefox, the same code is in the click listener
        if (this.eref.nativeElement.contains($event.target) && this.longPress === false) {
            this.newJsPlumbInstance.removeFromAllPosses(this.selectedNodes.map(node => node.id));
            this.clearSelectedNodes();
            if ($event.clientX > 200) {
                this.ngRedux.dispatch(this.actions.sendPaletteOpened(false));
            }
        }
    }

    /**
     * If the window gets scrolled, the HTML component where nodes can be
     * placed on gets extended.
     * @param $event
     */
    @HostListener('window:scroll', ['event'])
    adjustGrid($event) {
        this.gridTemplate.gridDimension = window.innerWidth;
        this.scrollOffset = window.scrollY;
    }

    /**
     * Hides the Sidebar on the right.
     */
    hideSidebar() {
        this.ngRedux.dispatch(this.actions.openSidebar({
            sidebarContents: {
                sidebarVisible: false,
                nodeClicked: false,
                id: '',
                nameTextFieldValue: '',
                type: ''
            }
        }));
    }

    /**
     * Handler for Keyboard actions
     * @param focusNodeData
     */
    handleNodeClickedActions(focusNodeData: any): void {
        if (focusNodeData.ctrlKey) {
            this.handleCtrlKeyNodePress(focusNodeData.id);
        } else {
            this.handleNodePressActions(focusNodeData.id);
        }
    }

    /**
     * Checks if array 'Nodes' contains 'id'.
     * @param Nodes
     * @param id
     * @returns Boolean True if 'Nodes' contains 'id'.
     */
    arrayContainsNode(nodes: any[], id: string): boolean {
        if (nodes !== null && nodes.length > 0) {
            return nodes.some(node => node.id === id);
        }
        return false;
    }

    /**
     * Removes the drag source from JSPlumb which marks the area where connections can be dragged from
     */
    unbindDragSource(): void {
        if (this.dragSourceInfos) {
            this.newJsPlumbInstance.removeAllEndpoints(this.dragSourceInfos.dragSource);
            if (this.dragSourceInfos.dragSource) {
                if (this.newJsPlumbInstance.isSource(this.dragSourceInfos.dragSource)) {
                    this.newJsPlumbInstance.unmakeSource(this.dragSourceInfos.dragSource);
                }
            }
            this.dragSourceActive = false;
        }
    }

    /**
     * Unbinds the JsPlumb connection listener which triggers every time a relationship is dragged from the dragSource
     */
    unbindConnection(): void {
        if (this.jsPlumbBindConnection === true) {
            this.newJsPlumbInstance.unbind('connection');
            this.jsPlumbBindConnection = false;
            this.unbindDragSource();
        }
    }

    /**
     * Removes the marked-styling from all connections.
     */
    unmarkConnections() {
        this.newJsPlumbInstance.select().removeType('marked');
    }

    /**
     * Registers relationship (connection) types in JSPlumb (Color, strokewidth etc.)
     * @param relType
     */
    assignRelTypes(relType: EntityType): void {
        this.newJsPlumbInstance.registerConnectionType(
            relType.qName, {
                paintStyle: {
                    stroke: relType.color,
                    strokeWidth: 2
                },
                hoverPaintStyle: { stroke: relType.color, strokeWidth: 5 }
            });
    }

    /**
     * Lifecycle hook
     */
    ngOnInit() {
        this.layoutDirective.setJsPlumbInstance(this.newJsPlumbInstance);
        this.newJsPlumbInstance.registerConnectionTypes({
            marked: {
                paintStyle: {
                    strokeWidth: 5
                }
            },
            added: {
                paintStyle: {
                    stroke: VersionUtils.getElementColorByDiffState(DifferenceStates.ADDED)
                }
            },
            removed: {
                paintStyle: {
                    stroke: VersionUtils.getElementColorByDiffState(DifferenceStates.REMOVED)
                }
            },
            changed: {
                paintStyle: {
                    stroke: VersionUtils.getElementColorByDiffState(DifferenceStates.CHANGED)
                }
            }
        });
        this.differ = this.differs.find([]).create(null);
    }

    /*
    isFieldValid(field: string) {
        return !this.form.get(field).valid && this.form.get(field).touched;
    }

    displayFieldCss(field: string) {
        return {
            'has-error': this.isFieldValid(field),
            'has-feedback': this.isFieldValid(field)
        };
    }
*/

    /**
     * Angular lifecycle event.
     */
    ngDoCheck() {
        const relationshipTypesChanges = this.differ.diff(this.relationshipTypes);
        // TODO: instead of fetching all relationship visuals one by one, do it similar to nodeTypes -> this check will
        // be obsolete
        if (relationshipTypesChanges && !this.diffMode) {
            relationshipTypesChanges.forEachAddedItem(r => this.assignRelTypes(r.currentValue));
        }
    }

    /**
     * sets the selectedRelationshipType emitted from a node and replaces spaces from it.
     * @param currentType
     */
    setSelectedRelationshipType(currentType: EntityType) {
        this.selectedRelationshipType = currentType;
    }

    /**
     * Removes an element from JSPlumb.
     * @param id
     */
    removeElement(id: string) {
        this.newJsPlumbInstance.remove(id);
        this.revalidateContainer();
    }

    /**
     * Tells JSPlumb to make a node draggable with the node id emitted from the corresponding node
     * @param nodeId
     */
    activateNewNode(nodeId: string): void {
        this.newJsPlumbInstance.draggable(nodeId);
        if (this.paletteOpened) {
            this.bindNewNode();
        }
    }

    /**
     * Removes the dragSource of a node which marks the area where a connection can be dragged from
     */
    removeDragSource(): void {
        this.nodeChildrenArray.some(node => {
            if (node.dragSource) {
                if (this.newJsPlumbInstance.isSource(node.dragSource)) {
                    this.newJsPlumbInstance.unmakeSource(node.dragSource);
                    node.connectorEndpointVisible = false;
                    return true;
                }
                node.connectorEndpointVisible = false;
            }
        });
    }

    /**
     * Tracks the time of mousedown, this is necessary
     * to decide whether a drag or a click is initiated
     * and resets dragSource, clears selectedNodes and unbinds the connection listener.
     * @param $event  The HTML event.
     */
    trackTimeOfMouseDown(): void {
        this.newJsPlumbInstance.select().removeType('marked');
        this.revalidateContainer();
        this.removeDragSource();
        this.clearSelectedNodes();
        this.unbindConnection();
        this.startTime = new Date().getTime();
    }

    /**
     * Tracks the time of mouseup, this is necessary
     * to decide whether a drag or a click is initiated.
     * @param $event  The HTML event.
     */
    trackTimeOfMouseUp(): void {
        this.endTime = new Date().getTime();
        this.determineDragOrClick();
    }

    /**
     * Lifecycle event
     */
    ngAfterViewInit() {
        this.nodeChildrenArray = this.nodeComponentChildren.toArray();
        this.nodeChildrenIdArray = this.nodeChildrenArray.map(node => node.nodeTemplate.id);
        this.nodeComponentChildren.changes.subscribe(children => {
            this.nodeChildrenArray = children.toArray();
            this.nodeChildrenIdArray = this.nodeChildrenArray.map(node => node.nodeTemplate.id);
        });
        if (this.allRelationshipTemplates.length > 0 && this.nodeChildrenArray.length > 1) {
            this.allRelationshipTemplates.forEach(rel => {
                setTimeout(() => this.manageRelationships(rel), 1);
            });
            // workaround for a jsPlumb connection bug, where upon loading node templates without relationships
            // no creation of relationships possible
        } else if (this.allRelationshipTemplates.length === 0) {
            const con = this.newJsPlumbInstance.connect({ source: 'dummy1', target: 'dummy2' });
            con.setVisible(false);
        }
        if (this.diffMode) {
            this.layoutTopology();
        }
    }

    /**
     * Lifecycle event
     */
    ngOnDestroy() {
        this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }

    /**
     * Handler for new nodes, binds them on mousemove and mouseup events
     * @param currentNodes  List of all displayed nodes.
     */
    private handleNewNode(currentNodes: Array<TNodeTemplate>): void {
        this.unbindConnection();
        this.clearSelectedNodes();
        if (this.newNode) {
            this.resetDragSource(this.newNode.id);
        }
        this.newNode = currentNodes[currentNodes.length - 1];
        this.allNodeTemplates.push(this.newNode);
        this.allNodeTemplates.some((node, index) => {
            if (node.id === this.newNode.id) {
                this.indexOfNewNode = index;
                return true;
            }
        });
    }

    /**
     * Handler for deleted nodes, removes the node from the internal representation
     * @param currentNodes  List of all displayed nodes.
     */
    private handleDeletedNodes(currentNodes: Array<TNodeTemplate>): void {
        // let deletedNode;
        this.allNodeTemplates.forEach(node => {
            if (!currentNodes.some(n => n.id === node.id)) {
                // deletedNode = node.id;
                let indexOfNode;
                this.allNodeTemplates.some((nodeTemplate, index) => {
                    if (nodeTemplate.id === node.id) {
                        indexOfNode = index;
                        return true;
                    }
                });
                this.allNodeTemplates.splice(indexOfNode, 1);
            }
        });
    }

    /**
     * Gets called if node is updated, implements some checks.
     * @param currentNodes  List of all displayed nodes.
     */
    private updateNodeAttributes(currentNodes: Array<TNodeTemplate>): void {
        this.allNodeTemplates.some(nodeTemplate => {
            const node = currentNodes.find(el => el.id === nodeTemplate.id);
            if (node) {
                if (nodeTemplate.name !== node.name) {
                    const nodeId = this.nodeChildrenIdArray.indexOf(nodeTemplate.id);
                    this.nodeChildrenArray[nodeId].nodeTemplate.name = node.name;
                    this.nodeChildrenArray[nodeId].flash('name');
                    nodeTemplate.name = node.name;
                    return true;
                } else if (nodeTemplate.minInstances !== node.minInstances) {
                    const nodeId = this.nodeChildrenIdArray.indexOf(nodeTemplate.id);
                    nodeTemplate.minInstances = node.minInstances;
                    this.nodeChildrenArray[nodeId].flash('min');
                    return true;
                } else if (nodeTemplate.maxInstances !== node.maxInstances) {
                    const nodeId = this.nodeChildrenIdArray.indexOf(nodeTemplate.id);
                    nodeTemplate.maxInstances = node.maxInstances;
                    this.nodeChildrenArray[nodeId].flash('max');
                    return true;
                } else if (nodeTemplate.properties !== node.properties) {
                    nodeTemplate.properties = node.properties;
                    return true;
                } else if (nodeTemplate.capabilities !== node.capabilities) {
                    nodeTemplate.capabilities = node.capabilities;
                    return true;
                } else if (nodeTemplate.requirements !== node.requirements) {
                    nodeTemplate.requirements = node.requirements;
                    return true;
                } else if (nodeTemplate.deploymentArtifacts !== node.deploymentArtifacts) {
                    nodeTemplate.deploymentArtifacts = node.deploymentArtifacts;
                    return true;
                } else if (nodeTemplate.policies !== node.policies) {
                    nodeTemplate.policies = node.policies;
                    return true;
                } else if (nodeTemplate.otherAttributes !== node.otherAttributes) {
                    nodeTemplate.otherAttributes = node.otherAttributes;
                    return true;
                }
            }
        });
    }

    /**
     * Sets the sidebar up for a new node, makes it visible, sets a type and adds a click listener to this relationship
     * @param conn            The JSPlumb connection
     * @param newRelationship The new relationship internally
     */
    private handleRelSideBar(conn: any, newRelationship: TRelationshipTemplate): void {
        conn.id = newRelationship.id;
        conn.setType(newRelationship.type);
        const me = this;
        conn.bind('click', rel => {
            this.clearSelectedNodes();
            this.newJsPlumbInstance.select().removeType('marked');
            const currentRel = me.allRelationshipTemplates.find(con => con.id === rel.id);
            if (currentRel) {
                me.ngRedux.dispatch(this.actions.openSidebar({
                    sidebarContents: {
                        sidebarVisible: true,
                        nodeClicked: false,
                        id: currentRel.id,
                        nameTextFieldValue: currentRel.name,
                        type: currentRel.type
                    }
                }));
                conn.addType('marked');
            }
        });
        this.revalidateContainer();
    }

    /**
     * Unbind all mouse actions
     */
    private unbindAll(): void {
        this.unbindMouseActions.forEach(unbindMouseAction => unbindMouseAction());
    }

    /**
     * Checks if DOM element is completely in the selection box.
     * @param selectionArea The selection box
     * @param object        The DOM element.
     */
    private isObjectInSelection(selectionArea, object): boolean {
        const selectionRect = selectionArea.getBoundingClientRect();
        return (
            ((selectionRect.top + selectionRect.height) > (object.nextElementSibling.offsetTop +
                object.nextElementSibling.offsetHeight - this.scrollOffset)) &&
            (selectionRect.top < (object.nextElementSibling.offsetTop - this.scrollOffset)) &&
            ((selectionRect.left + selectionArea.getBoundingClientRect().width) > (object.nextElementSibling.offsetLeft +
                object.nextElementSibling.offsetWidth)) &&
            (selectionRect.left < (object.nextElementSibling.offsetLeft))
        );
    }

    /**
     * Handler for the CTRL Key, adds or removes
     * elements to the current selection
     * @param nodeId
     */
    private handleCtrlKeyNodePress(nodeId: string): void {
        if (this.jsPlumbBindConnection === true) {
            this.unbindConnection();
        }
        if (!this.arrayContainsNode(this.selectedNodes, nodeId)) {
            this.enhanceDragSelection(nodeId);
            this.nodeChildrenArray.forEach(node => {
                let nodeIndex;
                this.selectedNodes.some((selectedNode, index) => {
                    if (selectedNode.id === node.nodeTemplate.id) {
                        nodeIndex = index;
                        return true;
                    }
                });
                if (this.selectedNodes[nodeIndex] === undefined) {
                    node.makeSelectionVisible = false;
                    this.unbindConnection();
                }
                if (node.connectorEndpointVisible === true) {
                    node.connectorEndpointVisible = false;
                    this.resetDragSource('reset previous drag source');
                }
            });
        } else {
            this.newJsPlumbInstance.removeFromAllPosses(nodeId);
            let nodeIndex;
            this.nodeChildrenArray.some((node, index) => {
                if (node.nodeTemplate.id === nodeId) {
                    nodeIndex = index;
                    return true;
                }
            });
            this.nodeChildrenArray[nodeIndex].makeSelectionVisible = false;
            let selectedNodeIndex;
            this.selectedNodes.some((node, index) => {
                if (node.id === nodeId) {
                    selectedNodeIndex = index;
                    return true;
                }
            });
            this.selectedNodes.splice(selectedNodeIndex, 1);
        }
    }

    /**
     * Clickhandler for Nodes, selects the clicked node.
     * @param nodeId
     */
    private handleNodePressActions(nodeId: string): void {
        this.nodeChildrenArray.forEach(node => {
            if (node.nodeTemplate.id === nodeId) {
                node.makeSelectionVisible = true;
            } else if (!this.arrayContainsNode(this.selectedNodes, node.nodeTemplate.id)) {
                node.makeSelectionVisible = false;
                this.resetDragSource(nodeId);
            }
        });
        this.unbindConnection();
        if (this.selectedNodes.length === 1 && this.selectedNodes.find(node => node.id !== nodeId)) {
            this.clearSelectedNodes();
        }
        if (this.selectedNodes.length === 0) {
            this.enhanceDragSelection(nodeId);
        }
        if (!this.arrayContainsNode(this.selectedNodes, nodeId)) {
            this.clearSelectedNodes();
        }
    }

    /**
     * Enhances the selection internally and for JSPlumb.
     * @param nodeId
     */
    private enhanceDragSelection(nodeId: string) {
        if (!this.arrayContainsNode(this.selectedNodes, nodeId)) {
            this.selectedNodes.push(this.getNodeByID(this.allNodeTemplates, nodeId));
            this.newJsPlumbInstance.addToPosse(nodeId, 'dragSelection');
            this.nodeChildrenArray.forEach(node => {
                if (this.selectedNodes.find(selectedNode => selectedNode.id === node.nodeTemplate.id)) {
                    if (node.makeSelectionVisible === false) {
                        node.makeSelectionVisible = true;
                    }
                }
            });
        }
    }

    /**
     * Getter for Node by ID
     * @param Nodes
     * @param id
     */
    private getNodeByID(nodes: Array<TNodeTemplate>, id: string): TNodeTemplate {
        if (nodes !== null && nodes.length > 0) {
            for (const node of nodes) {
                if (node.id === id) {
                    return node;
                }
            }
        }
    }

    /**
     * Binds to the JsPlumb connections listener which triggers every time a relationship is dragged from the dragSource
     * and pushes the new connection to the redux store
     */
    private bindConnection(): void {
        if (this.jsPlumbBindConnection === false) {
            this.jsPlumbBindConnection = true;
            this.newJsPlumbInstance.bind('connection', info => {
                const sourceElement = info.sourceId.substring(0, info.sourceId.indexOf('_E'));
                info.sourceId = sourceElement;
                const currentTypeValid = this.entityTypes.relationshipTypes.some(relType => relType.qName === this.selectedRelationshipType.qName);
                const currentSourceIdValid = this.allNodeTemplates.some(node => node.id === sourceElement);
                if (sourceElement && currentTypeValid && currentSourceIdValid) {
                    const targetElement = info.targetId;
                    let lastRelId = 'con_0';
                    if (this.allRelationshipTemplates.length > 0) {
                        lastRelId = this.allRelationshipTemplates[this.allRelationshipTemplates.length - 1].id;
                    }
                    const newRelCount = parseInt(lastRelId.substring(lastRelId.indexOf('_') + 1), 10) + 1;
                    const relationshipId = 'con_' + newRelCount.toString();
                    const relTypeExists = this.allRelationshipTemplates.some(rel => rel.id === relationshipId);
                    if (relTypeExists === false && sourceElement !== targetElement) {
                        const newRelationship = new TRelationshipTemplate(
                            { ref: sourceElement },
                            { ref: targetElement },
                            relationshipId,
                            relationshipId,
                            this.selectedRelationshipType.qName
                        );
                        this.ngRedux.dispatch(this.actions.saveRelationship(newRelationship));
                    }
                }
                this.unbindConnection();
                this.revalidateContainer();
            });
        }
    }

    /**
     * Handles the new node by binding to mouse move and mouse up actions
     */
    private bindNewNode(): void {
        setTimeout(() => this.handleNodePressActions(this.newNode.id), 1);
        this.zone.run(() => {
            this.unbindMouseActions.push(this.renderer.listen(this.eref.nativeElement, 'mousemove',
                (event) => this.moveNewNode(event)));
            this.unbindMouseActions.push(this.renderer.listen(this.eref.nativeElement, 'mouseup',
                ($event) => this.positionNewNode()));
        });
    }

    /**
     * Checks whether it was a drag or a click.
     */
    private determineDragOrClick(): void {
        if ((this.endTime - this.startTime) < this.draggingThreshold) {
            this.longPress = false;
        } else if (this.endTime - this.startTime >= this.draggingThreshold) {
            this.longPress = true;
        }
    }

    private layoutTopology() {
        this.layoutDirective.layoutNodes(this.nodeChildrenArray, this.allRelationshipTemplates);
        this.ngRedux.dispatch(this.topologyRendererActions.executeLayout());
    }
}
