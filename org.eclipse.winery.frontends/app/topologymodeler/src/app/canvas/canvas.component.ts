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
    AfterViewInit, Component, ElementRef, HostListener, Input, KeyValueDiffers, NgZone, OnChanges, OnDestroy, OnInit, QueryList, Renderer2, SimpleChanges,
    ViewChild, ViewChildren
} from '@angular/core';
import { JsPlumbService } from '../services/jsPlumb.service';
import { EntityType, TNodeTemplate, TRelationshipTemplate, VisualEntityType } from '../models/ttopology-template';
import { LayoutDirective } from '../layout/layout.directive';
import { WineryActions } from '../redux/actions/winery.actions';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { NodeComponent } from '../node/node.component';
import { Hotkey, HotkeysService } from 'angular2-hotkeys';
import { ModalDirective } from 'ngx-bootstrap';
import { GridTemplate } from '../models/gridTemplate';
import { Subscription } from 'rxjs';
import { CapabilitiesModalData } from '../models/capabilitiesModalData';
import { RequirementsModalData } from '../models/requirementsModalData';
import { NodeIdAndFocusModel } from '../models/nodeIdAndFocusModel';
import { ToggleModalDataModel } from '../models/toggleModalDataModel';
import { ToastrService } from 'ngx-toastr';
import { BackendService } from '../services/backend.service';
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
import { SplitMatchTopologyService } from '../services/split-match-topology.service';
import { PlaceComponentsService } from '../services/placement.service';
import { DifferenceStates, VersionUtils } from '../models/ToscaDiff';
import { ErrorHandlerService } from '../services/error-handler.service';
import { DragSource } from '../models/DragSource';
import { TopologyRendererState } from '../redux/reducers/topologyRenderer.reducer';
import { ThreatModelingModalData } from '../models/threatModelingModalData';
import { ThreatCreation } from '../models/threatCreation';
import { TopologyTemplateUtil } from '../models/topologyTemplateUtil';
import { ReqCapRelationshipService } from '../services/req-cap-relationship.service';
import { TPolicy } from '../models/policiesModalData';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { RequirementDefinitionModel } from '../models/requirementDefinitonModel';
import { CapabilityDefinitionModel } from '../models/capabilityDefinitionModel';
import { WineryRowData } from '../../../../tosca-management/src/app/wineryTableModule/wineryTable.component';
import { InheritanceUtils } from '../models/InheritanceUtils';

@Component({
    selector: 'winery-canvas',
    providers: [LayoutDirective],
    templateUrl: './canvas.component.html',
    styleUrls: ['./canvas.component.css']
})
export class CanvasComponent implements OnInit, OnDestroy, OnChanges, AfterViewInit {

    @ViewChildren(NodeComponent) nodeComponentChildren: QueryList<NodeComponent>;
    @ViewChildren('KVTextareas') KVTextareas: QueryList<any>;
    @ViewChildren('XMLTextareas') xmlTextareas: QueryList<any>;
    @ViewChildren('YamlPolicyProperties') yamlPolicyProperties: QueryList<any>;
    @ViewChild('nodes') child: ElementRef;
    @ViewChild('selection') selection: ElementRef;
    @ViewChild('capabilitiesModal') capabilitiesModal: ModalDirective;
    @ViewChild('requirementsModal') requirementsModal: ModalDirective;
    @ViewChild('importTopologyModal') importTopologyModal: ModalDirective;
    @ViewChild('threatModelingModal') threatModelingModal: ModalDirective;
    @ViewChild('manageYamlPoliciesModal') manageYamlPoliciesModal: ModalDirective;
    @ViewChild('addYamlPolicyModal') addYamlPolicyModal: ModalDirective;
    @Input() readonly: boolean;
    @Input() entityTypes: EntityTypesModel;
    @Input() diffMode = false;
    @Input() sidebarDeleteButtonClickEvent: any;

    readonly draggingThreshold = 300;
    readonly newNodePositionOffsetX = 108;
    readonly newNodePositionOffsetY = 30;

    allNodeTemplates: Array<TNodeTemplate> = [];
    allRelationshipTemplates: Array<TRelationshipTemplate> = [];
    topologyRendererState: TopologyRendererState;
    selectedNodes: Array<TNodeTemplate> = [];
    // current data emitted from a node
    currentModalData: any;
    dragSourceActive = false;
    event: any;
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
    threatModelingData: ThreatModelingModalData;

    // threatmodeling accordion state
    threatModalTab = 'create';

    indexOfNewNode: number;
    targetNodes: Array<string> = [];

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

    // Manage YAML Policies Modal
    selectedNewPolicyType: string;
    yamlPoliciesColumns = [
        { title: 'Name', name: 'name' },
        { title: 'Type', name: 'typeHref' }];
    selectedYamlPolicy: TPolicy;
    /**
     * Used to change the policy type fields into clickable <a> elements.
     * Must be populated on every show of the modal!
     */
    copyOfYamlPolicies: {
        name: string,
        policyType: string,
        typeHref: string,
        properties?: any,
        targets?: string[]
    }[];

