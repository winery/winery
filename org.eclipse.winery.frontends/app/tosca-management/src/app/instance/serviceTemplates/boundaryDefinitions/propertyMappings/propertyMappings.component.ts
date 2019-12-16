/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { Component, OnInit, ViewChild } from '@angular/core';
import { Property, PropertyMappingsApiData, PropertyMappingService } from './propertyMappings.service';
import { WineryRowData, WineryTableColumn } from '../../../../wineryTableModule/wineryTable.component';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { ModalDirective } from 'ngx-bootstrap';
import { NgForm } from '@angular/forms';
import { InstanceService } from '../../../instance.service';
import { WineryTemplate, WineryTopologyTemplate } from '../../../../model/wineryComponent';
import { ServiceTemplateTemplateTypes, ToscaTypes } from '../../../../model/enums';
import { Utils } from '../../../../wineryUtils/utils';
import { SelectItem } from 'ng2-select';
import { PropertiesDefinitionsResourceApiData } from '../../../sharedComponents/propertiesDefinition/propertiesDefinitionsResourceApiData';
import { SelectData } from '../../../../model/selectData';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'winery-instance-boundary-property-mappings',
    templateUrl: 'propertyMappings.component.html',
    providers: [PropertyMappingService]
})
export class PropertyMappingsComponent implements OnInit {

    readonly templatesEnum = ServiceTemplateTemplateTypes;

    loading = true;
    targetTypeSelected = false;
    apiData: PropertyMappingsApiData;
    columns: Array<WineryTableColumn> = [
        { title: 'Service Template Property', name: 'serviceTemplatePropertyRef', sort: true },
        { title: 'Target', name: 'targetObjectRef', sort: true },
        { title: 'Target Property', name: 'targetPropertyRef', sort: true }
    ];
    @ViewChild('addPropertyMappingModal') addPropertyMappingModal: ModalDirective;
    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;
    @ViewChild('browseForServiceTemplatePropertyDiag') browseForServiceTemplatePropertyDiag: ModalDirective;
    @ViewChild('propertyMappingForm') propertyMappingForm: NgForm;
    @ViewChild('tempList') templateSelect: any;
    @ViewChild('propertiesSelect') propertiesSelect: any;
    currentSelectedItem: Property = new Property();
    addOrUpdate = 'Add';
    properties: { name: string, property: string } = { name: '', property: '' };
    xmlData: any;
    selectedProperty = '';
    templateList: Array<SelectData> = [];
    topologyTemplate: WineryTopologyTemplate = null;
    toscaType: ToscaTypes;
    serviceTemplateTemplate: ServiceTemplateTemplateTypes;
    targetProperties: Array<SelectData>;
    targetObject: WineryTemplate;
    targetPropertiesWrapperElement: string = null;
    initialSelectProp: any = [{ id: '', text: '' }];

    constructor(private service: PropertyMappingService,
                private notify: WineryNotificationService,
                private instanceService: InstanceService) {
    }

    ngOnInit() {
        this.getMappings();
        this.getProperties();
        this.getTopologyTemplate();
    }

    getMappings() {
        this.service.getPropertyMappings().subscribe(
            data => this.handleData(data),
            error => this.notify.error(error.toString())
        );
    }

    getTopologyTemplate() {
        this.instanceService.getTopologyTemplate().subscribe(
            data => this.handleTopologyTemplateData(data),
            error => this.notify.error('Could not get topology data')
        );
    }

    getProperties() {
        this.service.getPropertiesOfServiceTemplate().subscribe(
            data => this.handleProperties(data),
            error => this.handleError(error)
        );
    }

    handleTopologyTemplateData(data: WineryTopologyTemplate) {
        this.topologyTemplate = data;
        if (!isNullOrUndefined(this.xmlData) && !isNullOrUndefined(this.apiData)) {
            this.loading = false;
        }
    }

    handleProperties(props: string) {
        const parser = new DOMParser();
        this.xmlData = parser.parseFromString(props, 'application/xml');
        this.properties.name = this.xmlData.firstChild.localName;
        this.properties.property = '/*[local-name()=\'' + this.properties.name + '\']';

        if (!isNullOrUndefined(this.topologyTemplate) && !isNullOrUndefined(this.apiData)) {
            this.loading = false;
        }
    }

    radioBtnSelected(event: any, reset = true) {
        this.serviceTemplateTemplate = Utils.getServiceTemplateTemplateFromString(event.target.value);
        if (!isNullOrUndefined(this.serviceTemplateTemplate)) {
            this.targetTypeSelected = true;
            this.toscaType = Utils.getTypeOfServiceTemplateTemplate(this.serviceTemplateTemplate);

            if (reset) {
                this.currentSelectedItem.targetObjectRef = '';
                this.currentSelectedItem.targetPropertyRef = '';
                this.targetObject = null;
            }

            if (this.serviceTemplateTemplate === ServiceTemplateTemplateTypes.NodeTemplate ||
                this.serviceTemplateTemplate === ServiceTemplateTemplateTypes.RelationshipTemplate) {
                this.templateList = this.getListOfTemplates(this.serviceTemplateTemplate);
            } else {
                this.targetObject = new WineryTemplate();
            }
        } else {
            this.targetTypeSelected = false;
        }
    }

