/*******************************************************************************
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
 *******************************************************************************/
import { Component, Input, OnInit } from '@angular/core';

import { Operation, ToscaInterface } from '../../model/toscaInterface';
import { Node } from '../../model/workflow/node';
import { WineryService } from '../../services/winery.service';
import { NodeTemplate } from '../../model/nodetemplate';
import { Template } from '../../model/workflow/Template';

/**
 * Enum to identify which fields have to be reset
 */
enum ResetType {
    Template = 2,
    Interface = 1,
    Operation = 0,
}

/**
 * node template component provides operations about tosca modules which saved in winery.
 * This component will be used in the property component while the corresponding workflow node is calling the node
 * template's operation
 */
@Component({
    selector: 'b4t-node-template',
    templateUrl: 'node-template.component.html',
})
export class WmNodeTemplateComponent implements OnInit {

    @Input() public node: Node;
    nodeTemplates: NodeTemplate[];
    nodeInterfaces: ToscaInterface[];
    nodeOperations: Operation[];

    selectedNodeTemplate: NodeTemplate;
    selectedNodeTemplateId: String = '';
    selectedInterface: ToscaInterface;
    selectedInterfaceName: String = '';
    selectedOperation: Operation;
    selectedOperationName: String = '';

    constructor(private wineryService: WineryService) {
    }

    public ngOnInit(): void {
        if (this.node && this.node.template) {
            this.selectedNodeTemplate = new NodeTemplate(this.node.template.id, this.node.template.id, this.node.template.type, this.node.template.namespace);
            this.selectedNodeTemplateId = this.selectedNodeTemplate.id;
            this.selectedInterface = new ToscaInterface();
            this.selectedInterface.name = this.node.nodeInterface;
            this.selectedInterfaceName = this.selectedInterface.name;
            this.selectedOperation = new Operation();
            this.selectedOperation.name = this.node.nodeOperation;
            this.selectedOperationName = this.selectedOperation.name;
            this.loadInterfaces();
        }

        this.wineryService.loadNodeTemplates()
            .subscribe(nodeTemplates => this.nodeTemplates = nodeTemplates);
    }

    public nodeTemplateChanged(nodeId: any) {
        this.selectedNodeTemplate = this.nodeTemplates.find(value => value.id === nodeId.target.value);
        this.selectedNodeTemplateId = this.selectedNodeTemplate.id;
        this.node.nodeTemplate = this.selectedNodeTemplate.name;
        this.node.template = new Template(this.selectedNodeTemplate.id, this.selectedNodeTemplate.namespace, this.selectedNodeTemplate.type);

        this.resetNode(ResetType.Template);
        this.loadInterfaces();
    }

    public nodeInterfaceChanged(nodeInterface: any) {
        this.selectedInterface = this.nodeInterfaces.find(value => value.name === nodeInterface.target.value);
        this.selectedInterfaceName = this.selectedInterface.name;
        this.node.nodeInterface = this.node.template.nodeInterface = this.selectedInterface.name;
        this.nodeOperations = this.selectedInterface.operation;

        this.resetNode(ResetType.Interface);
    }

    public nodeOperationChanged(operation: any) {
        this.selectedOperation = this.nodeOperations.find(value => value.name === operation.target.value);
        this.selectedOperationName = this.selectedOperation.name;
        this.node.nodeOperation = this.node.template.operation = this.selectedOperation.name;
        this.node.input = this.selectedOperation.inputParameters ? this.selectedOperation.inputParameters.inputParameter : [];
        this.node.output = this.selectedOperation.outputParameters ? this.selectedOperation.outputParameters.outputParameter : [];
    }

    private loadInterfaces() {
        if (this.selectedNodeTemplate && this.selectedNodeTemplate.namespace && this.selectedNodeTemplate.type) {
            this.wineryService
                .loadNodeTemplateInterfaces(
                    this.selectedNodeTemplate.namespace,
                    this.selectedNodeTemplate.type.replace(/^\{(.+)\}(.+)/, '$2')
                )
                .subscribe(interfaces => {
                    this.nodeInterfaces = interfaces;
                    if (this.selectedInterface && this.selectedInterface.name) {
                        // this is required since the node.template only saves the name of the interface
                        this.selectedInterface = this.nodeInterfaces.find(value => value.name === this.selectedInterface.name);
                        this.nodeOperations = this.selectedInterface.operation;
                    } else {
                        this.nodeOperations = [];
                    }
                });
        }
    }

    private resetNode(type: ResetType) {
        if (type >= ResetType.Interface) {
            if (type >= ResetType.Template) {
                this.selectedInterface = null;
                this.node.nodeInterface = null;
            }
            this.selectedOperation = null;
            this.node.nodeOperation = null;
            this.node.input = null;
            this.node.output = null;
        }
    }
}
