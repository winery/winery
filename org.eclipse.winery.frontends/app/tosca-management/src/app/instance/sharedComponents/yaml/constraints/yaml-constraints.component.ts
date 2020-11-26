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


import { Component, OnInit } from '@angular/core';
import { InstanceComponent } from '../../../instance.component';
import { ActivatedRoute, Router } from '@angular/router';
import { InstanceService } from '../../../instance.service';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { ExistService } from '../../../../wineryUtils/existService';
import { WineryInstance, WineryTemplateOrImplementationComponent } from '../../../../model/wineryComponent';
import { HttpErrorResponse } from '@angular/common/http';
import { TDataType } from '../../../../../../../topologymodeler/src/app/models/ttopology-template';
import { Constraint } from '../../../../model/constraint';
import { BsModalService } from 'ngx-bootstrap';
import { DataTypesService } from '../../../dataTypes/dataTypes.service';

const valid_constraint_keys = ['equal', 'greater_than', 'greater_or_equal', 'less_than', 'less_or_equal', 'in_range',
    'valid_values', 'length', 'min_length', 'max_length', 'pattern', 'schema'];
const list_constraint_keys = ['valid_values', 'in_range'];
const range_constraint_keys = ['in_range'];

@Component({
    selector: 'winery-yaml-constraints',
    templateUrl: 'yaml-constraints.component.html',
    styleUrls: [
        'yaml-constraints.component.css'
    ],
    providers: [
        DataTypesService
    ]
})
export class YamlConstraintsComponent implements OnInit {

    component: TDataType;
    loadingData = true;

    constructor(private notify: WineryNotificationService,
                public sharedData: InstanceService,
                private dataTypes: DataTypesService) {
    }

    ngOnInit(): void {
        this.loadDataType();
    }

    private loadDataType() {
        this.sharedData.getComponentData()
            .subscribe(
                data => this.handleDataInput(data),
                error => this.handleError(error)
            );
    }

    private handleDataInput(componentData: WineryInstance) {
        // TODO: check type hierarchy exposed by WineryInstance?
        this.component = componentData.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0] as unknown as TDataType;
        // backfill namespace from targetNamespace coming from the sharedData
        this.component.namespace = this.component.namespace || (this.component as any).targetNamespace;
        this.loadingData = false;
    }

    private handleError(error: HttpErrorResponse) {
        this.loadingData = false;
        this.notify.error(error.message);
    }

    /**
     * removes item from constraint list
     * @param constraintClause
     */
    removeConstraint(constraintClause: Constraint) {
        const index = this.component.constraints.indexOf(constraintClause);
        if (index > -1) {
            this.component.constraints.splice(index, 1);
        }
    }

    addConstraint(selectedConstraintKey: string, constraintValue: string) {
        // lists have to be separated by ','
        if (list_constraint_keys.indexOf(selectedConstraintKey) > -1) {
            this.component.constraints.push(new Constraint(selectedConstraintKey, null, constraintValue.split(',')));
        } else {
            this.component.constraints.push(new Constraint(selectedConstraintKey, constraintValue, null));
        }
    }

    save(): void {
        this.loadingData = true;
        this.dataTypes.updateConstraints(this.component)
            .subscribe(
                emptyResponse => this.loadingData = false,
                error => this.handleError(error)
            );
    }

    get valid_constraint_keys() {
        return valid_constraint_keys;
    }

    get list_constraint_keys() {
        return list_constraint_keys;
    }

    get range_constraint_keys() {
        return range_constraint_keys;
    }

}

