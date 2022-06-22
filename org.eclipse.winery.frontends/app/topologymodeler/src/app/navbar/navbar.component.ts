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

import { Component, ElementRef, Input, OnDestroy, TemplateRef, ViewChild } from '@angular/core';
import { animate, style, transition, trigger } from '@angular/animations';
import { ToastrService } from 'ngx-toastr';
import { NgRedux } from '@angular-redux/store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { IWineryState } from '../redux/store/winery.store';
import { BackendService } from '../services/backend.service';
import { Subscription } from 'rxjs';
import { Hotkey, HotkeysService } from 'angular2-hotkeys';
import { TopologyRendererState } from '../redux/reducers/topologyRenderer.reducer';
import { WineryActions } from '../redux/actions/winery.actions';
import { StatefulAnnotationsService } from '../services/statefulAnnotations.service';
import {
    FeatureEnum
} from '../../../../tosca-management/src/app/wineryFeatureToggleModule/wineryRepository.feature.direct';
import {
    WineryRepositoryConfigurationService
} from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { TTopologyTemplate } from '../models/ttopology-template';
import { OverlayService } from '../services/overlay.service';
import { BsModalRef } from 'ngx-bootstrap';
import { TopologyService } from '../services/topology.service';
import { VersionSliderService } from '../version-slider/version-slider.service';
import { CheService } from '../services/che.service';
import { TopologyModelerConfiguration } from '../models/topologyModelerConfiguration';
import { EntityTypesModel } from '../models/entityTypesModel';

/**
 * The navbar of the topologymodeler.
 */
@Component({
    selector: 'winery-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.css'],
    animations: [
        trigger('navbarInOut', [
            transition('void => *', [
                style({ transform: 'translateY(-100%)' }),
                animate('200ms ease-out')
            ]),
            transition('* => void', [
                animate('200ms ease-in', style({ transform: 'translateY(-100%)' }))
            ])
        ])
    ]
})
export class NavbarComponent implements OnDestroy {

    @Input() hideNavBarState: boolean;
    @Input() readonly: boolean;
    @Input() templateParameter: TopologyModelerConfiguration;
    @Input() entityTypes: EntityTypesModel;

    navbarButtonsState: TopologyRendererState;
    currentTopologyTemplate: TTopologyTemplate;
    subscriptions: Array<Subscription> = [];
    exportCsarUrl: string;
    splittingOngoing: boolean;
    matchingOngoing: boolean;
    placingOngoing: boolean;
    showVersionSliderButton: boolean;
    configEnum = FeatureEnum;
    unsavedChanges: boolean;
    modalRef: BsModalRef;

    @ViewChild('exportCsarButton')
    private exportCsarButtonRef: ElementRef;
    @ViewChild('confirmModal')
    private confirmModalRef: TemplateRef<any>;

    constructor(private alert: ToastrService,
                private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions,
                private wineryActions: WineryActions,
                public backendService: BackendService,
                private statefulService: StatefulAnnotationsService,
                private hotkeysService: HotkeysService,
                private overlayService: OverlayService,
                private topologyService: TopologyService,
                public configurationService: WineryRepositoryConfigurationService,
                private versionSliderService: VersionSliderService,
                private che: CheService) {
        this.subscriptions.push(ngRedux.select((state) => state.topologyRendererState)
            .subscribe((newButtonsState) => this.setButtonsState(newButtonsState)));
        this.subscriptions.push(ngRedux.select((currentState) => currentState.wineryState.currentJsonTopology)
            .subscribe((topologyTemplate) => this.currentTopologyTemplate = topologyTemplate));
        this.subscriptions.push(ngRedux.select((currentState) => currentState.wineryState.unsavedChanges)
            .subscribe((unsavedChanges) => this.unsavedChanges = unsavedChanges));

        this.hotkeysService.add(new Hotkey('mod+s', (event: KeyboardEvent): boolean => {
            event.stopPropagation();
            this.saveTopologyTemplateToRepository();
            return false; // Prevent bubbling
        }, undefined, 'Save the Topology Template'));
        this.hotkeysService.add(new Hotkey('mod+l', (event: KeyboardEvent): boolean => {
            event.stopPropagation();
            this.ngRedux.dispatch(this.actions.executeLayout());
            return false; // Prevent bubbling
        }, undefined, 'Apply the layout directive to the Node Templates'));
        this.exportCsarUrl = this.backendService.serviceTemplateURL + '/?csar';
        this.versionSliderService.hasMultipleVersions()
            .subscribe(hasMultipleVersions => this.showVersionSliderButton = hasMultipleVersions);
    }

