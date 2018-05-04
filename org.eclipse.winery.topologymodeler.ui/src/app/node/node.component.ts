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
    AfterViewInit,
    Component,
    ComponentRef,
    DoCheck,
    ElementRef,
    EventEmitter,
    Input,
    KeyValueDiffers,
    NgZone,
    OnDestroy,
    OnInit,
    Output,
    Renderer2
} from '@angular/core';
import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';
import { ButtonsStateModel } from '../models/buttonsState.model';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { WineryActions } from '../redux/actions/winery.actions';
import { hostURL } from '../models/configuration';
import { TNodeTemplate } from '../models/ttopology-template';
import { QName } from '../models/qname';
import { urlElement } from '../models/enums';
import { BackendService } from '../services/backend.service';
import { isNullOrUndefined } from 'util';

/**
 * Every node has its own component and gets created dynamically.
 */
@Component({
    selector: 'winery-node',
    templateUrl: './node.component.html',
    styleUrls: ['./node.component.css'],
    animations: [trigger('onCreateNodeTemplateAnimation', [
        state('hidden', style({opacity: 0, transform: 'translateX(0)'})),
        state('visible', style({opacity: 1, transform: 'scale'})),
        transition('hidden => visible', animate('300ms', keyframes([
            style({opacity: 0, transform: 'scale(0.2)', offset: 0}),
            style({opacity: 0.3, transform: 'scale(1.1)', offset: 0.7}),
            style({opacity: 1, transform: 'scale(1.0)', offset: 1.0})
        ]))),
    ]),
    ]
})
export class NodeComponent implements OnInit, AfterViewInit, OnDestroy, DoCheck {
    public items: string[] = ['Item 1', 'Item 2', 'Item 3'];
    public accordionGroupPanel = 'accordionGroupPanel';
    public customClass = 'customClass';
    visibilityState = 'hidden';
    connectorEndpointVisible = false;
    startTime;
    endTime;
    groupedNodeTypes: any;
    longpress = false;
    makeSelectionVisible = false;
    setFlash = false;
    setMaxFlash = false;
    setMinFlash = false;
    policyTypes: any;
    policyTemplates: any;
    artifactTypes: any;
    removeZIndex: any;
    propertyDefinitionType: string;
    @Input() entityTypes: any;
    @Input() dragSource: string;
    @Input() navbarButtonsState: ButtonsStateModel;
    @Output() sendId: EventEmitter<string>;
    @Output() askForRepaint: EventEmitter<string>;
    @Output() setDragSource: EventEmitter<any>;
    @Output() closedEndpoint: EventEmitter<string>;
    @Output() handleNodeClickedActions: EventEmitter<any>;
    @Output() updateSelectedNodes: EventEmitter<string>;
    @Output() sendCurrentType: EventEmitter<string>;
    @Output() askForRemoval: EventEmitter<string>;
    @Output() unmarkConnections: EventEmitter<string>;
    @Output() saveNodeRequirements: EventEmitter<any>;
    @Output() sendPaletteStatus: EventEmitter<any>;
    @Output() sendNodeData: EventEmitter<any>;
    @Input() allRelationshipTypesColors: Array<string>;
    @Input() nodeTemplate: TNodeTemplate;

    previousPosition: any;
    currentPosition: any;
    nodeRef: ComponentRef<Component>;
    unbindMouseMove: Function;
    currentNodeId: string;
    hostURL = hostURL;
    flashTimer = 300;
    parentEl: string;
    // differ object for detecting changes made to the nodeTemplate object for DoCheck
    differ: any;

    constructor(private zone: NgZone,
                private $ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                public elRef: ElementRef,
                private backendService: BackendService,
                private renderer: Renderer2,
                private differs: KeyValueDiffers) {
        this.sendId = new EventEmitter();
        this.askForRepaint = new EventEmitter();
        this.setDragSource = new EventEmitter();
        this.closedEndpoint = new EventEmitter();
        this.handleNodeClickedActions = new EventEmitter();
        this.updateSelectedNodes = new EventEmitter();
        this.sendCurrentType = new EventEmitter();
        this.askForRemoval = new EventEmitter();
        this.unmarkConnections = new EventEmitter();
        this.saveNodeRequirements = new EventEmitter();
        this.sendPaletteStatus = new EventEmitter();
        this.sendNodeData = new EventEmitter();
    }

    /**
     *  Parse the localName of the NodeType
     */
    get nodeTypeLocalName() {
        return this.nodeTemplate.type.split('}').pop();
        // return this.nodeTemplate.type ? new QName(this.nodeTemplate.type).localName : JSON.stringify({});
    }

    public addItem(): void {
        this.items.push(`Items ${this.items.length + 1}`);
    }

