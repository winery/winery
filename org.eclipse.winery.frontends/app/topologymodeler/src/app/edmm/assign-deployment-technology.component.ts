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
import { NgRedux } from '@angular-redux/store';
import { TNodeTemplate } from '../models/ttopology-template';
import { IWineryState } from '../redux/store/winery.store';
import { WineryActions } from '../redux/actions/winery.actions';
import { BackendService } from '../services/backend.service';
import { DeploymentTechnology } from '../models/deployment-technology';
import { QName } from '../../../../shared/src/app/model/qName';
import { TOSCA_WINERY_EXTENSIONS_NAMESPACE } from '../models/namespaces';

@Component({
    selector: 'winery-assign-deployment-technology',
    templateUrl: './assign-deployment-technology.component.html',
    styleUrls: ['./assign-deployment-technology.component.css']
})
export class AssignDeploymentTechnologyComponent implements OnInit {

    static QNAME_DEPLOYMENT_TECHNOLOGY = QName.create(TOSCA_WINERY_EXTENSIONS_NAMESPACE, 'deployment-technology').qName;

    @Input() readonly: boolean;
    @Input() deploymentTechnologies: DeploymentTechnology[];
    @Input() node: TNodeTemplate;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private ngActions: WineryActions,
                private backendService: BackendService) {
    }

    ngOnInit() {
        this.backendService.requestSupportedDeploymentTechnologies().subscribe(value => this.deploymentTechnologies = value);
    }

    isEllipsisActive(cell): boolean {
        return (cell.offsetWidth < cell.scrollWidth);
    }

    isMember(dt: DeploymentTechnology) {
        const value = this.node.otherAttributes[AssignDeploymentTechnologyComponent.QNAME_DEPLOYMENT_TECHNOLOGY];
        return value && dt.id === value;
    }

    toggleMembership(dt: DeploymentTechnology) {
        this.ngRedux.dispatch(this.ngActions.assignDeploymentTechnology(this.node, dt.id));
    }

    isEmpty(): boolean {
        return !this.deploymentTechnologies || this.deploymentTechnologies.length === 0;
    }
}
