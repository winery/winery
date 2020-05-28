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

import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
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
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DependenciesComponent implements OnInit, OnChanges {

    columns: Array<WineryTableColumn> = [
        { title: 'Name', name: 'name', sort: false },
    ];

    dependenciesTableModel: DependencyTableModel[] = [];

    @Input() dependencies: string[] = [];
    @Input() selectableArtifacts: Artifact[] = [];
    @Output() newDependencyAdded: EventEmitter<Artifact> = new EventEmitter();

    @ViewChild('modal') modal: ModalDirective;
    @ViewChild('confirmRemoveModal') confirmRemoveModal: ModalDirective;

    selectData: SelectData;
    object: DependencyTableModel;

    constructor(public instanceService: InstanceService) {
    }

    ngOnInit() {
    }

    /**
     * refreshes depdendencies if @Input values changed
     * @param changes
     */
    ngOnChanges(changes: SimpleChanges) {
        this.dependenciesTableModel = [];
        if (this.dependencies) {
            this.dependencies.forEach(item => this.dependenciesTableModel.push({ name: item }));
        } else {
            this.dependencies = [];
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
        this.newDependencyAdded.emit(this.selectableArtifacts.find(v => v.name === value));
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