    /**
     * This function determines which kind of properties the nodeType embodies.
     * We have 3 possibilities: none, XML element, or Key value pairs.
     * @param {string} type
     */
    findOutPropertyDefinitionTypeForProperties(type: string, groupedNodeTypes: any): string {
        for (const nameSpace of groupedNodeTypes) {
            for (const nodeTypeVar of nameSpace.children) {
                if (nodeTypeVar.id === type) {
                    // if PropertiesDefinition doesn't exist then it must be of type NONE
                    if (isNullOrUndefined(nodeTypeVar.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition)) {
                        return 'NONE';
                    } else {
                        // if no XML element inside PropertiesDefinition then it must be of type Key Value
                        if (!nodeTypeVar.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].propertiesDefinition.element) {
                            return 'KV';
                        } else {
                            // else we have XML
                            return 'XML';
                        }
                    }
                }
            }
        }
    }

    /**
     * Angular lifecycle event.
     */
    ngOnInit() {
        this.differ = this.differs.find([]).create(null);
        this.backendService.requestGroupedNodeTypes().subscribe((groupedNodeTypes) => {
            this.groupedNodeTypes = groupedNodeTypes;
            this.propertyDefinitionType = this.findOutPropertyDefinitionTypeForProperties(this.nodeTemplate.type, groupedNodeTypes);
        });
    }

    /**
     * Angular lifecycle event.
     */
    ngDoCheck() {
        const nodeTemplateChanges = this.differ.diff(this.nodeTemplate);
        if (nodeTemplateChanges) {
        }
    }

    /**
     * Triggered when opening a modal to send node data to the canvas for handling the addition of modal data.
     */
    sendToggleAction(nodeData: any): void {
        const currentNodeData = {...this.nodeTemplate, ...nodeData};
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
     * @param $event
     */
    repaint($event) {
        $event.stopPropagation();
        setTimeout(() => this.askForRepaint.emit('Repaint'), 1);
    }

    /**
     * Sets the current type of a node.
     * @param $event
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
        this.sendCurrentType.emit(currentType);
    }

    /**
     * Handler for mousedown events, toggles visibility of node attributes
     * @param $event
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
            const offsetLeft = this.elRef.nativeElement.firstChild.nextElementSibling.offsetLeft;
            const offsetTop = this.elRef.nativeElement.firstChild.nextElementSibling.offsetTop;
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
     * @param $event
     */
    mouseMove($event): void {
        const offsetLeft = this.elRef.nativeElement.firstChild.nextElementSibling.offsetLeft;
        const offsetTop = this.elRef.nativeElement.firstChild.nextElementSibling.offsetTop;
        this.currentPosition = {
            x: offsetLeft,
            y: offsetTop
        };
    }

    /**
     * Checks if it was a click or a drag operation on the node.
     * @param $event
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
     * @param $event
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
     * @param $event
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
     * @param $event
     */
    openSidebar($event): void {
        $event.stopPropagation();
        // close sidebar when longpressing a node template
        if (this.longpress) {
            this.sendPaletteStatus.emit('close Sidebar');
            this.$ngRedux.dispatch(this.actions.openSidebar({
                sidebarContents: {
                    sidebarVisible: false,
                    nodeClicked: true,
                    id: '',
                    nameTextFieldValue: '',
                    type: '',
                    minInstances: -1,
                    maxInstances: -1
                }
            }));
        } else {
            this.$ngRedux.dispatch(this.actions.openSidebar({
                sidebarContents: {
                    sidebarVisible: true,
                    nodeClicked: true,
                    id: this.nodeTemplate.id,
                    nameTextFieldValue: this.nodeTemplate.name,
                    type: this.nodeTemplate.type,
                    minInstances: this.nodeTemplate.minInstances,
                    maxInstances: this.nodeTemplate.maxInstances
                }
            }));
        }
    }

    /**
     * Navigates to the corresponding node type in the management UI
     * @param $event
     */
    linkType($event: any): void {
        const qName = new QName(this.nodeTemplate.type);
        const typeURL = this.backendService.configuration.uiURL + '#' + urlElement.NodeTypeURL +
            encodeURIComponent(encodeURIComponent(qName.nameSpace)) + '/' + qName.localName
            + urlElement.ReadMe;
        window.open(typeURL, '_blank');
    }

    /**
     * Displays a box of the whole text if the text doesn't fit in the original element
     * @param cell
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
    }

    /**
     * Checks if it was a click or a drag operation on the node.
     * @param $event
     */
    private testTimeDifference($event): void {
        if ((this.endTime - this.startTime) < 200) {
            this.longpress = false;
        } else if (this.endTime - this.startTime >= 200) {
            this.longpress = true;
        }
    }
}
