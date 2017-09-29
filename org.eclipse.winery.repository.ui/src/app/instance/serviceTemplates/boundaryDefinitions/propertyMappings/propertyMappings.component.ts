/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { Property, PropertyMappingsApiData, PropertyMappingService } from './propertyMappings.service';
import { WineryTableColumn } from '../../../../wineryTableModule/wineryTable.component';
import { isNullOrUndefined } from 'util';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { ModalDirective } from 'ngx-bootstrap';
import { NgForm } from '@angular/forms';
import { InstanceService } from '../../../instance.service';
import { WineryTemplate, WineryTopologyTemplate } from '../../../../wineryInterfaces/wineryComponent';
import { ToscaTypes } from '../../../../wineryInterfaces/enums';
import { Utils } from '../../../../wineryUtils/utils';
import { SelectItem } from 'ng2-select';
import { PropertiesDefinitionsResourceApiData } from '../../../sharedComponents/propertiesDefinition/propertiesDefinitionsResourceApiData';

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
    @ViewChild('tempList') templateSelect: any;
    @ViewChild('propertiesSelect') propertiesSelect: any;
    currentSelectedItem: Property = new Property();
    addOrUpdateBtnTxt = 'Add';
    properties: { name: string, property: string } = {name: '', property: ''};
    xmlData: any;
    selectedProperty: string;
    templateList: Array<any> = [];
    topologyTemplate: WineryTopologyTemplate = null;
    toscaType: ToscaTypes;
    targetProperties: Array<any> = [];
    targetObject: string;
    targetPropertiesWrapperElement: string = null;
    initialSelectProp: any = [{id: '', text: ''}];
    initialSelectedTempalte: [{ id: '', text: '' }];

    constructor(private service: PropertyMappingService,
                private notify: WineryNotificationService,
                private instanceService: InstanceService) {
    }

    ngOnInit() {
        this.getMappings();
        this.getProperties();
        this.getTopologyTempalte();
    }

    getTopologyTempalte() {
        this.instanceService.getTopologyTemplate().subscribe(
            data => this.topologyTemplate = data,
            error => this.notify.error('could not get topology data')
        );
    }

    getProperties() {
        this.service.getPropertiesOfServiceTemplate().subscribe(
            data => this.handleProperties(data),
            error => this.handleError(error)
        )
    }

    handleProperties(props: string) {
        const parser = new DOMParser();
        this.xmlData = parser.parseFromString(props, 'application/xml');
        this.properties.name = this.xmlData.firstChild.localName;
        this.properties.property = '/*[local-name()=\'' + this.properties.name + '\']'
    }

    radioBtnSelected(event: any) {
        const selectedType = event.target.value;
        if (!isNullOrUndefined(selectedType)) {
            this.toscaType = Utils.getToscaTypeFromString(selectedType.toLowerCase().slice(0, -1));
            this.templateList = this.getListOfTemplates(selectedType);
            this.templateSelect.selected.emit(this.templateList[0]);
            // this.initialSelectedTempalte = [this.templateList[0]];
            // this.targetObject = this.initialSelectedTempalte[0].text;
        }
    }

    getListOfTemplates(templateType: string): Array<string> {
        if (!isNullOrUndefined(this.topologyTemplate[templateType])) {
            return this.topologyTemplate[templateType].map((template: WineryTemplate) => {
                const newItem: SelectItem = new SelectItem('');
                newItem.id = template.type;
                newItem.text = Utils.getNameFromQname(template.type);
                return newItem;
            });
        } else {
            this.notify.error('No ' + Utils.getToscaTypeNameFromToscaType(this.toscaType) + ' available.\nTo select a ' +
                Utils.getToscaTypeNameFromToscaType(this.toscaType) +
                ' add at least one to the topology');
        }
    }

    targetObjectSelected(targetObj: any) {
        console.log(targetObj);
        this.targetObject = targetObj.text;
        this.currentSelectedItem.targetObjectRef = this.targetObject;
        this.getTargetProperties(targetObj);
    }

    getTargetProperties(targetObj: any) {
        const targetObjPath: string = Utils.getTypeOfTemplateOrImplementation(this.toscaType) + '/' +
            encodeURIComponent(encodeURIComponent(Utils.getNamespaceAndLocalNameFromQName(targetObj.id).namespace)) +
            '/' + this.targetObject;

        this.service.getTargetObjKVProperties(targetObjPath).subscribe(
            data => {
                this.handleGetProperties(data);
            },
            error => {
                this.notify.error('Could not get Properties for selected Template.\n' + error.toString());
            }
        )
    }

    handleGetProperties(propertiesDefinition: PropertiesDefinitionsResourceApiData) {
        if (!isNullOrUndefined(propertiesDefinition.winerysPropertiesDefinition)) {
            this.targetProperties = propertiesDefinition.winerysPropertiesDefinition.propertyDefinitionKVList.map(item => {
                return {id: item.key, text: item.key}
            });
            this.targetPropertiesWrapperElement = propertiesDefinition.winerysPropertiesDefinition.elementName;
            this.initialSelectProp = [this.targetProperties[0]];
            this.selectedProperty = this.initialSelectProp[0].text;
        } else {
            this.targetProperties = [];
            this.targetPropertiesWrapperElement = null;
            this.selectedProperty = '';
            this.currentSelectedItem.targetPropertyRef = '';
        }
    }

    targetPropertySelected(property: any) {
        console.log(property);
        if (!isNullOrUndefined(property.text) && !Array.isArray(property)) {
            this.selectedProperty = property.text;
        }
        this.currentSelectedItem.targetPropertyRef = '/*[local-name()=\'' + this.targetPropertiesWrapperElement +
            '\']/*[local-name()=\'' + this.selectedProperty + '\']';
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
        this.currentSelectedItem = new Property();
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
