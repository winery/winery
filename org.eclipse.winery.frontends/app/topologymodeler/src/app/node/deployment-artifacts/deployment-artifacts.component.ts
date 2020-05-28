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
import { IWineryState } from '../../redux/store/winery.store';
import { NgRedux } from '@angular-redux/store';
import { TableType } from '../../models/enums';
import { WineryRepositoryConfigurationService } from '../../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { TArtifact } from '../../models/ttopology-template';

@Component({
    selector: 'winery-deployment-artifacts',
    templateUrl: './deployment-artifacts.component.html',
    styleUrls: ['./deployment-artifacts.component.css']
})
/**
 * This Handles Information about the deployment artifacts
 */
export class DeploymentArtifactsComponent implements OnInit {

    readonly tableTypes = TableType;

    @Output() toggleModalHandler: EventEmitter<any>;
    @Input() readonly: boolean;
    @Input() currentNodeData: any;
    @Input() deploymentArtifacts;
    @Input() yamlArtifacts: TArtifact[];
    latestNodeTemplate;

    constructor(private $ngRedux: NgRedux<IWineryState>, private configurationService: WineryRepositoryConfigurationService) {
        this.toggleModalHandler = new EventEmitter();
    }

    /**
     * Propagates the click event to node.component, where deployment artifactOrPolicy modal gets opened.
     * @param $event
     */
    public toggleModal($event) {
        this.toggleModalHandler.emit(this.currentNodeData);
    }

    ngOnInit() {
    }
}
