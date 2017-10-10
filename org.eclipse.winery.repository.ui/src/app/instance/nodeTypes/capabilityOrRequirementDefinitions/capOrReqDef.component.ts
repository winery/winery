/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { CapabilityOrRequirementDefinitionsService } from './capOrReqDef.service';
import {
    CapabilityOrRequirementDefinition, CapOrRegDefinitionsResourceApiData, CapOrReqDefinition, Constraint
} from './capOrReqDefResourceApiData';
import { CapOrRegDefinitionsTableData } from './CapOrReqDefTableData';
import { NameAndQNameApiData, NameAndQNameApiDataList } from '../../../wineryQNameSelector/wineryNameAndQNameApiData';
import { Router } from '@angular/router';
import { WineryTableColumn } from '../../../wineryTableModule/wineryTable.component';
import { TypeWithShortName } from '../../admin/typesWithShortName/typeWithShortName.service';
import { SelectData } from '../../../wineryInterfaces/selectData';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { ModalDirective } from 'ngx-bootstrap';
import { SpinnerWithInfinityComponent } from '../../../winerySpinnerWithInfinityModule/winerySpinnerWithInfinity.component';

@Component({
    selector: 'winery-instance-cap-or-req-definitions',
    templateUrl: 'capOrReqDef.html',
    styleUrls: ['capOrReqDef.style.css'],
    providers: [
        CapabilityOrRequirementDefinitionsService
    ]
})
export class CapOrReqDefComponent implements OnInit {

    columns: Array<WineryTableColumn> = [
        { title: 'Name', name: 'name' },
        { title: 'Type', name: 'type' },
        { title: 'Lower Bound', name: 'lowerBound' },
        { title: 'Upper Bound', name: 'upperBound' },
        { title: 'Constraints', name: 'constraints', sort: false },
    ];

    elementToRemove: CapOrRegDefinitionsTableData = null;
    loading = true;
    resourceApiData: CapOrRegDefinitionsResourceApiData = null;
    tableData: Array<CapOrRegDefinitionsTableData> = [];
    capabilityTypesList: NameAndQNameApiDataList = { classes: null };
    capOrReqDefToBeAdded: CapOrReqDefinition = null;
    noneSelected = true;

    editorHeight = 200;

    defaultConstraintDataModel = `<tosca:Constraint xmlns:tosca="http://docs.oasis-open.org/tosca/ns/2011/12">
##
</tosca:Constraint>
`;
    constraintDataModel = '';

    // data for show constraint list modal
    noConstraintsExistingFlag = false;
    loadingConstraints = true;
    constraintTypeItems: SelectData[];
    activeTypeElement: SelectData;
    constraintList: Array<Constraint> = null;

    // data for edit specific constraint modal
    createNewConstraintFlag = true;
    activeCapOrRegDefinition: CapabilityOrRequirementDefinition;
    activeConstraint: Constraint;
    constraintTypes: Array<TypeWithShortName> = null;

    @Input() types = '';
    addCapOrRegModalTitle = '';

    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('editConModal') editConModal: ModalDirective;
    @ViewChild('editNewConModal') editNewConModal: ModalDirective;
    @ViewChild('lowerBoundSpinner') lowerBoundSpinner: SpinnerWithInfinityComponent;
    @ViewChild('upperBoundSpinner') upperBoundSpinner: SpinnerWithInfinityComponent;
    @ViewChild('editor') editor: any;

    constructor(private service: CapabilityOrRequirementDefinitionsService,
                private notify: WineryNotificationService,
                private router: Router) {
        this.capOrReqDefToBeAdded = new CapOrReqDefinition();

        this.activeTypeElement = new SelectData();
        this.activeTypeElement.text = '';
        this.activeTypeElement.id = '';
    }

    // region ########## Angular Callbacks ##########

    ngOnInit() {
        this.getCapOrReqDefinitionsResourceApiData();
        if (this.router.url.includes('capabilitydefinitions')) {
            this.types = 'capabilitytypes';
            this.addCapOrRegModalTitle = 'Add Capability Definition';
        } else {
            this.types = 'requirementtypes';
            this.addCapOrRegModalTitle = 'Add Requirement Definition';
        }

        this.getAllCapOrReqTypes(this.types);

    }

    // endregion

    // region ########## Table Callbacks ##########

