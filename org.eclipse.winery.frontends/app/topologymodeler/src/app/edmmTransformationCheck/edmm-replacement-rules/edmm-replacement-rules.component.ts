/********************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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
import { Component, Input, OnInit } from '@angular/core';
import { EdmmTechnologyTransformationCheck, EdmmTransformationCheckService } from '../edmmTransformationCheck.service';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { TNodeTemplate, TTopologyTemplate } from '../../models/ttopology-template';
import { TopologyRendererActions } from '../../redux/actions/topologyRenderer.actions';
import { ManageTopologyService } from '../../services/manage-topology.service';
import { BackendService } from '../../services/backend.service';

@Component({
    selector: 'winery-edmm-replacement-rules',
    templateUrl: './edmm-replacement-rules.component.html',
    styleUrls: [
        '../edmmTransformationCheck.component.css',
        '../../navbar/navbar.component.css'
    ],
    providers: [
        EdmmTransformationCheckService
    ]
})
export class EdmmReplacementRulesComponent implements OnInit {

    pluginName: string;
    components: string[];
    toTopology: string;

    @Input() pluginResult: EdmmTechnologyTransformationCheck;
    @Input() currentCandidate: string;
    @Input() topologyTemplate: TTopologyTemplate;
    @Input() oneToOneMap: Map<string, string>;

    private ruleIndex = 0;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions,
                private manageTopologyService: ManageTopologyService) {
    }

    /**
     * @param topology the fromTopology or toTopology contained in the replacementRules
     * @param processComponent a function to process each component
     * @param processRelation a function to process each relation
     * @param obj an object that is passed to every function and then returned
     */
    private static navigateTopology(
        topology: Map<string, any>,
        processComponent: (component: string, topology: Map<string, any>, result?: any) => any,
        processRelation: (source: string, target: string, relation: string, topology: Map<string, any>, result?: any) => any,
        obj?: any,
    ): any {
        for (const component in topology) {
            if (topology.hasOwnProperty(component)) {

                obj = processComponent(component, topology, obj);

                for (const i in topology[component]['relations']) {
                    if (topology[component]['relations'].hasOwnProperty(i)) {
                        for (const relation in topology[component]['relations'][i]) {
                            if (topology[component]['relations'][i].hasOwnProperty(relation)) {

                                obj = processRelation(component, topology[component]['relations'][i][relation], relation, topology, obj);
                            }
                        }
                    }
                }
            }
        }
        return obj;
    }

    ngOnInit(): void {

        if (this.pluginResult.replacementRules.length > 0) {
            this.pluginName = this.pluginResult.name;
            this.updateRule();
        }
    }

    applyRule() {
        // the center point of the nodes to be deleted will be the starting point to draw new nodes
        let x = 0;
        let y = 0;
        let nodeToBeReplaced;
        const relationsToBeReplaced = [];

        const unsupportedComponents = this.pluginResult.replacementRules[this.ruleIndex].unsupportedComponents;
        for (const node of this.topologyTemplate.nodeTemplates) {
            for (const componentId of unsupportedComponents) {
                if (node.id === componentId) {
                    x = x + node.x;
                    y = y + node.y;
                    nodeToBeReplaced = node;
                    // deleting the node
                    this.manageTopologyService.deleteNode(node.id);
                }
            }
        }

        for (const rel of this.topologyTemplate.relationshipTemplates) {
            if (rel.sourceElement.ref === nodeToBeReplaced.id ||
                rel.targetElement.ref === nodeToBeReplaced.id) {
                relationsToBeReplaced.push(rel);
            }
        }

        x = x / unsupportedComponents.length;
        y = y / unsupportedComponents.length;

        const drawNode = (component: string, topology: Map<string, any>) => {
            // it may be the case that a node has already been created because it was the target of a relation
            if (!topology[component].hasOwnProperty('_id')) {
                // if the component has not an _id property means that it has not been created
                // because it's the first time we meet it
                const sourceNode: TNodeTemplate = this.manageTopologyService.newNode(x, y, this.oneToOneMap[topology[component]['type']]);
                topology[component]['_id'] = sourceNode.id;
            }
        };

        EdmmReplacementRulesComponent.navigateTopology(
            this.pluginResult.replacementRules[this.ruleIndex].toTopology,
            (component, topology) => {
                drawNode(component, topology);
            },
            (source, target, relation, topology) => {
                drawNode(target, topology);
                this.manageTopologyService.newRelationship(topology[source]['_id'], topology[target]['_id'], this.oneToOneMap[relation]);
            }
        );

        const newTopologyMap = this.pluginResult.replacementRules[this.ruleIndex].toTopology;
        const key = Object.keys(newTopologyMap)[0];
        const newTargetNode = newTopologyMap[key];

        for (const rel of relationsToBeReplaced) {
            if (rel.sourceElement.ref === nodeToBeReplaced.id) {
                this.manageTopologyService.newRelationship(newTargetNode._id, rel.targetElement.ref, rel.type);
            }
            if (rel.targetElement.ref === nodeToBeReplaced.id) {
                this.manageTopologyService.newRelationship(rel.sourceElement.ref, newTargetNode._id, rel.type);
            }
        }

        this.ngRedux.dispatch(this.actions.executeLayout());
    }

    getColorClass(): string {
        const reason = this.pluginResult.replacementRules[this.ruleIndex].reason;
        if (reason === 'unsupported') {
            return 'notSupported';
        } else if (reason === 'preferred') {
            return 'applicable';
        }
        return 'partlyApplicable';
    }

    nextRule() {
        this.moveRule(+1);
    }

    prevRule() {
        this.moveRule(-1);
    }

    onMouseOver() {
        const idList = [];
        for (const node of this.topologyTemplate.nodeTemplates) {
            for (const componentId of this.pluginResult.replacementRules[this.ruleIndex].unsupportedComponents) {
                if (node.id === componentId) {
                    idList.push(node.id);
                }
            }
        }
        this.ngRedux.dispatch(this.actions.highlightNodes(idList));
    }

    onMouseLeave() {
        this.ngRedux.dispatch(this.actions.highlightNodes([]));
    }

    private updateRule() {
        this.components = this.pluginResult.replacementRules[this.ruleIndex].unsupportedComponents;
        this.toTopology = this.parseTopology(this.pluginResult.replacementRules[this.ruleIndex].toTopology);
    }

    /**
     * At the moment the topology represented  with a Map is displayed in a yaml-like way to the user.
     */
    private parseTopology(topology: Map<string, any>): string {
        return EdmmReplacementRulesComponent.navigateTopology(
            topology,
            (component, _topology, result) => {
                result += component + '\n';
                result += '  type : ' + this.oneToOneMap[_topology[component]['type']] + '\n';
                return result;
            },
            (source, target, relation, _topology, result) => {
                result += '  - ' + this.oneToOneMap[relation] + ' : ' + target + '\n';
                return result;
            },
            ''
        );
    }

    private updateRuleIndex(update: number) {
        this.ruleIndex += update;
        if (this.ruleIndex > this.pluginResult.replacementRules.length - 1) {
            this.ruleIndex = 0;
        } else if (this.ruleIndex < 0) {
            this.ruleIndex = this.pluginResult.replacementRules.length - 1;
        }
    }

    private moveRule(update: number) {
        this.updateRuleIndex(update);
        this.updateRule();
        this.onMouseLeave();
        this.onMouseOver();
    }
}
