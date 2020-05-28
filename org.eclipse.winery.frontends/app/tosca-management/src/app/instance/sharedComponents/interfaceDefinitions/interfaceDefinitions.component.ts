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
import { Component, OnInit, ViewChild } from '@angular/core';
import { InstanceService } from '../../instance.service';
import { ConfigureInterface, Interface, Operation, OperationImplementation, StandardInterface } from '../../../model/interfaces';
import { ModalDirective } from 'ngx-bootstrap';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';
import { SelectableListComponent } from '../interfaces/selectableList/selectableList.component';
import { InterfaceDefinitionsService } from './interfaceDefinitions.service';
import { HttpErrorResponse } from '@angular/common/http';
import { SelectData } from '../../../model/selectData';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { ArtifactsService } from '../artifacts/artifacts.service';
import { Artifact } from '../../../model/artifact';
import { FilesService } from '../filesTag/files.service';

@Component({
    selector: 'winery-interfaces',
    templateUrl: 'interfaceDefinitions.component.html',
})
export class InterfaceDefinitionsComponent implements OnInit {

    loading = false;

    interfaces: Interface[] = [];
    selectedInterface: Interface;
    selectedOperation: Operation;

    columnsInputParameters: Array<WineryTableColumn> = [
        { title: 'Name', name: 'key', sort: true },
        { title: 'Type', name: 'type', sort: false },
        { title: 'Required', name: 'required', sort: false },
        { title: 'Default Value', name: 'defaultValue', sort: false },
        { title: 'Description', name: 'description', sort: false },
    ];
    columnsOutputParameters: Array<WineryTableColumn> = [
        { title: 'Name', name: 'key', sort: true },
        { title: 'Type', name: 'type', sort: false },
        { title: 'Required', name: 'required', sort: false },
        { title: 'Value', name: 'value', sort: false },
        { title: 'Description', name: 'description', sort: false },
    ];

    validatorObject: WineryValidatorObject;
    @ViewChild('addInterfaceModal') addInterfaceModal: ModalDirective;
    @ViewChild('addOperationModal') addOperationModal: ModalDirective;
    @ViewChild('removeModal') removeModal: ModalDirective;
    modalTitle: string;
    removeModalElement: string;

    @ViewChild('interfacesList') interfacesListComponent: SelectableListComponent;
    @ViewChild('operationsList') operationsListComponent: SelectableListComponent;

    readonly interfaceTypes: SelectData[] = [
        { text: '{tosca.interfaces.node.lifecycle}Standard', id: 'Standard' },
        { text: '{tosca.interfaces.relationship}Configure', id: 'Configure' },
    ];
    selectableArtifacts: Artifact[] = [];
    selectedArtifact: Artifact[];

    constructor(private interfaceService: InterfaceDefinitionsService, public instanceService: InstanceService,
                private filesService: FilesService, private artifactsService: ArtifactsService) {
    }

    ngOnInit() {
        this.loadInterfaces();
        this.fetchFiles();
    }

    private loadInterfaces() {
        this.loading = true;
        this.interfaceService.getInterfaces()
            .subscribe(
                data => {
                    this.interfaces = [];
                    data.forEach(item => this.interfaces.push({ ...new Interface(), ...item }));
                    this.loading = false;
                },
                error => this.handleError(error)
            );
        this.artifactsService.getArtifacts().subscribe(data => {
            data.forEach(item => this.selectableArtifacts.push({ ...item, ...{ id: item.name, text: `${item.name} / ${item.type}` } }));
        });
        if (this.selectedInterface) {
            this.onInterfaceSelected(this.selectedInterface);
        }
        if (this.selectedOperation) {
            this.onOperationSelected(this.selectedOperation);
        }
    }

    private handleError(error: HttpErrorResponse) {
        console.error(error);
        this.loading = false;
    }