    onSelectedValueChanged(value: string) {
        this.capOrReqDefToBeAdded.type = value;
        this.noneSelected = this.capOrReqDefToBeAdded.type === '(none)';
    }

    /**
     * Called after cell of table is selected
     * @param data of the selected cell
     */
    onCellSelected(data: any) {
        switch (data.column) {
            case 'constraints': {
                for (const capOrRegDefinition of this.resourceApiData.capOrRegDefinitionsList) {
                    if (data.row.name === capOrRegDefinition.name) {
                        this.getConstraints(capOrRegDefinition);
                        this.getConstraintTypes();
                        this.editConstraints(capOrRegDefinition);
                    }
                }
                break;
            }
            case 'type': {
                for (const entry of this.tableData) {
                    if (data.row.name === entry.name) {
                        this.router.navigate([entry.typeUri]);
                    }
                }
                break;
            }
            default: {
                // EXTRA: add cell highlighting
                break;
            }
        }
    }

    private capOrReqTypeToHref(type: string): string {
        const name = type.split('}').pop();
        const namespaceEncoded: string = encodeURIComponent(encodeURIComponent(
            type.substring(type.lastIndexOf('{') + 1, type.lastIndexOf('}'))
        ));
        const absoluteURL = '/#/' + this.types + '/' + namespaceEncoded + '/' + name;
        return '<a href="' + absoluteURL + '">' + name + '</a>';
    }

    private getTypeURI(type: string): string {
        const name = type.split('}').pop();
        const namespaceEncoded: string = encodeURIComponent(
            type.substring(type.lastIndexOf('{') + 1, type.lastIndexOf('}'))
        );
        return '/' + this.types + '/' + namespaceEncoded + '/' + name;
    }

    private prepareTableData(apidata: CapOrRegDefinitionsResourceApiData) {
        this.tableData = [];
        for (const entry of apidata.capOrRegDefinitionsList) {
            const name = entry.name;
            const lowerBound = entry.lowerBound;
            const upperBound = entry.upperBound === 'unbounded' ? '∞' : entry.upperBound;
            const type = this.capOrReqTypeToHref(isNullOrUndefined(entry.capabilityType)
            === false ? entry.capabilityType : entry.requirementType);
            const constraint = '<button class="btn btn-xs" style="pointer-events: none;">Constraint...</button>';
            const typeUri = this.getTypeURI(isNullOrUndefined(entry.capabilityType)
            === false ? entry.capabilityType : entry.requirementType);

            this.tableData.push(new CapOrRegDefinitionsTableData(name, type, lowerBound, upperBound, constraint, typeUri));
        }
        this.handleSuccess();
    }

    /**
     * Opens show constraint list dialog of selected capability definition
     * @param capDefinition to which the constraints are to be displayed
     */
    editConstraints(capDefinition: CapabilityOrRequirementDefinition) {
        if (isNullOrUndefined(this.constraintList)) {
            this.noConstraintsExistingFlag = true;
            this.activeCapOrRegDefinition = capDefinition;
        } else {
            this.noConstraintsExistingFlag = false;
            this.activeCapOrRegDefinition = capDefinition;
        }
        this.editConModal.show();
    }

    /**
     * Event called if other constraint types are selected
     * @param value of the active selected constraint type
     */
    public constraintTypeSelected(value: SelectData): void {
        this.activeTypeElement = value;
    }

    /**
     * handler for clicks on remove button
     * @param capOrReqDefinition which is to be deleted
     */
    onRemoveClick(capOrReqDefinition: CapOrRegDefinitionsTableData) {
        if (isNullOrUndefined(capOrReqDefinition)) {
            return;
        } else {
            this.elementToRemove = capOrReqDefinition;
            this.confirmDeleteModal.show();
        }
    }

    /**
     * handler for clicks on the add button
     */
    onAddClick() {
        this.addModal.show();
    }

    // endregion

    // region ########## Modal Callbacks ##########

    /**
     * Adds a Capability to the table and model
     *
     */
    addCapability() {
        this.addModal.hide();
        this.capOrReqDefToBeAdded.lowerBound = this.lowerBoundSpinner.value;
        if (this.upperBoundSpinner.value === '∞') {
            this.capOrReqDefToBeAdded.upperBound = 'unbounded';
        } else {
            this.capOrReqDefToBeAdded.upperBound = this.upperBoundSpinner.value;
        }
        this.addNewCapability(this.capOrReqDefToBeAdded);
    }