    /**
     * Setter for buttonstate
     * @param newButtonsState
     */
    setButtonsState(newButtonsState: TopologyRendererState): void {
        this.navbarButtonsState = newButtonsState;
        if (!this.navbarButtonsState.buttonsState.splitTopologyButton) {
            this.splittingOngoing = false;
        }
        if (!this.navbarButtonsState.buttonsState.matchTopologyButton) {
            this.matchingOngoing = false;
        }
        if (!this.navbarButtonsState.buttonsState.placeComponentsButton) {
            this.placingOngoing = false;
        }
    }

    /**
     * Getter for the style of a pressed button.
     * @param buttonPressed
     */
    getStyle(buttonPressed: boolean): string {
        if (buttonPressed) {
            return '#AAEEAA';
        }
    }

    /**
     * Exports the service template as a CSAR file
     * @param event
     * @param edmm indicates whether EDMM should be exported.
     */
    exportCsar(event, edmm?: string) {
        let url = this.exportCsarUrl;
        if (edmm) {
            url = this.backendService.serviceTemplateURL + '/?edmm';
        } else if (event.ctrlKey) {
            url = this.backendService.serviceTemplateURL + '?definitions';
        }
        window.open(url, '_blank');
    }

    /**
     * This function is called whenever a navbar button is clicked.
     * It contains a separate case for each button.
     * It toggles the `pressed` state of a button and publishes the respective
     * button id and boolean to the subscribers of the Observable inside
     * SharedNodeNavbarService.
     * @param event -- The click event of a button.
     */
    toggleButton(event) {
        event.preventDefault();
        switch (event.target.id) {
            case 'targetLocations': {
                this.ngRedux.dispatch(this.actions.toggleTargetLocations());
                break;
            }
            case 'policies': {
                this.ngRedux.dispatch(this.actions.togglePolicies());
                break;
            }
            case 'requirementsCapabilities': {
                this.ngRedux.dispatch(this.actions.toggleRequirementsCapabilities());
                break;
            }
            case 'deploymentArtifacts': {
                this.ngRedux.dispatch(this.actions.toggleDeploymentArtifacts());
                break;
            }
            case 'properties': {
                this.ngRedux.dispatch(this.actions.toggleProperties());
                this.toggleCheckNodePropertiesIfNecessary();
                break;
            }
            case 'types': {
                this.ngRedux.dispatch(this.actions.toggleTypes());
                break;
            }
            case 'edmmTransformationCheck': {
                this.ngRedux.dispatch(this.actions.toggleEdmmTransformationCheck());
                break;
            }
            case 'ids': {
                this.ngRedux.dispatch(this.actions.toggleIds());
                break;
            }
            case 'layout': {
                this.ngRedux.dispatch(this.actions.executeLayout());
                break;
            }
            case 'alignh': {
                this.ngRedux.dispatch(this.actions.executeAlignH());
                break;
            }
            case 'alignv': {
                this.ngRedux.dispatch(this.actions.executeAlignV());
                break;
            }
            case 'importTopology': {
                this.ngRedux.dispatch(this.actions.importTopology());
                break;
            }
            case 'threatModeling': {
                this.ngRedux.dispatch(this.actions.threatModeling());
                break;
            }
            case 'split': {
                this.ngRedux.dispatch(this.actions.splitTopology());
                this.splittingOngoing = true;
                break;
            }
            case 'match': {
                this.ngRedux.dispatch(this.actions.matchTopology());
                this.matchingOngoing = true;
                break;
            }
            case 'problemDetection': {
                this.ngRedux.dispatch(this.actions.detectProblems());
                break;
            }
            case 'enrichment': {
                this.ngRedux.dispatch(this.actions.enrichNodeTemplates());
                break;
            }
            case 'instanceModelRefinement': {
                this.ngRedux.dispatch(this.actions.refineInstanceModel());
                break;
            }
            case 'substituteTopology':
                this.ngRedux.dispatch(this.actions.substituteTopology());
                break;
            case 'refinePatterns':
                this.readonly = true;
                this.ngRedux.dispatch(this.wineryActions.sendPaletteOpened(false));
                this.ngRedux.dispatch(this.actions.refinePatterns());
                break;
            case 'refineTopology':
                this.readonly = true;
                this.ngRedux.dispatch(this.wineryActions.sendPaletteOpened(false));
                this.ngRedux.dispatch(this.actions.refineTopology());
                break;
            case 'refineTopologyWithTests':
                this.readonly = true;
                this.ngRedux.dispatch(this.wineryActions.sendPaletteOpened(false));
                this.ngRedux.dispatch(this.actions.addTestRefinements());
                break;
            case 'generateGDM':
                this.ngRedux.dispatch(this.actions.generatePlaceholder());
                break;
            case 'extractLDM':
                this.ngRedux.dispatch(this.actions.extractLDM());
                break;
            case 'generatePlaceholderSubs':
                this.ngRedux.dispatch(this.actions.generatePlaceholderSubs());
                break;
            case 'determineStatefulComponents':
                this.ngRedux.dispatch(this.actions.determineStatefulComponents());
                break;
            case 'determineFreezableComponents':
                this.ngRedux.dispatch(this.actions.determineFreezableComponents());
                break;
            case 'cleanFreezableComponents':
                this.ngRedux.dispatch(this.actions.cleanFreezableComponents());
                break;
            case 'placement':
                this.ngRedux.dispatch(this.actions.placeComponents());
                this.placingOngoing = true;
                break;
            case 'manageYamlPolicies':
                this.ngRedux.dispatch(this.actions.manageYamlPolicies());
                break;
            case 'versionSlider':
                this.readonly = true;
                this.ngRedux.dispatch(this.wineryActions.sendPaletteOpened(false));
                this.ngRedux.dispatch(this.actions.toggleVersionSlider());
                break;
            case 'manageYamlGroups':
                this.ngRedux.dispatch(this.actions.toggleManageYamlGroups());
                break;
            case 'yamlGroups':
                this.ngRedux.dispatch(this.actions.toggleYamlGroups());
                break;
            case 'manageParticipants':
                this.ngRedux.dispatch(this.actions.toggleManageParticipants());
                break;
            case 'assignParticipants':
                this.ngRedux.dispatch(this.actions.toggleAssignParticipants());
                break;
            case 'hideDependsOnRelations':
                this.ngRedux.dispatch(this.actions.toggleHideDependsOnRelations());
                break;
            case 'assignDeploymentTechnology':
                this.ngRedux.dispatch(this.actions.toggleAssignDeploymentTechnology());
                break;
            case 'detectPatterns':
                this.readonly = true;
                this.ngRedux.dispatch(this.wineryActions.sendPaletteOpened(false));
                this.ngRedux.dispatch(this.actions.detectPatterns());
                break;
        }
    }

