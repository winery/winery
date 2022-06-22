/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

import { Component, OnDestroy } from '@angular/core';
import { PlaceholderSubstitutionService } from './placeholderSubstitution.service';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { TopologyRendererActions } from '../../redux/actions/topologyRenderer.actions';
import { WineryActions } from '../../redux/actions/winery.actions';
import { WineryRepositoryConfigurationService } from '../../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { BackendService } from '../../services/backend.service';
import { EntityTypesModel } from '../../models/entityTypesModel';

@Component({
    selector: 'winery-placeholder-substitution',
    templateUrl: 'placeholderSubstitution.component.html',
    providers: [
        PlaceholderSubstitutionService
    ]
})
export class PlaceholderSubstitutionComponent implements OnDestroy {

    private entityTypes: EntityTypesModel;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private rendererActions: TopologyRendererActions,
                private wineryActions: WineryActions,
                private service: PlaceholderSubstitutionService,
                private configurationService: WineryRepositoryConfigurationService,
                private backendService: BackendService) {
        this.ngRedux.select(state => state.wineryState.entityTypes)
            .subscribe(types => this.entityTypes = types);
    }

    ngOnDestroy(): void {
    }
    
}