    removeConfirmed() {
        this.confirmDeleteModal.hide();
        this.deleteCapOrReqDef(this.elementToRemove);
        this.elementToRemove = null;
    }

    /**
     * Open edit constraint modal with selected constraint
     * If no constraint is selected a new constraint is created
     * @param constraint to be edited
     */
    openEditConstraintModal(constraint?: Constraint) {
        const re = /\#\#/;
        const xmlDef = /<\?xml.*>\n/;

        let constraintTypeElement: SelectData = null;
        let constraintEditorContent = this.defaultConstraintDataModel;

        if (!isNullOrUndefined(constraint)) {
            this.activeConstraint = constraint;
            this.createNewConstraintFlag = false;
            constraintEditorContent = this.defaultConstraintDataModel.replace(re, constraint.any).replace(xmlDef, '');
            constraintTypeElement = this.constraintTypeItems.filter(item => item.id === this.activeConstraint.constraintType)[0];
        } else {
            this.activeConstraint = new Constraint();
            this.createNewConstraintFlag = true;
            constraintEditorContent = this.defaultConstraintDataModel.replace(re, ' \n');
        }

        // check if constraint type exists and is defined
        if (isNullOrUndefined(constraintTypeElement)) {
            constraintTypeElement = new SelectData();
            constraintTypeElement.text = '';
            constraintTypeElement.id = '';
            this.notify.warning('Please select a valid constraint type!');
        }

        this.constraintDataModel = constraintEditorContent;
        this.activeTypeElement = constraintTypeElement;
        this.editNewConModal.show();
    }

    /**
     * Delete selected constraint and return to constraint list modal
     * @param constraint to be deleted
     */
    deleteSelectedConstraint(constraint: Constraint) {
        for (const con of this.constraintList) {
            if (con === constraint) {
                const index1: number = this.constraintList.indexOf(name);
                this.constraintList.splice(index1, 1);
                if (this.constraintList.length === 0) {
                    this.noConstraintsExistingFlag = true;
                }
                this.deleteConstraint(this.activeCapOrRegDefinition, constraint);
            }
        }
    }

    /**
     *  Method to refresh constraint type selection
     *  to prevent select failures -> select is set to empty selection
     */
    refreshConstraintTypeSelector(): void {
        this.getConstraintTypes();

        const constraintTypeElement: SelectData = this.constraintTypeItems
            .filter(item => item.id === this.activeConstraint.constraintType)[0];

        if (isNullOrUndefined(constraintTypeElement)) {
            this.activeTypeElement = new SelectData();
            this.activeTypeElement.text = '';
            this.activeTypeElement.id = '';
            this.notify.warning('No valid constraint type is selected');
        } else {
            this.activeTypeElement = constraintTypeElement;
        }
    }