    selectRelationshipType(event) {
        this.ngRedux.dispatch(this.actions.showOnlyMappingsOfSelectedType(event.target.value));
    }

    toggleCheckNodeProperties() {
        if (this.navbarButtonsState.buttonsState.propertiesButton) {
            this.ngRedux.dispatch(this.actions.toggleCheckNodeProperties());
        } else if (!this.navbarButtonsState.buttonsState.propertiesButton && !this.navbarButtonsState.buttonsState.checkNodePropertiesButton) {
            this.ngRedux.dispatch(this.actions.toggleProperties());
            this.ngRedux.dispatch(this.actions.toggleCheckNodeProperties());
        }
    }

    toggleCheckNodePropertiesIfNecessary() {
        if (!this.navbarButtonsState.buttonsState.propertiesButton && this.navbarButtonsState.buttonsState.checkNodePropertiesButton) {
            this.ngRedux.dispatch(this.actions.toggleCheckNodeProperties());
        }
    }

    /**
     * Calls the BackendService's saveTopologyTemplate method and displays a success message if successful.
     */
    saveTopologyTemplateToRepository() {
        this.overlayService.showOverlay('Saving topology template. This may take a while.');
        this.backendService.saveTopologyTemplate(this.currentTopologyTemplate)
            .subscribe((res) => {
                if (res.ok) {
                    this.alert.success('<p>Saved the topology!<br>' + 'Response Status: '
                        + res.statusText + ' ' + res.status + '</p>');
                } else {
                    this.alert.info('<p>Something went wrong! <br>' + 'Response Status: '
                        + res.statusText + ' ' + res.status + '</p>');
                }
            }, (err) => {
                this.alert.error(err.error);
            })
            .add(() => {
                this.topologyService.checkForSaveChanges();
                this.topologyService.checkForDeployChanges();
                this.overlayService.hideOverlay();
            });
    }

    /**
     * Angular lifecycle event.
     */
    ngOnDestroy() {
        this.subscriptions.forEach((subscription) => {
            subscription.unsubscribe();
        });
    }

    openManagementUi() {
        window.open(this.backendService.serviceTemplateUiUrl, '_blank');
    }

    openChe() {
        this.che.openChe(
            this.backendService.configuration.repositoryURL,
            this.backendService.configuration.id,
            this.backendService.configuration.ns,
            'servicetemplates'
        );
    }
}