    constructor(private jsPlumbService: JsPlumbService,
                private eref: ElementRef,
                private layoutDirective: LayoutDirective,
                private ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                private topologyRendererActions: TopologyRendererActions,
                private zone: NgZone,
                private hotkeysService: HotkeysService,
                private renderer: Renderer2,
                private alert: ToastrService,
                private differs: KeyValueDiffers,
                private backendService: BackendService,
                private importTopologyService: ImportTopologyService,
                private existsService: ExistsService,
                private splitMatchService: SplitMatchTopologyService,
                private placementService: PlaceComponentsService,
                private errorHandler: ErrorHandlerService,
                private reqCapRelationshipService: ReqCapRelationshipService,
                private notify: ToastrService,
                private configuration: WineryRepositoryConfigurationService) {
        this.newJsPlumbInstance = this.jsPlumbService.getJsPlumbInstance();
        this.newJsPlumbInstance.setContainer('container');

        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.currentJsonTopology.policies)
            .subscribe(policies => this.handleUpdatedYamlPolicies(policies)));
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.currentJsonTopology.nodeTemplates)
            .subscribe(currentNodes => this.updateNodes(currentNodes)));
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.currentJsonTopology.relationshipTemplates)
            .subscribe(currentRelationships => this.updateRelationships(currentRelationships)));
        this.subscriptions.push(this.ngRedux.select(state => state.topologyRendererState)
            .subscribe(currentButtonsState => this.setRendererState(currentButtonsState)));
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.currentNodeData)
            .subscribe(currentNodeData => this.toggleMarkNode(currentNodeData)));
        this.gridTemplate = new GridTemplate(100, false, false, 30);
        this.subscriptions.push(this.ngRedux.select(state => state.wineryState.currentPaletteOpenedState)
            .subscribe(currentPaletteOpened => this.setPaletteState(currentPaletteOpened)));
        this.hotkeysService.add(new Hotkey('mod+a', (event: KeyboardEvent): boolean => {
            event.stopPropagation();
            this.allNodeTemplates.forEach(node => this.enhanceDragSelection(node.id));
            return false; // Prevent bubbling
        }, undefined, 'Select all Node Templates'));
        this.hotkeysService.add(new Hotkey('del', (event: KeyboardEvent): boolean => {
            this.handleDeleteKeyEvent();
            return false;
        }, undefined, 'Delete an element.'));
        this.capabilities = new CapabilitiesModalData();
        this.requirements = new RequirementsModalData();
        this.importTopologyData = new ImportTopologyModalData();
        this.threatModelingData = new ThreatModelingModalData();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes) {
            const buttonClickEvent = changes.sidebarDeleteButtonClickEvent;
            this.handleDeleteKeyEvent();
        }
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
    onMouseDown(event: MouseEvent) {
        // don't do right/middle clicks
        if (event.button === 0) {
            this.longPressing = false;
            setTimeout(() => this.longPressing = true, 250);
        }
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
        this.paletteOpened = currentPaletteOpened;
        if (this.paletteOpened) {
            this.gridTemplate.marginLeft = 300;
        } else {
            this.gridTemplate.marginLeft = 30;
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
                if (this.configuration.isYaml()) {
                    this.modalData.modalTitle = 'Artifact';
                } else {
                    this.modalData.modalTitle = 'Deployment Artifact';
                }
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
                                        this.requirements.properties = InheritanceUtils.getKVProperties(reqType);
                                        this.setDefaultReqKVProperties();
                                        return true;
                                    }
                                } else {
                                    this.requirements.properties = InheritanceUtils.getKVProperties(reqType);
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
                                            this.setDefaultReqXMLProperties();
                                            return true;
                                        }
                                    } else {
                                        this.requirements.properties = defaultXML;
                                        this.setDefaultReqXMLProperties();
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
                        const data = InheritanceUtils.getEffectiveRequirementDefinitionsOfNodeType(currentNodeData.type, this.entityTypes);
                        this.requirements.reqDefinitionNames = [];
                        this.requirements.reqDefinitionName = '';

                        for (const reqType of data) {
                            const qNameOfType = new QName(reqType.requirementType);
                            this.requirements.reqDefinitionNames.push(qNameOfType.localName);
                        }
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
                                        this.capabilities.properties = InheritanceUtils.getKVProperties(capType);
                                        this.setDefaultCapKVProperties();
                                        return true;
                                    }
                                } else {
                                    this.capabilities.properties = InheritanceUtils.getKVProperties(capType);
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
                        const data = InheritanceUtils.getEffectiveCapabilityDefinitionsOfNodeType(currentNodeData.type, this.entityTypes);
                        this.capabilities.capDefinitionNames = [];
                        this.capabilities.capDefinitionName = '';
                        for (const capType of data) {
                            const qNameOfType = new QName(capType.capabilityType);
                            this.capabilities.capDefinitionNames.push(qNameOfType.localName);
                        }

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
        if (!this.capabilities.capabilities || !this.capabilities.capabilities.capability) {
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
                    this.capabilities.properties = InheritanceUtils.getKVProperties(cap);
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
        if (!this.requirements.requirements || !this.requirements.requirements.requirement) {
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
                    this.requirements.properties = InheritanceUtils.getKVProperties(req);
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
        const x = event.clientX - this.newNodePositionOffsetX;
        const y = event.clientY - this.newNodePositionOffsetY + this.scrollOffset;
        this.allNodeTemplates[this.indexOfNewNode].x = x;
        this.allNodeTemplates[this.indexOfNewNode].y = y;
    }

    /**
     * Repositions the new node and repaints the screen
     * @param $event  The html event.
     */
    positionNewNode(): void {
        this.updateSelectedNodes();
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
                if (this.configuration.isYaml() && difference < 0) {
                    // a relationship is deleted. reset the associated source requirement
                    const deletedRels = this.allRelationshipTemplates.filter(rel => currentRelationships.every(curRel => curRel.id !== rel.id));
                    deletedRels.forEach(deletedRel => {
                        const reqId = deletedRel.sourceElement.ref;
                        const sourceNodeTemplate = this.allNodeTemplates
                            .find(nt =>
                                nt.requirements &&
                                nt.requirements.requirement
                                && nt.requirements.requirement.some(req => req.id === reqId));
                        const requirementModel: RequirementModel = sourceNodeTemplate.requirements.requirement
                            .find(req => req.id === reqId);
                        const requirementDefinition: RequirementDefinitionModel = InheritanceUtils
                            .getEffectiveRequirementDefinitionsOfNodeType(sourceNodeTemplate.type, this.entityTypes)
                            .find(reqDef => reqDef.name === requirementModel.name);
                        requirementModel.capability = requirementDefinition.capability;
                        requirementModel.relationship = requirementDefinition.relationship;
                        requirementModel.node = requirementDefinition.node;

                    });

                }
                this.allRelationshipTemplates = currentRelationships;
                this.allRelationshipTemplates.forEach(relTemplate => this.manageRelationships(relTemplate));
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
     * @param rendererState  Representation of all possible buttons.
     */
    setRendererState(rendererState: TopologyRendererState): void {
        if (rendererState) {
            this.topologyRendererState = rendererState;
            this.revalidateContainer();
            let leaveNodesAsSelectedAfterLayouting;

            if (this.topologyRendererState.buttonsState.layoutButton) {
                this.layoutDirective.layoutNodes(this.nodeChildrenArray, this.allRelationshipTemplates)
                    .then(done => {
                        leaveNodesAsSelectedAfterLayouting = false;
                        // This call might seem confusing as we are calling it again right after executing,
                        // but this just toggles the button state back to false, so layout can be called again.
                        // TODO: change this behavior to simple events without a boolean flag
                        this.ngRedux.dispatch(this.topologyRendererActions.executeLayout());
                    });
            } else if (this.topologyRendererState.buttonsState.alignHButton
                || this.topologyRendererState.buttonsState.alignVButton) {
                const selectionActive = (this.selectedNodes.length >= 1);
                const nodesToBeAligned = selectionActive ? this.selectedNodes : this.allNodeTemplates;
                leaveNodesAsSelectedAfterLayouting = selectionActive;
                const alignmentMode = this.topologyRendererState.buttonsState.alignHButton ? align.Horizontal : align.Vertical;
                this.layoutDirective.align(this.nodeChildrenArray, nodesToBeAligned, alignmentMode)
                    .then(() => {
                        leaveNodesAsSelectedAfterLayouting = false;
                        // This call might seem confusing as we are calling it again right after executing,
                        // but this just toggles the button state back to false, so layout can be called again.
                        if (alignmentMode === align.Horizontal) {
                            this.ngRedux.dispatch(this.topologyRendererActions.executeAlignH());
                        } else {
                            this.ngRedux.dispatch(this.topologyRendererActions.executeAlignV());
                        }
                    });
            } else if (this.topologyRendererState.buttonsState.importTopologyButton) {
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
            } else if (this.topologyRendererState.buttonsState.splitTopologyButton) {
                this.splitMatchService.splitTopology(this.backendService, this.ngRedux, this.topologyRendererActions, this.errorHandler);
            } else if (this.topologyRendererState.buttonsState.threatModelingButton) {

                // don't cache data, always refetch.
                this.threatModelingData = new ThreatModelingModalData();
                this.ngRedux.dispatch(this.topologyRendererActions.threatModeling());
                // show modal
                this.threatModelingModal.show();

                this.backendService.threatCatalogue().subscribe(
                    threats => threats.forEach(threat => this.threatModelingData.threatCatalog.push(threat))
                );

                this.backendService.threatAssessment().subscribe(
                    assessment => {
                        Object.keys(assessment.threats)
                            .map(key => assessment.threats[key])
                            .map(threat => threat.mitigations
                                .filter(mitigation => assessment.svnfs.includes(mitigation))
                                .map(mitigation => this.threatModelingData.mitigations.add(new QName(mitigation)))
                            );
                    }
                );
            } else if (this.topologyRendererState.buttonsState.matchTopologyButton) {
                this.splitMatchService.matchTopology(this.backendService, this.ngRedux, this.topologyRendererActions, this.errorHandler);
            } else if (this.topologyRendererState.buttonsState.substituteTopologyButton) {
                this.ngRedux.dispatch(this.topologyRendererActions.substituteTopology());
                this.backendService.substituteTopology();
            } else if (this.topologyRendererState.nodesToSelect) {
                this.clearSelectedNodes();
                this.topologyRendererState.nodesToSelect
                    .forEach(value => this.enhanceDragSelection(value));
            } else if (this.topologyRendererState.buttonsState.placeComponentsButton) {
                this.placementService.placeComponents(this.backendService, this.ngRedux, this.topologyRendererActions, this.errorHandler);
            } else if (this.topologyRendererState.buttonsState.manageYamlPoliciesButton) {
                this.ngRedux.dispatch(this.topologyRendererActions.manageYamlPolicies());
                // show manageYamlPoliciesModal
                this.copyOfYamlPolicies = this.getYamlPoliciesTableData();
                this.manageYamlPoliciesModal.show();
            }

            setTimeout(() => {
                if (leaveNodesAsSelectedAfterLayouting === true) {
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
     * Closes the threat modeling modal
     */
    closeThreatModeling(): void {
        this.threatModelingModal.hide();
    }

    createNewThreat(): void {
        this.backendService.threatCreation(this.threatModelingData.threatCreation).subscribe(res => {
            this.threatModelingData.threatCreation = new ThreatCreation();
            this.alert.info(res);
        });
    }

    addMitigationToTopology(mitigation): void {
        this.closeThreatModeling();
        const newNode: TNodeTemplate = new TNodeTemplate(
            {},
            mitigation.localName + '_' + Math.floor(Math.random() * 10),
            mitigation.qName,
            mitigation.localName,
            1,
            1,
            TopologyTemplateUtil.getNodeVisualsForNodeTemplate(mitigation.qName, this.entityTypes.nodeVisuals),
            [],
            [],
            {},
            1,
            1,
            null,
            null,
            null,
            null
        );

        this.ngRedux.dispatch(this.actions.saveNodeTemplate(newNode));
    }

    /**
     * Revalidates the offsets and other data of the container in the DOM.
     */
    public revalidateContainer(): void {
        if (this.newJsPlumbInstance) {
            setTimeout(() => {
                this.newJsPlumbInstance.revalidate('container');
                this.newJsPlumbInstance.repaintEverything();
            }, 1);
        }
    }

    /**
     * Updates the internal representation of all nodes with the actual DOM information.
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
            if (node.id === nodeTemplate.firstChild.id) {
                nodeIndex = index;
                return true;
            }
        });
        const nodeCoordinates = {
            id: nodeTemplate.firstChild.id,
            x: nodeTemplate.firstChild.offsetLeft,
            y: nodeTemplate.firstChild.offsetTop
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
                if (this.selectedNodes.some(node => node.id === nodeTemplate.firstChild.id)) {
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

            if (labelString.startsWith(this.backendService.configuration.relationshipPrefix)) {
                // Workaround to support old topology templates with the real name
                labelString = newRelationship.type.substring(newRelationship.type.indexOf('}') + 1);
            }

            let relationSource = newRelationship.sourceElement.ref;
            let relationTarget = newRelationship.targetElement.ref;

            if (newRelationship.policies && newRelationship.policies.policy) {
                const list: TPolicy[] = newRelationship.policies.policy;
                labelString += '<br>';
                for (const value of list) {
                    const visual = this.entityTypes.policyTypeVisuals.find(
                        policyTypeVisual => policyTypeVisual.typeId === value.policyType
                    );

                    if (visual && visual.imageUrl) {
                        labelString += '<img style="display: block; margin-left: auto; margin-right: auto; margin-top: 5px;' +
                            ' max-width: 40px; max-height: 40px;" src="' + visual.imageUrl + '" />';
                    }
                }
            }

            // check if source reference is not a node template
            {
                if (!this.allNodesIds.includes(relationSource)) {
                    // check if source reference is a requirement of a node template
                    const findNode = this.allNodeTemplates
                        .find(node => node.requirements && node.requirements.requirement
                            && node.requirements.requirement.find(req => req.id === relationSource)
                        );
                    if (findNode) {
                        relationSource = findNode.id;
                    }
                }
            }

            // check if target reference is a node template
            if (!this.allNodesIds.includes(relationTarget)) {
                // check if target reference is a capability of a node template
                const findNode = this.allNodeTemplates
                    .find(node => node.capabilities && node.capabilities.capability && node.capabilities.capability.find(cap => cap.id === relationTarget));
                if (findNode) {
                    relationTarget = findNode.id;
                }
            }

            const border = isNullOrUndefined(newRelationship.state)
                ? '#fafafa' : VersionUtils.getElementColorByDiffState(newRelationship.state);
            const me = this;
            const conn = this.newJsPlumbInstance.connect({
                source: relationSource,
                target: relationTarget,
                overlays: [['Arrow', { width: 15, length: 15, location: 1, id: 'arrow', direction: 1 }],
                    ['Label', {
                        label: labelString,
                        id: 'label',
                        events: {
                            click: function (labelOverlay, originalEvent) {
                                setTimeout(() => me.onClickJsPlumbConnection(conn, newRelationship), 1);
                            }
                        },
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
            this.handleRelSideBar(conn, newRelationship);

            if (newRelationship.state) {
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
                    if (this.newJsPlumbInstance.isSource(this.dragSourceInfos.nodeId)) {
                        this.newJsPlumbInstance.unmakeSource(this.dragSourceInfos.nodeId);
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
    handleDeleteKeyEvent(event?: KeyboardEvent) {
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
                    const toDelete = this.newJsPlumbInstance.getConnections().filter(conn => conn.sourceId === node.nodeTemplate.id ||
                        conn.targetId === node.nodeTemplate.id);
                    toDelete.forEach(conn => {
                        this.ngRedux.dispatch(this.actions.deleteRelationshipTemplate(conn.id));
                        this.newJsPlumbInstance.deleteConnection(conn);
                    });
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
                if (this.selectedNodes.find(selectedNode => selectedNode && selectedNode.id === node.nodeTemplate.id)) {
                    node.makeSelectionVisible = false;
                }
            });
            this.newJsPlumbInstance.removeFromAllPosses(this.selectedNodes.map(node => node && node.id));
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
                this.enhanceDragSelection(node.firstChild.id);
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
                type: '',
                properties: '',
                source: '',
                target: ''
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
     * @param nodes
     * @param id
     * @returns Boolean True if 'Nodes' contains 'id'.
     */
    arrayContainsNode(nodes: any[], id: string): boolean {
        if (nodes !== null && nodes.length > 0) {
            return nodes.some(node => node && node.id === id);
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
    assignRelTypes(relType: VisualEntityType): void {
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
        this.entityTypes.relationshipTypes.forEach(value => this.assignRelTypes(value));
        this.reqCapRelationshipService.sourceSelectedEvent.subscribe(source => this.setSource(source));
        this.reqCapRelationshipService.sendSelectedRelationshipTypeEvent.subscribe(relationship => this.setSelectedRelationshipType(relationship));
    }

    /**
     * set source for Relationship between Requirement and a Capability
     * @param dragSourceInfo
     */
    setSource(dragSourceInfo: DragSource) {
        if (dragSourceInfo) {
            const nodeArrayLength = this.allNodeTemplates.length;
            const currentNodeIsSource = this.newJsPlumbInstance.isSource(dragSourceInfo.dragSource);
            if (!this.dragSourceActive && !currentNodeIsSource && nodeArrayLength > 1) {
                this.newJsPlumbInstance.makeSource(dragSourceInfo.nodeId, {
                    connectorOverlays: [
                        ['Arrow', { location: 1 }],
                    ],
                });
                this.dragSourceInfos = dragSourceInfo;
                this.targetNodes = this.getAllCapabilityIds();

                if (this.targetNodes.length > 0) {
                    this.newJsPlumbInstance.makeTarget(this.targetNodes);
                    this.newJsPlumbInstance.targetEndpointDefinitions = {};
                    this.dragSourceActive = true;
                    this.bindReqCapConnection();
                }
            }
        }
    }

    getAllCapabilityIds() {
        const capIds: string[] = [];
        this.allNodeTemplates.forEach(node => {
            if (node.capabilities) {
                if (node.capabilities.capability) {
                    node.capabilities.capability.forEach(cap => {
                        capIds.push(node.id + '.' + cap.id);
                    });
                }
            }
        });
        return capIds;
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
        if (nodeId) {
            this.newJsPlumbInstance.draggable(nodeId);
            if (this.paletteOpened) {
                this.bindNewNode();
            }
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
        if (this.newJsPlumbInstance) {
            this.newJsPlumbInstance.select().removeType('marked');
            this.revalidateContainer();
            this.removeDragSource();
            this.clearSelectedNodes();
            this.unbindConnection();
            this.startTime = new Date().getTime();
        }
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

        // if in YAML mode, automatically add all requirement and capability definitions to the node template!
        if (this.configuration.isYaml()) {
            this.newNode.requirements = { requirement: [] };
            this.newNode.capabilities = { capability: [] };
            const reqData = InheritanceUtils.getEffectiveRequirementDefinitionsOfNodeType(this.newNode.type, this.entityTypes);
            if (reqData) {
                reqData.forEach(reqDef => {
                    const reqModel = RequirementModel.fromRequirementDefinition(reqDef);
                    reqModel.id = TopologyTemplateUtil.generateYAMLRequirementID(this.newNode, reqModel.name);
                    this.newNode.requirements.requirement.push(reqModel);
                });
            }
            const capData = InheritanceUtils.getEffectiveCapabilityDefinitionsOfNodeType(this.newNode.type, this.entityTypes);
            if (capData) {
                capData.forEach(capDef => {
                    const capModel = CapabilityModel.fromCapabilityDefinitionModel(capDef);
                    capModel.id = TopologyTemplateUtil.generateYAMLCapabilityID(this.newNode, capModel.name);
                    this.newNode.capabilities.capability.push(capModel);
                });
            }
        }
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

    onClickRelationshipTemplateName(clickedRelTemplateId: string): void {
        const currentRelTemplate = this.allRelationshipTemplates.find(rel => rel.id === clickedRelTemplateId);
        const connection = this.newJsPlumbInstance.getAllConnections().find(conn => conn.id === clickedRelTemplateId);
        this.onClickJsPlumbConnection(connection, currentRelTemplate);
    }

    /**
     * Sets the sidebar up for a new node, makes it visible, sets a type and adds a click listener to this relationship
     * @param conn            The JSPlumb connection
     * @param newRelationship The new relationship internally
     */
    private handleRelSideBar(conn: any, newRelationship: TRelationshipTemplate): void {
        if (conn) {
            conn.id = newRelationship.id;
            conn.setType(newRelationship.type);
            conn.bind('click', rel => {
                this.onClickJsPlumbConnection(conn, rel);
            });
        }

        this.revalidateContainer();
    }

    /**
     * jsPlumb relationship/label click actions
     */
    onClickJsPlumbConnection(conn: any, rel: any) {
        this.clearSelectedNodes();
        this.newJsPlumbInstance.select().removeType('marked');
        const currentRel = this.allRelationshipTemplates.find(con => con.id === rel.id);
        if (currentRel) {
            this.ngRedux.dispatch(this.actions.openSidebar({
                sidebarContents: {
                    sidebarVisible: true,
                    nodeClicked: false,
                    id: currentRel.id,
                    nameTextFieldValue: currentRel.name,
                    type: currentRel.type,
                    properties: currentRel.properties,
                    source: currentRel.sourceElement.ref,
                    target: currentRel.targetElement.ref
                }
            }));
            conn.addType('marked');
        }
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
            ((selectionRect.top + selectionRect.height) > (object.offsetTop +
                object.offsetHeight - this.scrollOffset)) &&
            (selectionRect.top < (object.offsetTop - this.scrollOffset)) &&
            ((selectionRect.left + selectionArea.getBoundingClientRect().width) > (object.offsetLeft +
                object.offsetWidth)) &&
            (selectionRect.left < (object.offsetLeft))
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
                if (this.selectedNodes.find(selectedNode => selectedNode && selectedNode.id === node.nodeTemplate.id)) {
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
                const currentTypeValid = this.entityTypes.relationshipTypes.some(relType => relType.qName === this.selectedRelationshipType.qName);
                const currentSourceIdValid = this.allNodeTemplates.some(node => node.id === sourceElement);
                if (sourceElement && currentTypeValid && currentSourceIdValid) {
                    const prefix = this.backendService.configuration.relationshipPrefix;
                    const relName = this.selectedRelationshipType.name;
                    let relNumber = 0;
                    let relationshipId: string;

                    do {
                        relationshipId = prefix + '_' + relName + '_' + relNumber++;
                    } while (this.allRelationshipTemplates.find(value => value.id === relationshipId));

                    if (sourceElement !== info.targetId) {
                        const newRelationship = new TRelationshipTemplate(
                            { ref: sourceElement },
                            { ref: info.targetId },
                            this.selectedRelationshipType.name,
                            relationshipId,
                            this.selectedRelationshipType.qName,
                            InheritanceUtils.getDefaultPropertiesFromEntityTypes(this.selectedRelationshipType.name, this.entityTypes.relationshipTypes),
                            [],
                            [],
                            {}
                        );
                        this.ngRedux.dispatch(this.actions.saveRelationship(newRelationship));
                    }
                }
                this.unbindConnection();
                this.revalidateContainer();
            });
        }
    }

    bindReqCapConnection() {
        if (this.jsPlumbBindConnection === false && this.selectedRelationshipType) {
            this.newJsPlumbInstance.bind('connection', info => {
                this.jsPlumbBindConnection = true;
                if (this.dragSourceInfos) {
                    const sourceNode = info.sourceId;
                    const requirementId = this.dragSourceInfos.dragSource.id;
                    const capabilityId: string = info.targetId.substring(info.targetId.indexOf('.') + 1);
                    const currentTypeValid = this.entityTypes.relationshipTypes.some(relType => relType.qName === this.selectedRelationshipType.qName);
                    const currentSourceIdValid = this.allNodeTemplates.some(node => node.id === sourceNode);
                    if (sourceNode && currentTypeValid && currentSourceIdValid) {
                        const prefix = this.backendService.configuration.relationshipPrefix;
                        const relName = this.selectedRelationshipType.name;
                        let relNumber = 0;
                        let relationshipId: string;

                        do {
                            relationshipId = prefix + '_' + relName + '_' + relNumber++;
                        } while (this.allRelationshipTemplates.find(value => value.id === relationshipId));
                        const capModel: CapabilityModel = this.getCapability(capabilityId);
                        const reqModel: RequirementModel = this.dragSourceInfos.dragSource;
                        const sourceNodeTypeString: string = this.allNodeTemplates
                            .filter(nodeTemplate => nodeTemplate.id === this.dragSourceInfos.nodeId)
                            .map(nodeTemplate => nodeTemplate.type)[0];
                        const targetNodeTypeString: string = this.allNodeTemplates
                            .filter(nodeTemplate => nodeTemplate.id === info.targetId.substring(0, info.targetId.indexOf('.')))
                            .map(nodeTemplate => nodeTemplate.type)[0];
                        const capDef: CapabilityDefinitionModel = InheritanceUtils
                            .getEffectiveCapabilityDefinitionsOfNodeType(targetNodeTypeString, this.entityTypes)
                            .filter(current => current.name === capModel.name)[0];
                        const reqDef: RequirementDefinitionModel = InheritanceUtils
                            .getEffectiveRequirementDefinitionsOfNodeType(sourceNodeTypeString, this.entityTypes)
                            .filter(current => current.name === reqModel.name)[0];

                        if (this.checkReqCapCompatibility(reqDef, capDef, capModel, new QName(sourceNodeTypeString), new QName(targetNodeTypeString))) {
                            reqModel.capability = capModel.name;
                            reqModel.relationship = relationshipId;
                            reqModel.node = info.targetId.substring(0, info.targetId.indexOf('.'));
                            const newRelationship = new TRelationshipTemplate(
                                { ref: requirementId },
                                { ref: capabilityId },
                                this.selectedRelationshipType.name,
                                relationshipId,
                                this.selectedRelationshipType.qName,
                                InheritanceUtils.getDefaultPropertiesFromEntityTypes(this.selectedRelationshipType.name,
                                    this.entityTypes.relationshipTypes),
                                [],
                                [],
                                {}
                            );
                            this.ngRedux.dispatch(this.actions.saveRelationship(newRelationship));
                        }
                        for (const rel of this.newJsPlumbInstance.getConnections()) {
                            if (rel.targetId === info.targetId) {
                                this.newJsPlumbInstance.deleteConnection(rel);
                            }
                        }
                        this.dragSourceActive = false;
                        this.resetDragSource(requirementId);
                    }
                    this.unbindConnection();
                    this.revalidateContainer();
                }
            });
        }
    }

    getCapability(capabilityId: string): CapabilityModel {
        let capability: any = null;
        this.allNodeTemplates.forEach(node => {
            if (node.capabilities) {
                if (node.capabilities.capability) {
                    node.capabilities.capability.forEach(cap => {
                        if (cap.id === capabilityId) {
                            capability = cap;
                        }
                    });
                }
            }
        });
        return capability;
    }

    // todo ensure supporting inheritance of node types and capability types
    checkReqCapCompatibility(reqDefinition: RequirementDefinitionModel, capDefinition: CapabilityDefinitionModel,
                             cap: CapabilityModel, sourceNodeType: QName, targetNodeType: QName) {
        if (this.configuration.isYaml()) {
            // we assume the relationship type is correct
            // check the conditions set by the requirement definition
            if (this.matchType(reqDefinition.node, targetNodeType.qName, this.entityTypes.unGroupedNodeTypes) &&
                this.matchType(reqDefinition.capability, capDefinition.capabilityType, this.entityTypes.capabilityTypes)) {
                const validSourceTypes: string[] = InheritanceUtils.getValidSourceTypes(capDefinition, this.entityTypes.capabilityTypes);
                if (validSourceTypes) {
                    if (validSourceTypes.some(e => this.matchType(e, sourceNodeType.qName, this.entityTypes.unGroupedNodeTypes))) {
                        return true;
                    } else {
                        this.notify.warning(sourceNodeType.localName + ' is not a valid source type for a capability of type '
                            + new QName(cap.type).localName);
                        return false;
                    }
                } else {
                    // if no valid source types are defined, then we assume all node types are valid sources.
                    return true;
                }
            } else {
                this.notify.warning('The selected requirement and capability are not compatible');
                return false;
            }
        } else {
            return this.searchTypeAndCheckForCompatibility(cap);
        }
    }

    /**
     * check for compatibility of Requirement and Capability
     * @param capability the capability model (assignment) to validate
     */
    searchTypeAndCheckForCompatibility(capability: CapabilityModel) {
        let requiredTargetType = '';

        this.entityTypes.requirementTypes.some(req => {
            if (req.qName === this.dragSourceInfos.dragSource.type) {
                requiredTargetType = req.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].requiredCapabilityType;
                return true;
            }
        });
        return this.matchType(requiredTargetType, capability.type, this.entityTypes.capabilityTypes);
    }

    /**
     * check if the target entity type (or any of its parents) match the required entity type
     * @param requiredType
     * @param targetElementType
     * @param targetElementTypeSet
     */
    matchType(requiredType: string, targetElementType: string, targetElementTypeSet: EntityType[]) {
        if (requiredType) {
            const typeAncestry = InheritanceUtils.getInheritanceAncestry(targetElementType, targetElementTypeSet);
            return typeAncestry.some(type => type.qName === requiredType);
        } else {
            // if there is no required type, we assume all target types are valid!
            return true;
        }
    }

    /**
     * Handles the new node by binding to mouse move and mouse up actions
     */
    private bindNewNode(): void {
        if (this.newNode) {
            this.handleNodePressActions(this.newNode.id);
            this.unbindMouseActions.push(this.renderer.listen(this.eref.nativeElement, 'mousemove',
                (event) => this.moveNewNode(event)));
            this.unbindMouseActions.push(this.renderer.listen(this.eref.nativeElement, 'mouseup',
                ($event) => this.positionNewNode()));
        }
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

    // YAML Policy methids
    addNewYamlPolicy(policyName: string) {
        if (policyName && this.selectedNewPolicyType && policyName.length > 0 && this.selectedNewPolicyType.length > 0) {
            if (this.entityTypes.yamlPolicies.some(policy => policy.name === policyName)) {
                this.notify.warning('Duplicate policy name!', 'Policy not Added!');
            } else {
                const newPolicy = new TPolicy(policyName, undefined, this.selectedNewPolicyType, [],
                    [], {}, { kvproperties: {} }, []);
                const newPolicies = [...this.entityTypes.yamlPolicies, newPolicy];
                this.ngRedux.dispatch(this.actions.changeYamlPolicies(newPolicies));
                this.addYamlPolicyModal.hide();
            }
        } else {
            this.notify.warning('Missing info!', 'Policy not Added!');
        }
    }

    handleUpdatedYamlPolicies(policies: { policy: TPolicy[] }) {
        if (this.entityTypes) {
            this.entityTypes.yamlPolicies = policies.policy;
            this.copyOfYamlPolicies = this.getYamlPoliciesTableData();
        }
    }

    handleRemoveYamlPolicyClick($event: TPolicy) {
        const newPolicies = this.entityTypes.yamlPolicies.filter(policy => policy.name !== $event.name);
        this.ngRedux.dispatch(this.actions.changeYamlPolicies(newPolicies));
    }

    handleAddNewYamlPolicyClick() {
        this.addYamlPolicyModal.show();
    }

    handleYamlPolicySelected($event: WineryRowData) {
        this.selectedYamlPolicy = this.entityTypes.yamlPolicies.find(policy => policy.name === (<TPolicy>$event.row).name);
        this.selectedYamlPolicy.properties = InheritanceUtils.getEffectiveKVPropertiesOfTemplateElement(this.selectedYamlPolicy.properties,
            this.selectedYamlPolicy.policyType, this.entityTypes.policyTypes);
    }

    savePolicyProperties(): void {
        this.yamlPolicyProperties.forEach(txtArea => {
            const keyOfChangedTextArea = txtArea.nativeElement.parentElement.innerText.replace(/\s/g, '');
            this.selectedYamlPolicy.properties.kvproperties[keyOfChangedTextArea] = txtArea.nativeElement.value;
        });

    }

    showPropertiesOfSelectedYamlPolicy(): boolean {
        if (this.selectedYamlPolicy && this.selectedYamlPolicy.properties && this.selectedYamlPolicy.properties.kvproperties) {
            return Object.keys(this.selectedYamlPolicy.properties.kvproperties).length > 0;
        }

        return false;
    }

    getYamlPoliciesTableData() {
        return this.entityTypes.yamlPolicies.map(policy => {
            const result:
                {
                    name: string,
                    policyType: string,
                    typeHref: string,
                    properties?: any,
                    targets?: string[]
                } = {
                name: policy.name,
                policyType: policy.policyType,
                typeHref: this.typeToHref(new QName(policy.policyType), 'policytypes'),
                properties: policy.properties,
                targets: policy.targets
            };
            return result;
        });
    }

    showYamlPolicyManagementModal() {
        this.ngRedux.dispatch(this.topologyRendererActions.manageYamlPolicies());
    }

    private typeToHref(typeQName: QName, refType: string): string {
        // no need to encode the namespace since we assume dotted namespaces in YAML mode
        const absoluteURL = `${this.backendService.configuration.uiURL}${refType}/${typeQName.nameSpace}/${typeQName.localName}`;
        return '<a href="' + absoluteURL + '">' + typeQName.localName + '</a>';
    }
}
