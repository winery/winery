/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { Property, PropertyMappingsApiData, PropertyMappingService } from './propertyMappings.service';
import { WineryTableColumn } from '../../../../wineryTableModule/wineryTable.component';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { ModalDirective } from 'ngx-bootstrap';
import { NgForm } from '@angular/forms';

@Component({
    selector: 'winery-instance-boundary-property-mappings',
    templateUrl: 'propertyMappings.component.html',
    providers: [PropertyMappingService]
})
export class PropertyMappingsComponent implements OnInit {

    loading = true;
    apiData: PropertyMappingsApiData;
    columns: Array<WineryTableColumn> = [
        {title: 'Service Template Property', name: 'serviceTemplatePropertyRef', sort: true},
        {title: 'Target', name: 'targetObjectRef', sort: true},
        {title: 'Target Property', name: 'targetPropertyRef', sort: true}
    ];
    @ViewChild('addPropertyMappingModal') addPropertyMappingModal: ModalDirective;
    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('browseForServiceTemplatePropertyDiag') browseForServiceTemplatePropertyDiag: ModalDirective;
    @ViewChild('propertyMappingForm') propertyMappingForm: NgForm;
    currentSelectedItem: Property;
    addOrUpdateBtnTxt = 'Add';

    constructor(private service: PropertyMappingService,
                private notify: WineryNotificationService) {
    }

    ngOnInit() {
        this.getMappings();
    }

    getMappings() {
        this.service.getPropertyMappings().subscribe(
            data => {
                this.handleData(data);
            },
            error => this.notify.error(error.toString())
        );
    }

    handleData(data: any) {
        this.apiData = data;
        this.apiData.propertyMappings.propertyMapping = this.apiData.propertyMappings.propertyMapping.map(
            obj => {
                if (obj.targetObjectRef === null) {
                    obj.targetObjectRef = '';
                } else {
                    obj.targetObjectRef = obj.targetObjectRef.id;
                }
                return obj;
            }
        );
        this.loading = false;
    }

    onCellSelected(selectedItem: any) {
        this.currentSelectedItem = selectedItem.row;
    }

    removeConfirmed() {
        this.service.removePropertyMapping(this.currentSelectedItem.serviceTemplatePropertyRef).subscribe(
            data => this.handleSuccess('Deleted property mapping'),
            error => this.handleError(error)
        );
    }

    onRemoveClick(elementToRemove: Property) {
        if (!isNullOrUndefined(elementToRemove) && !isNullOrUndefined(this.currentSelectedItem)) {
            this.confirmDeleteModal.show();
        } else {
            this.notify.warning('No Element was selected!');
        }
    }

    onAddClick() {
        this.addOrUpdateBtnTxt = 'Add';
        this.propertyMappingForm.reset();
        this.currentSelectedItem = null;
        this.addPropertyMappingModal.show();
    }

    onEditClick() {
        this.addOrUpdateBtnTxt = 'Update';
        this.addPropertyMappingModal.show();
    }

    addPropertyMapping(serviceTemplateProp: string, targetObj: string, targetProp: string) {
        this.service.addPropertyMapping(
            {
                serviceTemplatePropertyRef: serviceTemplateProp,
                targetObjectRef: targetObj,
                targetPropertyRef: targetProp
            }
        ).subscribe(
            data => this.handleSuccess('Added new property mapping'),
            error => this.handleError(error)
        );
        this.addPropertyMappingModal.hide();
    }

    handleSuccess(message: string) {
        this.getMappings();
        this.notify.success(message);
    }

    handleError(error: Error) {
        this.notify.error(error.toString());
    }
}
