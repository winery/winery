/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { VersionElement } from '../../models/versionElement';
import { UpdateInfo } from '../../models/UpdateInfo';
import { UpdateService } from './update.service';
import { ErrorHandlerService } from '../../services/error-handler.service';
import { TTopologyTemplate } from '../../models/ttopology-template';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { TopologyTemplateUtil } from '../../models/topologyTemplateUtil';
import { WineryActions } from '../../redux/actions/winery.actions';
import { PropertyDiffList } from '../../models/propertyDiffList';
import { Utils } from '../../../../../tosca-management/src/app/wineryUtils/utils';
import { WineryVersion } from '../../../../../tosca-management/src/app/model/wineryVersion';

@Component({
    selector: 'winery-versions',
    templateUrl: './versions.component.html',
    styleUrls: ['./versions.component.css'],
    providers: [UpdateService]
})
export class VersionsComponent implements OnInit {

    chosenVersion: WineryVersion;

    choosedNewProperty: string;
    choosedRemovedProperty: string;

    // first entry newProperties, second entry removedProperties
    matchedProperties: string[][];

    @ViewChild('updateVersionModal') updateVersionModal: ModalDirective;
    updateVersionModalRef: BsModalRef;

    @ViewChild('updatePropertyModal') updatePropertyModal: ModalDirective;
    updatePropertyModalRef: BsModalRef;

    @Input() aVersionElement: VersionElement;
    @Input() nodeTemplateId: string;
    @Input() nodeType: string;
    qNamePrefix: string;
    versions: WineryVersion[];
    kvComparison: any;
    versionClicked = false;
    continueOrMap: string;

    propertyDiff: PropertyDiffList;

    constructor(private modalService: BsModalService,
                private updateService: UpdateService,
                private errorHandler: ErrorHandlerService,
                private ngRedux: NgRedux<IWineryState>,
                private wineryActions: WineryActions) {
    }

    ngOnInit() {
        this.qNamePrefix = this.aVersionElement.qName.split('}')[0] + '}';
        this.versions = this.aVersionElement.versions;
    }

    readProperties(removedProperties: number, newProperties: number, resolvedProperties: number) {
        if (Math.max(removedProperties, newProperties) >= resolvedProperties) {
            return removedProperties >= newProperties ? this.propertyDiff.removedProperties : this.propertyDiff.newProperties;
        }
        return this.propertyDiff.resolvedProperties;
    }

    open() {
        this.updateVersionModalRef = this.modalService.show(this.updateVersionModal);
        this.versionClicked = false;
    }

    openProperty() {
        this.updatePropertyModalRef = this.modalService.show(this.updatePropertyModal);

        this.continueOrMap = 'Continue';

    }

    matchProperties() {
        if (this.choosedNewProperty != null && this.choosedRemovedProperty != null) {
            this.matchedProperties.push([this.choosedNewProperty, this.choosedRemovedProperty]);
            this.propertyDiff.newProperties.splice(this.propertyDiff.newProperties.indexOf(this.choosedNewProperty), 1);
            this.propertyDiff.removedProperties.splice(this.propertyDiff.removedProperties.indexOf(this.choosedRemovedProperty), 1);

            this.choosedRemovedProperty = null;
            this.choosedNewProperty = null;

            this.continueOrMap = 'Continue';
        }

    }

    triggerUpdate(nodeTemplateId: string) {

        const qName = this.qNamePrefix + Utils.getNameWithoutVersion(Utils.getNameFromQName(this.nodeType))
            + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + this.chosenVersion.toString();
        this.updateService.update(new UpdateInfo(nodeTemplateId, qName,
            this.matchedProperties,
            this.propertyDiff.newProperties,
            this.propertyDiff.resolvedProperties))
            .subscribe(
                data => this.updateTopology(data),
                error => this.errorHandler.handleError(error)
            );
    }

    selectedVersion(version: WineryVersion) {
        this.kvComparison = null;
        this.chosenVersion = version;
        this.showKVComparison();
        this.versionClicked = true;
    }

    selectedNewProperty(newProperty: string) {
        this.continueOrMap = 'Map';
        this.choosedNewProperty = newProperty;
    }

    selectedRemovedProperty(removedProperty) {
        this.continueOrMap = 'Map';
        this.choosedRemovedProperty = removedProperty;
    }

    updateTopology(topology: TTopologyTemplate) {
        TopologyTemplateUtil.updateTopologyTemplate(this.ngRedux, this.wineryActions, topology);
    }

    showKVComparison() {
        const qName = this.qNamePrefix + Utils.getNameWithoutVersion(Utils.getNameFromQName(this.nodeType))
            + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + this.chosenVersion.toString();

        this.updateService.getKVComparison(new UpdateInfo(this.nodeTemplateId, qName))
            .subscribe(
                data => this.propertyDiff = new PropertyDiffList(data.resolvedProperties, data.removedProperties, data.newProperties)
            );

        this.matchedProperties = [];
        this.choosedNewProperty = null;
        this.choosedRemovedProperty = null;
    }

    triggerUpdateOrMatchProperties(nodeTemplateId: string) {
        if (this.continueOrMap.trim() === 'Continue') {
            this.triggerUpdate(nodeTemplateId);
        } else {
            this.matchProperties();

            this.openProperty();
        }
    }

}
