/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
import { Component, OnInit, ViewChild } from '@angular/core';
import { PoliciesService, WineryPolicy } from './policies.service';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { WineryTableColumn } from '../../../../wineryTableModule/wineryTable.component';
import { ModalDirective } from 'ngx-bootstrap';
import { isNullOrUndefined } from 'util';
import { WineryValidatorObject } from '../../../../wineryValidators/wineryDuplicateValidator.directive';
import { SelectItem } from 'ng2-select';
import { EditXMLComponent } from '../../../sharedComponents/editXML/editXML.component';
import { Response } from '@angular/http';

@Component({
    templateUrl: 'policies.component.html',
    providers: [
        PoliciesService
    ]
})
export class PoliciesComponent implements OnInit {

    loading = true;
    loadingTemplate = false;
    policies: Array<WineryPolicy> = [];
    newPolicy: WineryPolicy = new WineryPolicy();
    policyTypes: Array<SelectItem> = [];
    policyTemplates: Array<SelectItem> = [];
    activePolicyType: SelectItem = new SelectItem('');
    activePolicyTemplate: SelectItem = new SelectItem('');

    columnsArray: Array<WineryTableColumn> = [
        {title: 'Name', name: 'name'},
        {title: 'Type', name: 'policyType'},
        {title: 'Template', name: 'policyRef'}
    ];
    selectedCell: WineryPolicy;
    validator: WineryValidatorObject;
    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('addModal') addModal: ModalDirective;
    @ViewChild('xmlEditor') xmlEditor: EditXMLComponent;

    policyXml = '<Policy xsi:nil="true" xmlns="http://docs.oasis-open.org/tosca/ns/2011/12"'
        + ' xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n</Policy>';

    constructor(private service: PoliciesService, private notify: WineryNotificationService) {

    }

    ngOnInit(): void {
        this.loading = true;
        this.service.getPolicies()
            .subscribe(
                data => this.handlePolicies(data),
                error => this.handleError(error)
            );
        this.service.getPolicyTypes()
            .subscribe(
                data => this.policyTypes = data,
                error => this.handlePolicyTypesError(error)
            );
    }

    add() {
        if (!isNullOrUndefined(this.policyTypes[0])) {
            this.newPolicy = new WineryPolicy();
            this.validator = new WineryValidatorObject(this.policies, 'name');
            this.activePolicyType = this.policyTypes[0].children[0];
            this.activePolicyTemplate = new SelectItem('');
            this.xmlEditor.setEditorContent(this.policyXml);
            this.addModal.show();
            this.loadTemplates();
        } else {
            this.notify.warning('Please create a Policy Type first.', 'No Policy Types');
        }
    }

    selected(item: WineryPolicy) {
        this.selectedCell = item;
    }

    remove() {
        if (!isNullOrUndefined(this.selectedCell)) {
            this.confirmDeleteModal.show();
        } else {
            this.notify.warning('You need to select a row to remove!', 'Nothing selected');
        }
    }

    /**
     * Because the user is used to work with XML to apply more functionality to a specific policy,
     * we work with XML here too.
     */
    addConfirmed() {
        let xml = this.xmlEditor.getEditorContent();
        const list = xml.split('>');

        list[0] += ' name="' + this.newPolicy.name + '"';

        const pType = this.activePolicyType.id.slice(1).split('}');
        list[0] += ' xmlns:ns10="' + pType[0] + '" '
            + 'policyType="ns10:' + pType[1] + '"';

        if (this.activePolicyTemplate.id !== '') {
            const pTemp = this.activePolicyTemplate.id.slice(1).split('}');
            list[0] += ' xmlns:ns11="' + pTemp[0] + '" '
                + 'policyRef="ns11:' + pTemp[1] + '"';
        }

        xml = list[0] + '>';
        // Append the missing '>' to complete the xml again.
        // Because there is an empty part at the last position, ignore the last item in the list.
        for (let i = 1; i < list.length - 1; i++) {
            xml += list[i] + '>';
        }

        this.service.postPolicy(xml)
            .subscribe(
                data => this.handleSaveDelete('added'),
                error => this.handleError(error)
            );
    }

    removeConfirmed() {
        this.service.deletePolicy(this.selectedCell.id)
            .subscribe(
            data => this.handleSaveDelete('deleted'),
            error => this.handleError(error)
        );
    }

    policyTypeSelected(data: SelectItem) {
        this.activePolicyType = data;
        this.loadTemplates();
    }

    policyTemplateSelected(data: SelectItem) {
        this.activePolicyTemplate = data;
    }

    private loadTemplates() {
        this.loadingTemplate = true;
        this.service.getPolicyTemplatesForType(this.activePolicyType)
            .subscribe(
                d => this.handleTemplates(d),
                error => this.handleError(error)
            );
    }

    private handlePolicies(data: WineryPolicy[]) {
        this.loading = false;
        this.policies = data;
    }

    private handleTemplates(data: SelectItem[]) {
        this.policyTemplates = data;
        this.activePolicyTemplate = new SelectItem('');
        this.loadingTemplate = false;
    }

    private handleSaveDelete(type: string) {
        this.loading = false;
        this.notify.success('Successfully ' + type + ' policy!', type);
        this.ngOnInit();
    }

    private handleError(error: any) {
        this.loading = false;
        this.notify.error(error);
    }

    private handlePolicyTypesError(error: Response) {
        if (error.status === 404) {
            // warns the user if there are nor policy types available -> send warning now
            this.add();
        } else {
            this.handleError(error);
        }
    }
}
