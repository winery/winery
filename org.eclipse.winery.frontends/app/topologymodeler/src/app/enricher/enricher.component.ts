/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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
import { Component } from '@angular/core';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { WineryActions } from '../redux/actions/winery.actions';
import { TopologyRendererState } from '../redux/reducers/topologyRenderer.reducer';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { TTopologyTemplate } from '../models/ttopology-template';
import { TopologyTemplateUtil } from '../models/topologyTemplateUtil';
import { EnricherService } from './enricher.service';
import { Enrichment, FeatureEntity } from './enrichmentEntity';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';

@Component({
    selector: 'winery-enricher',
    templateUrl: './enricher.component.html',
    styleUrls: ['./enricher.component.css']
})
export class EnricherComponent {

    // enrichment object containing available features
    availableFeatures: Enrichment;
    // array to store enrichment to be applied
    toApply = [];

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions,
                private wineryActions: WineryActions,
                private alert: ToastrService,
                private configurationService: WineryRepositoryConfigurationService,
                private enricherService: EnricherService) {
        this.ngRedux.select(state => state.topologyRendererState)
            .subscribe(currentButtonsState => this.checkButtonsState(currentButtonsState));
    }

    /**
     * This method checks the current button state of Winery UI to take action when the Enrichment Button was clicked.
     * @param currentButtonsState TopologyRendererState object containt state of Winery UI Buttons
     */
    private checkButtonsState(currentButtonsState: TopologyRendererState) {
        // check if Enrichment Button is clicked and available features are pulled
        if (currentButtonsState.buttonsState.enrichmentButton && !this.availableFeatures) {
            this.enricherService.getAvailableFeatures().subscribe(
                data => this.showAvailableFeatures(data),
                error => this.handleError(error)
            );
            // if button is unclicked, reset available features
        } else if (!currentButtonsState.buttonsState.enrichmentButton) {
            this.availableFeatures = null;
        }
    }

    /**
     * This method is called when the selection of a enrichment is changed.
     * It pushs/removes the selected/removed enrichment from the array of enrichments to be applied
     * @param feature: feature which changed
     * @param node: node template id where enrichment shall be applied later
     * @param event: selection changed event from checkbox
     */
    protected featureSelectionChanged(feature: FeatureEntity, node: Enrichment, event: any) {
        const isChecked = event.target.checked;
        const nodeTemplate = node.nodeTemplateId;
        // if a new feature was selected and to applicable enrichment array is empty or node template is not added yet
        if (isChecked && (this.toApply.length === 0) || !this.checkIfNodeTypeSelected(nodeTemplate)) {
            const selectedEnrichment = {
                nodeTemplateId: nodeTemplate,
                features: []
            };
            selectedEnrichment.features.push(feature);
            this.toApply.push(selectedEnrichment);
            // if feature was selected and node template id is already existing
        } else if (isChecked && this.checkIfNodeTypeSelected(nodeTemplate)) {
            this.toApply[this.checkWhichIndexNodeType(nodeTemplate)].features.push(feature);
            // if feature was unselected
        } else if (!event.target.checked && this.checkIfNodeTypeSelected(nodeTemplate)) {
            this.removeFeatureForNodeTemplate(feature, this.checkWhichIndexNodeType(nodeTemplate));
        }
    }

    /**
     * This method checks whether to applicable enrichments array already contains an entry for a given node template.
     * @param nodeTemplateId: id of node template which shall be checked
     * @return boolean: true if array already contains node template, false else
     */
    private checkIfNodeTypeSelected(nodeTemplateId: string): boolean {
        for (const element of this.toApply) {
            // if entry node template matches node template we're searching for, return true
            if (element.nodeTemplateId === nodeTemplateId) {
                return true;
            }
        }
        // if node template id was not found after iterating over all entries, return false
        return false;
    }

    /**
     * This method returns the index of an node template entry in the to applicable enrichments array.
     * This is used to determine the entry which shall be removed when unselecting the last feature of a node template.
     * @param nodeTemplateId: id of node template to be checked
     * @return i: number of index of node template entry in to applicable enrichments array
     */
    private checkWhichIndexNodeType(nodeTemplateId: string): number {
        for (let i = 0; i < this.toApply.length; i++) {
            // if node template id is found, return the index of the entry
            if (this.toApply[i].nodeTemplateId === nodeTemplateId) {
                return i;
            }
        }
    }

    /**
     * This method removes a feature of a node template entry in the to applicable enrichments array.
     * @param feature: feature entry which shall be modified
     * @param index: index of entry to be removed
     */
    private removeFeatureForNodeTemplate(feature: FeatureEntity, index: number): void {
        for (let i = 0; i < this.toApply[index].features.length; i++) {
            // check if feature is feature to be modified
            if (this.toApply[index].features[i] === feature) {
                this.toApply[index].features.splice(i, 1);
            }
        }
        // delete entry if no feature selected anymore
        if (this.toApply[index].features.length === 0) {
            this.toApply.splice(index, 1);
        }
    }

    /**
     * This method is called when clicking the "Apply" button.
     * It starts the Enricher Service to apply the selected enrichments.
     */
    protected applyEnrichment() {
        this.enricherService.applySelectedFeatures(this.toApply).subscribe(
            data => this.enrichmentApplied(data),
            error => this.handleError(error)
        );
    }

    /**
     * This method is called when available features are retrieved and fills the available features array with the
     * gathered data.
     * @param data: json response of backend containing available features for all node templates
     */
    private showAvailableFeatures(data: Enrichment): void {
        // check if array contains data at all (data != null does not work, as data is not null but an empty array)
        if (data.length > 0) {
            this.availableFeatures = data;
        } else {
            this.alert.info('No enrichment found!');
        }
    }

    /**
     * This method is called when an error occurs durring fetching or pushing the enrichments.
     * It alerts the merror message in the UI.
     * @param error: error message
     */
    private handleError(error: HttpErrorResponse) {
        this.alert.error(error.message);
    }

    /**
     * This method is called when the User hovers over a node template in the enrichment sidebar
     * It highlights the respective node template in the topology modeler.
     * @param entry: entry of available features displayed in the UI
     */
    protected onHoverOver(entry: Enrichment) {
        const nodeTemplateIds: string[] = [];
        nodeTemplateIds.push(entry.nodeTemplateId);
        this.ngRedux.dispatch(this.actions.highlightNodes(nodeTemplateIds));
    }

    /**
     * This method is called when the user hovers out of a node template.
     */
    protected hoverOut() {
        this.ngRedux.dispatch(this.actions.highlightNodes([]));
    }

    /**
     * This method is called when the User clicks "Cancel".
     * It resets the available features, which lets the enrichment sidebar disappear.
     */
    protected cancel() {
        this.availableFeatures = null;
        this.ngRedux.dispatch(this.actions.enrichNodeTemplates());
    }

    /**
     * This method is called when the enrichment is successfully applied in the backend.
     * It updates the topology template then, resets the available features and displays an success message.
     * @param data: topology template that was updated
     */
    private enrichmentApplied(data: TTopologyTemplate) {
        TopologyTemplateUtil.updateTopologyTemplate(this.ngRedux, this.wineryActions, data, this.configurationService.isYaml());
        // reset available features since they are no longer valid
        this.availableFeatures = null;
        this.alert.success('Updated Topology Template!');
        this.ngRedux.dispatch(this.actions.enrichNodeTemplates());
    }
}
