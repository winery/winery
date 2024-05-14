/********************************************************************************
 * Copyright (c) 2017-2021 Contributors to the Eclipse Foundation
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
import { SplitMatchTopologyService } from './sidebars/splitting-matching/split-match-topology.service';
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
import { LiveModelingService } from './services/live-modeling.service';
import { ContainerService } from './services/container.service';
import { CollapseModule, ModalModule, TooltipModule } from 'ngx-bootstrap';
import { ReqCapRelationshipService } from './services/req-cap-relationship.service';
import { WineryTableModule } from '../../../tosca-management/src/app/wineryTableModule/wineryTable.module';
import { LiveModelingActions } from './redux/actions/live-modeling.actions';
import { AngularResizedEventModule } from 'angular-resize-event';
import { OverlayComponent } from './overlay/overlay.component';
import { EdmmTransformationCheckComponent } from './edmmTransformationCheck/edmmTransformationCheck.component';
import { EdmmReplacementRulesComponent } from './edmmTransformationCheck/edmm-replacement-rules/edmm-replacement-rules.component';
import { ManageTopologyService } from './services/manage-topology.service';
import { PolicyService } from './services/policy.service';
import { SidebarModule } from 'ng-sidebar';
import { NodeDetailsSidebarComponent } from './sidebars/node-details/nodeDetailsSidebar.component';
import { RefinementSidebarComponent } from './sidebars/refinement/refinementSidebar.component';
import { GroupViewComponent } from './group-view/group-view.component';
import { TagService } from '../../../tosca-management/src/app/instance/sharedComponents/tag/tag.service';
import { WineryDynamicTableModule } from '../../../tosca-management/src/app/wineryDynamicTable/wineryDynamicTable.module';
import { WineryDuplicateValidatorModule } from '../../../tosca-management/src/app/wineryValidators/wineryDuplicateValidator.module';
import { GroupViewPoliciesComponent } from './group-view/policies/policies.component';
import { VersionSliderComponent } from './version-slider/version-slider.component';
import { NgxSliderModule } from '@angular-slider/ngx-slider';
import { VersionSliderService } from './version-slider/version-slider.service';
import { MultiParticipantsService } from './services/multi-participants.service';
import { ManageParticipantsComponent } from './participants/manage-participants.component';
import { ResearchPluginsComponent } from './sidebars/research-plugins/research-plugins.component';
import { InstanceModelComponent } from './sidebars/instanceModel/instanceModel.component';
import { WineryNamespaceSelectorService } from '../../../tosca-management/src/app/wineryNamespaceSelector/wineryNamespaceSelector.service';
import { PropertyValidatorService } from './services/property-validator.service';
import { OverlayService } from './services/overlay.service';
import { TopologyService } from './services/topology.service';
import { LoggingService } from './services/logging.service';
import { LiveModelingSidebarModule } from './live-modeling/live-modeling-sidebar.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NavbarModule } from './navbar/navbar.module';
import { PlaceholderSubstitutionComponent } from './sidebars/placeholderSubstitution/placeholderSubstitution.component';
import { PlaceholderSubstitutionWebSocketService } from './sidebars/placeholderSubstitution/placeholderSubstitutionWebSocket.service';
import { CdkAccordionModule } from '@angular/cdk/accordion';
import { MatListModule } from '@angular/material';
import { SplitMatchTopologyComponent } from './sidebars/splitting-matching/split-match-topology.component';
import { WineryLoaderModule } from '../../../tosca-management/src/app/wineryLoader/wineryLoader.module';

@NgModule({
    declarations: [
        WineryComponent,
        PaletteComponent,
        NodeDetailsSidebarComponent,
        PrintViewComponent,
        RefinementSidebarComponent,
        ProblemDetectionComponent,
        EnricherComponent,
        OverlayComponent,
        MultiParticipantsComponent,
        EdmmTransformationCheckComponent,
        VersionSliderComponent,
        GroupViewComponent,
        InstanceModelComponent,
        GroupViewPoliciesComponent,
        ManageParticipantsComponent,
        ResearchPluginsComponent,
        EdmmReplacementRulesComponent,
        PlaceholderSubstitutionComponent,
        SplitMatchTopologyComponent
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
        NgxSliderModule,
        TooltipModule.forRoot(),
        AngularResizedEventModule,
        ModalModule.forRoot(),
        LiveModelingSidebarModule,
        NavbarModule,
        CdkAccordionModule,
        MatListModule,
        WineryLoaderModule
    ],
    providers: [
        // { provide: ToastOptions, useClass: WineryCustomOption },
        JsPlumbService,
        WineryActions,
        TopologyRendererActions,
        LiveModelingActions,
        LoadedService,
        AppReadyEventService,
        BackendService,
        TagService,
        ExistsService,
        EntitiesModalService,
        ManageTopologyService,
        ImportTopologyService,
        SplitMatchTopologyService,
        ErrorHandlerService,
        StatefulAnnotationsService,
        PlaceComponentsService,
        ReqCapRelationshipService,
        PolicyService,
        VersionSliderService,
        MultiParticipantsService,
        WineryNamespaceSelectorService,
        PolicyService,
        ContainerService,
        PropertyValidatorService,
        LiveModelingService,
        ReqCapRelationshipService,
        OverlayService,
        TopologyService,
        LoggingService,
        PlaceholderSubstitutionWebSocketService
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