    save() {
        // cleanup empty operation implementations
        for (const i of this.interfaces) {
            for (const o of i.operations) {
                if (o.implementation) {
                    if (!o.implementation.primary && (!o.implementation.dependencies || o.implementation.dependencies.length === 0)) {
                        o.implementation = undefined;
                    }
                }
            }
        }
        this.loading = true;
        this.interfaceService.updateInterfaces(this.interfaces)
            .subscribe(
                () => this.loadInterfaces(),
                error => this.handleError(error)
            );
    }

    onAddInterface() {
        this.validatorObject = new WineryValidatorObject(this.interfaces, 'name');
        this.addInterfaceModal.show();
    }

    onInterfaceSelected(selectedInterface: Interface) {
        this.selectedInterface = selectedInterface;
    }

    onRemoveInterface() {
        this.modalTitle = 'Interface';
        this.removeModalElement = this.selectedInterface.name;
        this.removeModal.show();
    }

    addInterface(item: SelectData) {
        let int = Object.assign(new Interface(), StandardInterface);
        if (item.id === 'Configure') {
            int = Object.assign(new Interface(), ConfigureInterface);
        }
        this.interfaces.push(int);
        this.interfacesListComponent.selectItem(int);
    }

    removeInterface() {
        for (let i = 0; i < this.interfaces.length; i++) {
            if (this.interfaces[i].name === this.selectedInterface.name) {
                this.interfaces.splice(i, 1);
            }
        }
        this.selectedInterface = null;
    }

    onAddOperation() {
        this.validatorObject = new WineryValidatorObject(this.selectedInterface.operations, 'name');
        this.addOperationModal.show();
    }

    onOperationSelected(selectedOperation: Operation) {
        this.selectedArtifact = [];
        this.selectedOperation = selectedOperation;
        if (this.selectedOperation.implementation) {
            const id = this.selectedOperation.implementation.primary;
            const artifact = this.selectableArtifacts.find(item => item.name === id);
            if (artifact) {
                this.selectedArtifact.push(artifact);
            }
            if (!this.selectedOperation.implementation.dependencies) {
                this.selectedOperation.implementation.dependencies = [];
            }
        }
    }

    onRemoveOperation() {
        this.modalTitle = 'Operation';
        this.removeModalElement = this.selectedOperation.name;
        this.removeModal.show();
    }

    addOperation(name: string) {
        if (this.selectedInterface) {
            const op = Object.assign(new Operation(), { name: name });
            this.selectedInterface.operations.push(op);
            this.operationsListComponent.selectItem(op);
        }
    }

    removeOperation() {
        const arr = this.selectedInterface.operations;
        for (let i = 0; i < arr.length; i++) {
            if (arr[i].name === this.selectedOperation.name) {
                arr.splice(i, 1);
            }
        }
        this.selectedOperation = null;
    }

    onArtifactSelected(data: SelectData) {
        if (!this.selectedOperation) {
            return;
        }
        if (!this.selectedOperation.implementation) {
            this.selectedOperation.implementation = new OperationImplementation();
        }
        this.selectedOperation.implementation.primary = data.id;
    }

    onArtifactRemoved($event: any) {
        if (!this.selectedOperation) {
            return;
        }
        this.selectedOperation.implementation.primary = undefined;
    }

    addNewDependency($event: Artifact) {
        if (!this.selectedOperation.implementation) {
            this.selectedOperation.implementation = new OperationImplementation();
        }
        if (!this.selectedOperation.implementation.dependencies) {
            this.selectedOperation.implementation.dependencies = [];
        }
        this.selectedOperation.implementation.dependencies.push($event.name);
    }

    /**
     * looks for files in <currentURL>/files
     * and adds them to possible dependencies
     */
    private fetchFiles() {
        this.filesService.getLocalFiles().subscribe(
            (obs) => {
                // related to https://github.com/valor-software/ng2-select/issues/896
                const tmp: Artifact[] = [];
                obs.files.forEach((f) => {
                    const a: Artifact = { name: f.name, type: 'file', description: '', deployPath: '', file: f.url };
                    tmp.push({ ...a, ...{ id: f.name, text: `${f.name}` } });
                });
                this.selectableArtifacts = tmp.concat(this.selectableArtifacts);
            });
    }
}
