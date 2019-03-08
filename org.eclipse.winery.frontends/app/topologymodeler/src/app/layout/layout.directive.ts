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

import {Directive, ElementRef} from '@angular/core';
import ELK from 'elkjs/lib/elk.bundled.js';
import {TNodeTemplate, TRelationshipTemplate} from '../models/ttopology-template';
import {ToastrService} from 'ngx-toastr';
import {LayoutChildNodeModel} from '../models/layoutChildNodeModel';
import {NodeComponent} from '../node/node.component';
import {align} from '../models/enums';

@Directive({
    selector: '[wineryLayout]'
})
/**
 * Manages all layouting operations besides drag and drop (this is in canvas.ts)
 */
export class LayoutDirective {
    readonly nodeXOffset = 340;
    readonly nodeYOffset = 100;
    private jsPlumbInstance: any;

    constructor(private alert: ToastrService,
                private elRef: ElementRef) {
    }

    /**
     * Sets the JsPlumb instance from the existing JsPlumb canvas instance for further usage
     * @param jsPlumbInstance
     */
    setJsPlumbInstance(jsPlumbInstance: any): void {
        this.jsPlumbInstance = jsPlumbInstance;
    }

    /**
     * Layouts all nodes (not just the selected ones).
     * Uses ELK.Js which implements sugiyama to layout nodes.
     * @param nodeChildrenArray
     * @param relationshipTemplates
     */
    public layoutNodes(nodeChildrenArray: Array<NodeComponent>,
                       relationshipTemplates: Array<TRelationshipTemplate>): Promise<boolean> {
        // These are the input arrays for eclipse layout kernel (ELK).
        const children: LayoutChildNodeModel[] = [];
        const edges: any[] = [];

        // get width and height of nodes
        nodeChildrenArray.forEach(node => {
            const width = node.elRef.nativeElement.firstChild.offsetWidth;
            const height = node.elRef.nativeElement.firstChild.offsetHeight;
            children.push(new LayoutChildNodeModel(node.nodeTemplate.id, width, height));
            // also get their current positions and apply them to the internal list
            const left = node.elRef.nativeElement.firstChild.offsetLeft;
            const top = node.elRef.nativeElement.firstChild.offsetTop;
            node.nodeTemplate.x = left;
            node.nodeTemplate.y = top;
        });

        // get source and targets of relationships
        relationshipTemplates.forEach((rel, index) => {
            const sourceElement = rel.sourceElement.ref;
            const targetElement = rel.targetElement.ref;
            edges.push({id: index.toString(), sources: [sourceElement], targets: [targetElement]});
        });

        // initialize elk object which will layout the graph
        const elk = new ELK({});
        const graph = {
            id: 'root',
            properties: {
                'elk.algorithm': 'layered',
                'elk.spacing.nodeNode': '200',
                'elk.direction': 'DOWN',
                'elk.layered.spacing.nodeNodeBetweenLayers': '200'
            },
            children: children,
            edges: edges,
        };
        return new Promise(resolve => {

            const promise = elk.layout(graph);
            promise.then((data) => {
                this.applyPositions(data, nodeChildrenArray).then(() => {
                    resolve(true);
                });
            });
        });
    }

    /**
     * This applies the calculated positions to the actual node elements.
     * Uses ELK.Js which implements sugiyama to layout nodes.
     * @param data The data (relationships, nodes) used by the layouting algo.
     * @param nodeTemplates The internal representation of the nodes.
     * @param jsPlumbInstance
     */
    private applyPositions(data: any,
                           nodeChildrenArray: Array<NodeComponent>): Promise<boolean> {
        return new Promise(resolve => {

            nodeChildrenArray.forEach((node, index) => {
                // apply the new positions to the nodes
                node.nodeTemplate.x = data.children[index].x + this.nodeXOffset;
                node.nodeTemplate.y = data.children[index].y + this.nodeYOffset;
            });

            this.repaintEverything();
            resolve(true);
        });

    }

    /**
     * Aligns all selected elements horizontally or vertically.
     * If no element is selected, all elements get aligned horizontally or vertically.
     * @param nodeChildrenArray
     * @param selectedNodes
     * @param alignMode
     */
    public align(nodeChildrenArray: Array<NodeComponent>, selectedNodes: Array<TNodeTemplate>, alignMode): Promise<boolean> {
        let result;
        let selectedNodeComponents;
        if (nodeChildrenArray.length !== selectedNodes.length) {
            selectedNodeComponents = nodeChildrenArray.filter(node =>
                selectedNodes.find(selectedNode => node.nodeTemplate.id === selectedNode.id));
        } else {
            selectedNodeComponents = nodeChildrenArray;
        }
        return new Promise((resolve, reject) => {
            // if there is only 1 node selected, do nothing
            if (!(selectedNodeComponents.length === 1)) {
                const topPositions = selectedNodeComponents.map((node) => {
                    return node.elRef.nativeElement.firstChild.offsetTop;
                });
                // add biggest value to smallest and divide by 2, to get the exact middle of both
                result = ((Math.max.apply(null, topPositions) + Math.min.apply(null, topPositions)) / 2);
                // iterate over the nodes again, and apply positions
                selectedNodeComponents.forEach((node) => {
                    if (alignMode === align.Horizontal) {
                        node.nodeTemplate.y = result;
                    } else {
                        node.nodeTemplate.x = result;
                    }
                });
                this.repaintEverything();
                resolve(true);
            } else {
                reject(this.alert.info('You have only one node selected.'));
            }
        });
    }

    /**
     * Repaints everything after 1ms.
     * @param jsPlumbInstance
     */
    private repaintEverything(): void {
        setTimeout(() => this.jsPlumbInstance.repaintEverything(), 1);
    }

}
