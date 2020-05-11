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

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { WineryTableColumn } from '../../../../wineryTableModule/wineryTable.component';
import { InstanceService } from '../../../instance.service';
import { ModalDirective } from 'ngx-bootstrap';
import { Artifact } from '../../../../model/artifact';
import { SelectData } from '../../../../model/selectData';

export class DependencyTableModel {
    name: string;
}

@Component({
    selector: 'winery-dependencies',
    templateUrl: 'dependencies.component.html',
})
export class DependenciesComponent implements OnInit {

    /* tslint:disable no-bitwise */
    uuid: string = (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    columns: Array<WineryTableColumn> = [
        { title: 'Name', name: 'name', sort: false },
    ];

    dependenciesTableModel: DependencyTableModel[] = [];

    @Input() dependencies: string[] = [];
    @Input() selectableArtifacts: Artifact[] = [];

    @ViewChild('modal') modal: ModalDirective;
    @ViewChild('confirmRemoveModal') confirmRemoveModal: ModalDirective;

    selectData: SelectData;
    object: DependencyTableModel;

    constructor(public instanceService: InstanceService) {
    }

    ngOnInit() {
        this.dependenciesTableModel = [];
        if (this.dependencies) {
            this.dependencies.forEach(item => this.dependenciesTableModel.push({ name: item }));
        }
    }

    openModal() {
        this.selectData = null;
        this.modal.show();
    }

    openConfirmRemoveModal(object: DependencyTableModel) {
        if (object === null || object === undefined) {
            return;
        }
        this.object = object;
        this.confirmRemoveModal.show();
    }

    add() {
        const value = this.selectData.id;
        this.dependenciesTableModel.push({ name: value });
        this.dependencies.push(value);
    }

    removeObject(object: DependencyTableModel) {
        for (let i = 0; i < this.dependenciesTableModel.length; i++) {
            if (this.dependenciesTableModel[i].name === object.name) {
                this.dependenciesTableModel.splice(i, 1);
            }
        }
        for (let i = 0; i < this.dependencies.length; i++) {
            if (this.dependencies[i] === object.name) {
                this.dependencies.splice(i, 1);
            }
        }
        this.confirmRemoveModal.hide();
        this.object = null;
    }

    onArtifactSelected(data: SelectData) {
        this.selectData = data;
    }
}
