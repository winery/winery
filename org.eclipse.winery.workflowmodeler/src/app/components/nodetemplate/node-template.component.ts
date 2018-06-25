/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { AfterViewInit, Component, Input } from '@angular/core';

import { Operation } from '../../model/operation';
import { Parameter } from '../../model/parameter';
import { Node } from '../../model/workflow/node';
import { WineryService } from '../../services/winery.service';

/**
 * node template component provides operations about tosca modules which saved in winery.
 * This component will be used in the property component while the corresponding workflow node is calling the node template's operation
 */
@Component({
    selector: 'winery-b4t-node-template',
    templateUrl: 'node-template.component.html',
})
export class WmNodeTemplateComponent implements AfterViewInit {

    @Input() public node: Node;
    nodeTemplates = [];
    nodeInterfaces: string[] = [];
    nodeOperations: Operation[] = [];

    constructor(private wineryService: WineryService) {
    }

    public ngAfterViewInit() {
        this.wineryService.loadNodeTemplates()
            .subscribe(nodeTemplates => this.nodeTemplates = nodeTemplates);

        this.loadInterfaces();
        this.loadOperations();
    }

    public nodeTemplateChanged() {
        this.node.nodeTemplate = this.node.template.id;
        this.setTemplateNamespace();

        this.node.template.nodeInterface = '';
        this.nodeInterfaceChanged();

        this.loadInterfaces();
    }

    public nodeInterfaceChanged() {
        this.node.nodeInterface = this.node.template.nodeInterface;
        this.node.template.operation = '';
        this.nodeOperationChanged();

        this.loadOperations();
    }

    public nodeOperationChanged() {
        this.node.nodeOperation = this.node.template.operation;
        this.node.input = [];
        this.node.output = [];

        this.loadParameters();
    }

    private setTemplateNamespace() {
        const nodeTemplate = this.nodeTemplates.find(
            tmpNodeTemplate => tmpNodeTemplate.id === this.node.template.id);

        if (nodeTemplate) {
            this.node.template.namespace = nodeTemplate.namespace;
            this.node.template.type = nodeTemplate.type;
        }
    }

    private loadInterfaces() {
        if (this.node.template.id) {
            this.wineryService
                .loadNodeTemplateInterfaces(this.node.template.namespace, this.node.template.type)
                .subscribe(interfaces => this.nodeInterfaces = interfaces);
        }
    }

    private loadOperations() {
        if (this.node.template.nodeInterface) {
            this.nodeOperations = [];
            this.wineryService.loadNodeTemplateOperations(
                this.node.template.namespace,
                this.node.template.type,
                this.node.template.nodeInterface)
                .subscribe(operations =>
                    operations.forEach(operation => this.nodeOperations.push(new Operation(operation))));
        }
    }

    private loadParameters() {
        if (this.node.template.operation) {
            const template = this.node.template;
            this.wineryService
                .loadNodeTemplateOperationParameter(
                    template.namespace,
                    template.type,
                    template.nodeInterface,
                    template.operation)
                .then(params => this.updateNodeParams(params));
        }
    }

    private updateNodeParams(params: { input: string[], output: string[] }) {
        this.node.input = [];
        params.input.forEach(param =>
            this.node.input.push(new Parameter(param, 'string', '')));

        this.node.output = [];
        params.output.forEach(param =>
            this.node.output.push(new Parameter(param, 'string', '')));
    };

}
