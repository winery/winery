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

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { VersionElement } from '../../models/versionElement';
import { PropertyMatching, UpdateInfo } from '../../models/UpdateInfo';
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
import { WineryRepositoryConfigurationService } from '../../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';

@Component({
    selector: 'winery-versions',
    templateUrl: './versions.component.html',
    styleUrls: ['./versions.component.css'],
    providers: [UpdateService]
})
export class VersionsComponent implements OnInit {

    readonly CONTINUE = 'Continue';
    readonly MAP = 'Map';

    chosenVersion: WineryVersion;
    chosenNewProperty: string;
    chosenRemovedProperty: string;

    // first entry newProperties, second entry removedProperties
    matchedProperties: PropertyMatching[] = [];

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
    continueOrMap: string;

    propertyDiff: PropertyDiffList;
    saveAfterUpdate: boolean;

    constructor(private modalService: BsModalService,
                private updateService: UpdateService,
                private errorHandler: ErrorHandlerService,
                private ngRedux: NgRedux<IWineryState>,
                private configurationService: WineryRepositoryConfigurationService,
                private wineryActions: WineryActions) {
    }

    ngOnInit() {
        this.qNamePrefix = this.aVersionElement.qName.split('}')[0] + '}';
        this.versions = this.aVersionElement.versions;
    }

    readProperties() {
        if (this.propertyDiff &&
            Math.max(this.propertyDiff.removedProperties.length, this.propertyDiff.newProperties.length) >= this.propertyDiff.resolvedProperties.length) {
            return this.propertyDiff.removedProperties.length >= this.propertyDiff.newProperties.length
                ? this.propertyDiff.removedProperties
                : this.propertyDiff.newProperties;
        }

        return this.propertyDiff.resolvedProperties;
    }

    open() {
        this.updateVersionModalRef = this.modalService.show(this.updateVersionModal);
        this.chosenVersion = null;
        this.saveAfterUpdate = false;
    }

    openProperty() {
        this.updatePropertyModalRef = this.modalService.show(this.updatePropertyModal);

        this.continueOrMap = this.CONTINUE;

    }

    matchProperties() {
        if (this.chosenNewProperty != null && this.chosenRemovedProperty != null) {
            this.matchedProperties
                .push(
                    new PropertyMatching(this.chosenRemovedProperty, this.chosenNewProperty)
                );

            this.propertyDiff.newProperties
                .splice(this.propertyDiff.newProperties.indexOf(this.chosenNewProperty), 1);
            this.propertyDiff.removedProperties
                .splice(this.propertyDiff.removedProperties.indexOf(this.chosenRemovedProperty), 1);
            this.resetChosen();
        }

    }

    triggerUpdate(nodeTemplateId: string) {
        const qName = this.qNamePrefix + Utils.getNameWithoutVersion(Utils.getNameFromQName(this.nodeType))
            + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + this.chosenVersion.toString();
        const updateInfo = new UpdateInfo(
            nodeTemplateId,
            qName,
            this.matchedProperties,
            this.propertyDiff.newProperties,
            this.propertyDiff.resolvedProperties,
            this.saveAfterUpdate
        );

        this.updateService.update(updateInfo)
            .subscribe(
                data => this.updateTopology(data),
                error => this.errorHandler.handleError(error)
            );
    }

    selectedVersion(version: WineryVersion) {
        this.kvComparison = null;
        this.chosenVersion = version;
        this.showKVComparison();
    }

    selectedNewProperty(newProperty: string) {
        this.continueOrMap = this.MAP;
        this.chosenNewProperty = newProperty;
    }

    selectedRemovedProperty(removedProperty) {
        this.continueOrMap = this.MAP;
        this.chosenRemovedProperty = removedProperty;
    }

    updateTopology(topology: TTopologyTemplate) {
        TopologyTemplateUtil.updateTopologyTemplate(this.ngRedux, this.wineryActions, topology, this.configurationService.isYaml());
    }

    showKVComparison() {
        const qName = this.qNamePrefix + Utils.getNameWithoutVersion(Utils.getNameFromQName(this.nodeType))
            + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + this.chosenVersion.toString();

        this.updateService.getKVComparison(new UpdateInfo(this.nodeTemplateId, qName))
            .subscribe(
                data => this.propertyDiff = new PropertyDiffList(data.resolvedProperties, data.removedProperties, data.newProperties)
            );

        this.matchedProperties = [];
        this.chosenNewProperty = null;
        this.chosenRemovedProperty = null;
    }

    triggerUpdateOrMatchProperties(nodeTemplateId: string) {
        if (this.continueOrMap.trim() === 'Continue') {
            this.triggerUpdate(nodeTemplateId);
        } else {
            this.matchProperties();
            this.openProperty();
        }
    }

    disableMapButton() {
        if (this.continueOrMap === this.CONTINUE) {
            return false;
        }

        return !(this.chosenNewProperty && this.chosenRemovedProperty);
    }

    resetChosen() {
        this.chosenRemovedProperty = null;
        this.chosenNewProperty = null;
        this.continueOrMap = this.CONTINUE;
    }
}