    getListOfTemplates(templateType: string): Array<SelectData> {
        if (!isNullOrUndefined(this.topologyTemplate[templateType])) {
            return this.topologyTemplate[templateType].map((template: WineryTemplate) => {
                const newItem: SelectItem = new SelectItem('');
                newItem.id = template.id;
                newItem.text = template.id;
                return newItem;
            });
        } else {
            this.notify.warning('No ' + Utils.getToscaTypeNameFromToscaType(this.toscaType) + ' available.\nTo select a ' +
                Utils.getToscaTypeNameFromToscaType(this.toscaType) +
                ' add at least one to the topology');
        }
    }

    targetObjectSelected(targetObj: SelectItem) {
        const templates: Array<WineryTemplate> = this.topologyTemplate[this.serviceTemplateTemplate];
        this.targetObject = templates.find((template: WineryTemplate) => {
            return template.id === targetObj.id;
        });
        this.currentSelectedItem.targetObjectRef = this.targetObject.id;
        this.currentSelectedItem.targetPropertyRef = '';
        this.getTargetProperties();
    }

    getTargetProperties() {
        const typeNameAndNamespace = Utils.getNamespaceAndLocalNameFromQName(this.targetObject.type);
        const targetObjPath: string = this.toscaType + '/' +
            encodeURIComponent(encodeURIComponent(typeNameAndNamespace.namespace)) +
            '/' + typeNameAndNamespace.localName;

        this.service.getTargetObjKVProperties(targetObjPath).subscribe(
            data => this.handleGetProperties(data),
            error => this.notify.error('Could not get Properties for selected Template.\n' + error.message)
        );
    }

    handleGetProperties(propertiesDefinition: PropertiesDefinitionsResourceApiData) {
        if (!isNullOrUndefined(propertiesDefinition.winerysPropertiesDefinition)) {
            this.targetProperties = propertiesDefinition.winerysPropertiesDefinition.propertyDefinitionKVList.map(item => {
                return { id: item.key, text: item.key };
            });
            this.targetPropertiesWrapperElement = propertiesDefinition.winerysPropertiesDefinition.elementName;
            this.currentSelectedItem.targetPropertyRef = this.initialSelectProp[0].text;
        } else {
            this.targetProperties = [];
            this.targetPropertiesWrapperElement = null;
            this.selectedProperty = '';
            this.currentSelectedItem.targetPropertyRef = '';
            this.notify.warning('The ' + Utils.getToscaTypeNameFromToscaType(this.toscaType) + ' does not have any properties!');
        }
    }

    targetPropertySelected(property: SelectItem) {
        if (!isNullOrUndefined(property)) {
            this.selectedProperty = property.id;
        }
        this.currentSelectedItem.targetPropertyRef = '/*[local-name()=\'' + this.targetPropertiesWrapperElement +
            '\']/*[local-name()=\'' + this.selectedProperty + '\']';
    }

    handleData(data: PropertyMappingsApiData) {
        this.apiData = data;
        if (!isNullOrUndefined(this.xmlData) && !isNullOrUndefined(this.topologyTemplate)) {
            this.loading = false;
        }
    }

    onCellSelected(selectedItem: WineryRowData) {
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
        this.addOrUpdate = 'Add';
        this.currentSelectedItem = new Property();
        this.propertyMappingForm.reset();
        this.targetObject = null;
        this.targetTypeSelected = false;
        this.serviceTemplateTemplate = null;
        this.addPropertyMappingModal.show();
    }

    onEditClick() {
        let elementType = ServiceTemplateTemplateTypes.NodeTemplate;
        let element: WineryTemplate = this.topologyTemplate.nodeTemplates.find(nodeTemplate => {
            return nodeTemplate.id === this.currentSelectedItem.targetObjectRef;
        });

        if (isNullOrUndefined(element)) {
            element = this.topologyTemplate.relationshipTemplates.find(relationshipTemplate => {
                if (relationshipTemplate.id === this.currentSelectedItem.targetObjectRef) {
                    elementType = ServiceTemplateTemplateTypes.RelationshipTemplate;
                    return true;
                }
                return false;
            });
        }

        if (!isNullOrUndefined(element)) {
            // Get the last value defined in local-name()='valueWeWantToGet'
            const splittedProperty = this.currentSelectedItem.targetPropertyRef.split('\'');
            this.selectedProperty = splittedProperty[splittedProperty.length - 2];
            this.targetObject = new WineryTemplate();
            this.addOrUpdate = 'Update';
            this.radioBtnSelected({ target: { value: elementType } }, false);
            this.addPropertyMappingModal.show();
        } else {
            this.notify.warning('Element not found in TopologyTemplate!');
        }
    }

    addPropertyMapping() {
        if (this.currentSelectedItem.targetObjectRef.length === 0 || this.currentSelectedItem.serviceTemplatePropertyRef.length === 0) {
            this.notify.warning('You need to specify an Target Element and Target Property!');
            return;
        }
        this.service.addPropertyMapping(this.currentSelectedItem)
            .subscribe(
                data => this.handleSuccess('Added new property mapping'),
                error => this.handleError(error)
            );
        this.addPropertyMappingModal.hide();
    }

    handleSuccess(message: string) {
        this.getMappings();
        this.notify.success(message);
    }

    handleError(error: HttpErrorResponse) {
        this.notify.error(error.message);
    }
}
