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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TableType } from '../../models/enums';
import { FeatureEnum } from '../../../../../tosca-management/src/app/wineryFeatureToggleModule/wineryRepository.feature.direct';
import { EntityTypesModel } from '../../models/entityTypesModel';

@Component({
    selector: 'winery-policies',
    templateUrl: './policies.component.html',
    styleUrls: ['./policies.component.css']
})
/**
 * This Handles Information about the nodes policies
 */
export class PoliciesComponent implements OnInit {

    readonly tableTypes = TableType;
    readonly features = FeatureEnum;

    @Output() toggleModalHandler: EventEmitter<any>;
    @Output() showYamlPolicyManagementModal: EventEmitter<void>;
    @Input() readonly: boolean;
    @Input() currentNodeData: any;
    @Input() policies;
    @Input() entityTypes: EntityTypesModel;

    constructor() {
        this.toggleModalHandler = new EventEmitter();
        this.showYamlPolicyManagementModal = new EventEmitter();
    }

    /**
     * Propagates the click event to node.component, where policies modal gets opened.
     * @param $event
     */
    public toggleModal($event) {
        this.toggleModalHandler.emit(this.currentNodeData);
    }

    ngOnInit() {
    }

    handleShowYamlPolicyModalEvent() {
        this.showYamlPolicyManagementModal.emit();
    }

    isEmpty(): boolean {
        return !this.policies || this.policies.length === 0;
    }
}
