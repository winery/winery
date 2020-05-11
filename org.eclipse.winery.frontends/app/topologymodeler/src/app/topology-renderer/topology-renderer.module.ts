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

import { ModuleWithProviders, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { NavbarComponent } from '../navbar/navbar.component';
import { NodeComponent } from '../node/node.component';
import { CanvasComponent } from '../canvas/canvas.component';
import { LayoutDirective } from '../layout/layout.directive';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { JsPlumbService } from '../services/jsPlumb.service';
import { TopologyRendererComponent } from './topology-renderer.component';
import { NgReduxModule } from '@angular-redux/store';
import { RequirementsComponent } from '../node/requirements/requirements.component';
import { TargetLocationsComponent } from '../node/target-locations/target-locations.component';
import { PoliciesComponent } from '../node/policies/policies.component';
import { DeploymentArtifactsComponent } from '../node/deployment-artifacts/deployment-artifacts.component';
import { CapabilitiesComponent } from '../node/capabilities/capabilities.component';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';
import { ToscatypeTableComponent } from '../node/toscatype-table/toscatype-table.component';
import { EntitiesModalComponent } from '../canvas/entities-modal/entities-modal.component';
import { LocalnamePipe } from '../pipes/localname.pipe';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { PopoverModule } from 'ngx-bootstrap/popover';
import { VersionsComponent } from '../node/versions/versions.component';
import { PropertiesModule } from '../properties/properties.module';
import { WineryModalModule } from '../../../../tosca-management/src/app/wineryModalModule/winery.modal.module';
import { WineryFeatureToggleModule } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/winery-feature-toggle.module';
import { WineryTableModule } from '../../../../tosca-management/src/app/wineryTableModule/wineryTable.module';
import { Ng2TableModule } from 'ng2-table';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        HttpClientModule,
        BrowserAnimationsModule,
        BsDropdownModule.forRoot(),
        ToastrModule.forRoot(),
        AccordionModule.forRoot(),
        NgReduxModule,
        RouterModule,
        WineryModalModule,
        TypeaheadModule.forRoot(),
        TooltipModule.forRoot(),
        PopoverModule.forRoot(),
        PropertiesModule,
        WineryFeatureToggleModule,
        WineryTableModule,
        Ng2TableModule,
    ],
    declarations: [
        NavbarComponent,
        NodeComponent,
        CanvasComponent,
        LayoutDirective,
        TopologyRendererComponent,
        RequirementsComponent,
        TargetLocationsComponent,
        PoliciesComponent,
        DeploymentArtifactsComponent,
        CapabilitiesComponent,
        ToscatypeTableComponent,
        EntitiesModalComponent,
        LocalnamePipe,
        VersionsComponent
    ],
    exports: [
        TopologyRendererComponent,

    ],
})
export class TopologyRendererModule {

    static forRoot(): ModuleWithProviders {
        return {
            ngModule: TopologyRendererModule,
            providers: [
                // { provide: ToastOptions, useClass: WineryCustomOption },
                JsPlumbService
            ]
        };
    }

    constructor() {
    }
}