    /**
     * Callback to update selected constraint
     */
    updateSelectedConstraint(): void {
        const reStart = /(<tosca:Constraint.*")/i;
        const constraintData: string = this.editor.getData().replace(reStart, '$1' + ' constraintType="' + this.activeTypeElement.id + '"');
        this.activeConstraint.constraintType = this.activeTypeElement.id;
        this.updateConstraint(this.activeCapOrRegDefinition, this.activeConstraint, constraintData);
    }

    /**
     * Callback to create current constraint
     */
    createSelectedConstraint(): void {
        const reStart = /(<tosca:Constraint.*")/i;
        const constraintData: string = this.editor.getData().replace(reStart, '$1' + ' constraintType="' + this.activeTypeElement.id + '"');

        this.createConstraint(this.activeCapOrRegDefinition, constraintData);
    }

    // endregion

    // region ########## Service Callbacks ##########

    private deleteConstraint(capabilityOrRequirementDefinition: CapabilityOrRequirementDefinition, constraint: Constraint) {
        this.service.deleteConstraint(capabilityOrRequirementDefinition.name, constraint.id)
            .subscribe(
                data => this.handleDeleteConstraint(),
                error => this.handleError(error)
            );
    }

    private updateConstraint(capabilityOrRequirementDefinition: CapabilityOrRequirementDefinition,
                             constraint: Constraint, constraintData: string) {
        this.service.updateConstraint(capabilityOrRequirementDefinition.name, constraint.id, constraintData)
            .subscribe(
                data => this.handleUpdateConstraint(data),
                error => this.handleError(error)
            );
    }

    private createConstraint(capabilityOrRequirementDefinition: CapabilityOrRequirementDefinition, constraintData: string) {
        this.service.createConstraint(capabilityOrRequirementDefinition.name, constraintData)
            .subscribe(
                data => this.handleCreateConstraint(data),
                error => this.handleError(error)
            );
    }

    private getConstraints(capabilityOrRequirementDefinition: CapabilityOrRequirementDefinition): void {
        this.loadingConstraints = true;
        this.service.getConstraints(capabilityOrRequirementDefinition.name).subscribe(
            data => this.handleGetConstraints(data),
            error => this.handleError(error.toString())
        );
    }

    private handleUpdateConstraint(data: string): void {
        this.notify.success('Constraint Updated!');
        this.activeConstraint.id = data;
        this.editNewConModal.hide();
    }

    private handleCreateConstraint(data: string): void {
        this.notify.success('Constraint Created!');
        this.activeConstraint.id = data;
        this.editNewConModal.hide();
        this.getConstraints(this.activeCapOrRegDefinition);
        this.editConModal.show();
    }

    private handleGetConstraints(data: Constraint[]): void {
        this.constraintList = data;
        this.noConstraintsExistingFlag = isNullOrUndefined(data) || data.length === 0;
        this.loadingConstraints = false;
    }

    private handleDeleteConstraint() {
        this.notify.success('Constraint deleted!');
        this.getConstraints(this.activeCapOrRegDefinition);
    }

    private getConstraintTypes(): void {
        this.service.getConstraintTypes().subscribe(
            data => this.handleConstraintTypeData(data),
            error => this.handleError(error.toString())
        );
    }

    private addNewCapability(capOrReqDef: CapOrReqDefinition): void {
        this.loading = true;
        this.service.sendPostRequest(capOrReqDef).subscribe(
            data => this.handlePostResponse(),
            error => this.handleError(error)
        );
    }

    private getCapOrReqDefinitionsResourceApiData(): void {
        this.loading = true;
        this.service.getCapOrReqDefinitionsData().subscribe(
            data => this.handleCapabilityDefinitionsData(data),
            error => this.handleError(error)
        );
    }

    private handleCapabilityDefinitionsData(data: CapabilityOrRequirementDefinition[]): void {
        this.resourceApiData = new CapOrRegDefinitionsResourceApiData();
        this.resourceApiData.capOrRegDefinitionsList = data;
        this.prepareTableData(this.resourceApiData);

    }

    private getAllCapOrReqTypes(types: string): void {
        this.service.getAllCapOrReqTypes(types).subscribe(
            data => this.handleCapOrReqTypesData(data),
            error => this.handleError(error)
        );
    }

    private handlePostResponse() {
        let notification = '';
        if (this.types === 'capabilitytypes') {
            notification = 'New Capability added!';
        } else {
            notification = 'New Requirement added!';
        }
        this.notify.success(notification);
        this.getCapOrReqDefinitionsResourceApiData();

    }

    private handleCapOrReqTypesData(data: NameAndQNameApiData[]) {
        this.capabilityTypesList.classes = data;
        this.handleSuccess();
    }

    private handleConstraintTypeData(data: TypeWithShortName[]): void {
        this.constraintTypes = data;
        this.constraintTypeItems = [];

        for (const entry of this.constraintTypes) {
            const item: SelectData = new SelectData();
            item.id = entry.type;
            item.text = entry.shortName;
            this.constraintTypeItems.push(item);
        }
    }

    private deleteCapOrReqDef(elementToRemove: CapOrRegDefinitionsTableData) {
        this.service.deleteCapOrReqDef(elementToRemove.name)
            .subscribe(
                data => this.handleCapOrReqDelete(),
                error => this.handleError(error)
            );
    }

    private handleCapOrReqDelete() {
        let notification = '';
        if (this.types === 'capabilitytypes') {
            notification = 'Capability deleted!';
        } else {
            notification = 'Requirement deleted!';
        }
        this.notify.success(notification);
        this.getCapOrReqDefinitionsResourceApiData();
    }

    /**
     * Set loading to false and show success notification.
     */
    private handleSuccess(): void {
        this.loading = false;
    }

    /**
     * Sets loading to false and shows error notification.
     *
     * @param error notification to be shown
     */
    private handleError(error: any): void {
        this.loading = false;
        this.notify.error(error);
    }

    // endregion

}
