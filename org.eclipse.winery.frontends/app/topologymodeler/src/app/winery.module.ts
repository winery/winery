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

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { JsPlumbService } from './services/jsPlumb.service';
import { WineryComponent } from './winery.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';
import { PaletteComponent } from './palette/palette.component';
import { TopologyRendererModule } from './topology-renderer/topology-renderer.module';
import { PrintViewComponent } from './print-view/print-view.component';
import { DevToolsExtension, NgRedux, NgReduxModule } from '@angular-redux/store';
import { INITIAL_IWINERY_STATE, IWineryState, rootReducer } from './redux/store/winery.store';
import { WineryActions } from './redux/actions/winery.actions';
import { TopologyRendererActions } from './redux/actions/topologyRenderer.actions';
import { LoadedService } from './services/loaded.service';
import { AppReadyEventService } from './services/app-ready-event.service';
import { HotkeyModule } from 'angular2-hotkeys';
import { BackendService } from './services/backend.service';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';
import { ExistsService } from './services/exists.service';
import { EntitiesModalService } from './canvas/entities-modal/entities-modal.service';
import { ImportTopologyService } from './services/import-topology.service';
import { SplitMatchTopologyService } from './services/split-match-topology.service';
import { ErrorHandlerService } from './services/error-handler.service';
import { PopoverModule } from 'ngx-bootstrap/popover';
import { ProblemDetectionComponent } from './problemDetection/problemDetection.component';
import { PropertiesModule } from './properties/properties.module';
import { StatefulAnnotationsService } from './services/statefulAnnotations.service';
import { WineryModalModule } from '../../../tosca-management/src/app/wineryModalModule/winery.modal.module';
import { EnricherComponent } from './enricher/enricher.component';
import { WineryFeatureToggleModule } from '../../../tosca-management/src/app/wineryFeatureToggleModule/winery-feature-toggle.module';
import { PlaceComponentsService } from './services/placement.service';
import { MultiParticipantsComponent } from './multi-participants/multi-participants.component';
import { ReqCapRelationshipService } from './services/req-cap-relationship.service';
import { WineryTableModule } from '../../../tosca-management/src/app/wineryTableModule/wineryTable.module';
import { EdmmTransformationCheckComponent } from './edmmTransformationCheck/edmmTransformationCheck.component';
import { PolicyService } from './services/policy.service';
import { SidebarModule } from 'ng-sidebar';
import { NodeDetailsSidebarComponent } from './sidebars/node-details/nodeDetailsSidebar.component';
import { RefinementSidebarComponent } from './sidebars/refinement/refinementSidebar.component';
import { GroupViewComponent } from './group-view/group-view.component';
import { TagService } from '../../../tosca-management/src/app/instance/sharedComponents/tag/tag.service';
import { WineryDynamicTableModule } from '../../../tosca-management/src/app/wineryDynamicTable/wineryDynamicTable.module';
import { WineryDuplicateValidatorModule } from '../../../tosca-management/src/app/wineryValidators/wineryDuplicateValidator.module';
import { CollapseModule } from 'ngx-bootstrap';
import { GroupViewPoliciesComponent } from './group-view/policies/policies.component';
import { VersionSliderComponent } from './version-slider/version-slider.component';
import { NgxSliderModule } from '@angular-slider/ngx-slider';
import { VersionSliderService } from './version-slider/version-slider.service';
import { MultiParticipantsService } from './services/multi-participants.service';
import { ManageParticipantsComponent } from './participants/manage-participants.component';
import { ResearchPluginsComponent } from './sidebars/research-plugins/research-plugins.component';

@NgModule({
    declarations: [
        WineryComponent,
        PaletteComponent,
        NodeDetailsSidebarComponent,
        PrintViewComponent,
        RefinementSidebarComponent,
        ProblemDetectionComponent,
        EnricherComponent,
        MultiParticipantsComponent,
        EdmmTransformationCheckComponent,
        VersionSliderComponent,
        GroupViewComponent,
        GroupViewPoliciesComponent,
        ManageParticipantsComponent,
        ResearchPluginsComponent,
    ],
    exports: [WineryComponent],
    imports: [
        SidebarModule.forRoot(),
        BrowserModule,
        FormsModule,
        HttpClientModule,
        RouterModule.forRoot([{ path: '**', redirectTo: '', pathMatch: 'full' }]),
        BrowserAnimationsModule,
        NgReduxModule,
        BsDropdownModule.forRoot(),
        ToastrModule.forRoot({
            timeOut: 3000,
            preventDuplicates: true,
            easing: 'ease-in-out',
            enableHtml: true,
            progressBar: true,
            extendedTimeOut: 3000,
            easeTime: 450
        }),
        AccordionModule.forRoot(),
        TopologyRendererModule.forRoot(),
        HotkeyModule.forRoot({ cheatSheetHotkey: 'mod+space' }),
        WineryModalModule,
        TypeaheadModule.forRoot(),
        PopoverModule.forRoot(),
        PropertiesModule,
        WineryFeatureToggleModule,
        WineryTableModule,
        WineryDynamicTableModule,
        WineryDuplicateValidatorModule,
        CollapseModule,
        WineryTableModule,
        NgxSliderModule
    ],
    providers: [
        // { provide: ToastOptions, useClass: WineryCustomOption },
        JsPlumbService,
        WineryActions,
        TopologyRendererActions,
        LoadedService,
        AppReadyEventService,
        BackendService,
        TagService,
        ExistsService,
        EntitiesModalService,
        ImportTopologyService,
        SplitMatchTopologyService,
        ErrorHandlerService,
        StatefulAnnotationsService,
        PlaceComponentsService,
        ReqCapRelationshipService,
        PolicyService,
        VersionSliderService,
        MultiParticipantsService
    ],
    bootstrap: [WineryComponent]
})
export class WineryModule {
    constructor(ngRedux: NgRedux<IWineryState>,
                devTools: DevToolsExtension) {
        const storeEnhancers = devTools.isEnabled() ?
            [devTools.enhancer()] :
            [];

        ngRedux.configureStore(
            rootReducer,
            INITIAL_IWINERY_STATE,
            [],
            storeEnhancers);
    }
}
