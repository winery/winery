/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { PlansApiData } from './plansApiData';
import { PlansService } from './plans.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { isNullOrUndefined } from 'util';
import { SelectData } from '../../../wineryInterfaces/selectData';
import { WineryUploaderComponent } from '../../../wineryUploader/wineryUploader.component';
import { SelectItem } from 'ng2-select';
import { InputParameters, OutputParameters } from '../../../wineryInterfaces/parameters';
import { backendBaseURL, hostURL } from '../../../configuration';
import { InstanceService } from '../../instance.service';

@Component({
    selector: 'winery-plans',
    templateUrl: 'plans.component.html',
    providers: [
        PlansService
    ]
})
export class PlansComponent implements OnInit {

    loading = true;

    embeddedPlansColumns: Array<WineryTableColumn> = [
        {title: 'Precondition', name: 'precondition', sort: true},
        {title: 'Name', name: 'name', sort: true},
        {title: 'Type', name: 'planType', sort: true},
        {title: 'Language', name: 'planLanguage', sort: true}
    ];
    linkedPlansColumns: Array<WineryTableColumn> = [
        {title: 'Precondition', name: 'precondition', sort: true},
        {title: 'Name', name: 'name', sort: true},
        {title: 'Type', name: 'planType', sort: true},
        {title: 'Language', name: 'planLanguage', sort: true},
        {title: 'Reference', name: 'planModelReference.reference', sort: true}
    ];

    plansApiData: PlansApiData[] = null;
    linkedPlans: any[] = null;
    elementToRemove: PlansApiData;

    planTypes: SelectData[];
    selectedPlanType: SelectData = new SelectData();
    planLanguages: SelectData[];
    selectedPlanLanguage: SelectData = new SelectData();
    newPlan = new PlansApiData();
    fileDropped = false;
    showArchiveUpload = true;
    fileToUpload: any;
    uploaderUrl: string;

    @ViewChild('addPlanModal') addPlanModal: any;
    @ViewChild('uploader') uploader: WineryUploaderComponent;

    @ViewChild('ioModal') ioModal: any;
    @ViewChild('confirmDeleteModal') confirmDeleteModal: any;

    constructor(private notify: WineryNotificationService,
                private sharedData: InstanceService,
                private service: PlansService) {
    }

    ngOnInit() {
        this.getPlanTypesData();
        this.uploaderUrl = this.service.path + 'addarchive/';
    }

    // region ########## Callbacks ##########
    // region ########## Table buttons ##########
    onAddPlanType() {
        this.refreshPlanLanguages();
        this.refreshPlanTypes();
        this.newPlan = new PlansApiData();
        this.fileToUpload = null;
        this.fileDropped = false;
        this.showArchiveUpload = true;
    }

    onEditPlan(plan: PlansApiData) {
        const bpmnUrl = hostURL + '/bpmn4tosca/'
            + '?repositoryURL=' + encodeURIComponent(backendBaseURL + '/')
            + '&namespace=' + encodeURIComponent(this.sharedData.selectedNamespace)
            + '&id=' + this.sharedData.selectedResource
            + '&plan=' + plan.name;
        window.open(bpmnUrl, '_blank');
    }

    onRemovePlan(plan: PlansApiData) {
        this.elementToRemove = plan;
        this.confirmDeleteModal.show();
    }

    onEditPlanIOParameters(selectedType: PlansApiData) {
        this.newPlan = selectedType;
        if (isNullOrUndefined(this.newPlan.inputParameters)) {
            this.newPlan.inputParameters = new InputParameters();
        }
        if (isNullOrUndefined(this.newPlan.outputParameters)) {
            this.newPlan.outputParameters = new OutputParameters();
        }
        this.ioModal.show();
    }

    // endregion

    // region ########## Add Modal ##########
    addPlan() {
        this.newPlan.planLanguage = this.selectedPlanLanguage.id;
        this.newPlan.planType = this.selectedPlanType.id;

        this.service.addPlan(this.newPlan)
            .subscribe(
                () => this.handlePlanCreated(),
                error => this.handleError(error)
            );
    }

    refreshPlanTypes() {
        this.service.getPlanTypes()
            .subscribe(
                data => this.handlePlanTypes(data),
                error => this.handleError(error)
            );
    }

    refreshPlanLanguages() {
        this.service.getPlanLanguages()
            .subscribe(
                data => this.handlePlanLanguages(data),
                error => this.handleError(error)
            );
    }

    planLanguageSelected(event: SelectItem) {
        if (event.id.includes('http://www.opentosca.org/bpmn4tosca')) {
            this.fileDropped = true;
            this.showArchiveUpload = false;
        } else if (!isNullOrUndefined(this.fileToUpload)) {
            this.fileDropped = true;
            this.showArchiveUpload = true;
        } else {
            this.fileDropped = false;
            this.showArchiveUpload = true;
        }

        this.selectedPlanLanguage = event;
    }

    planTypeSelected(event: SelectItem) {
        this.selectedPlanType = event;
    }

    onFileDropped(event: any) {
        this.fileDropped = true;
        this.fileToUpload = event;
    }

    // endregion

    // region ######### IOParameter Modal #########
    editPlan() {
        this.service.updatePlan(this.newPlan)
            .subscribe(
                () => this.handlePlanSaved(),
                error => this.handleError(error)
            );
    }

    // endregion

    // region ########## Remove Modal ##########
    deletePlan() {
        this.loading = true;
        this.service.deletePlan(this.elementToRemove.id)
            .subscribe(
                () => {
                    this.notify.success('Successfully deleted plan ' + this.elementToRemove.name);
                    this.getPlanTypesData();
                },
                error => this.handleError(error)
            );
    }

    getPlanTypesData() {
        this.service.getPlansData()
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }
    // endregion
    // endregion

    // region ########## Private Methods ##########
    private handleData(data: PlansApiData[]) {
        this.plansApiData = data;
        this.loading = false;
    }

    private handlePlanTypes(types: SelectData[]) {
        this.planTypes = types;
        this.selectedPlanType = isNullOrUndefined(types[0]) ? new SelectData() : types[0];

        if (!isNullOrUndefined(this.planLanguages)) {
            this.loading = false;
            this.addPlanModal.show();
        }
    }

    private handlePlanLanguages(languages: SelectData[]) {
        this.planLanguages = languages;
        this.selectedPlanLanguage = isNullOrUndefined(languages[0]) ? new SelectData() : languages[0];

        if (!isNullOrUndefined(this.planTypes)) {
            this.loading = false;
            this.addPlanModal.show();
        }
    }

    private handlePlanCreated() {
        this.loading = true;
        this.uploaderUrl = this.service.path + '/' + this.newPlan.name + '/file';
        if (!this.showArchiveUpload) {
            this.handlePlanSaved();
        } else {
            this.uploader.upload(this.uploaderUrl);
        }
    }

    private handlePlanSaved() {
        this.loading = false;
        this.notify.success('Successfully added Plan!');
        this.getPlanTypesData();
    }

    private handleError(error: any) {
        this.notify.error(error);
        this.loading = false;
    }

    // endregion
}
