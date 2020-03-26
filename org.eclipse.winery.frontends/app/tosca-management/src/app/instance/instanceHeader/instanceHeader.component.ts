/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { RemoveWhiteSpacesPipe } from '../../wineryPipes/removeWhiteSpaces.pipe';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { ToscaComponent } from '../../model/toscaComponent';
import { ToscaTypes } from '../../model/enums';
import { WineryVersion } from '../../model/wineryVersion';
import { InstanceService, ToscaLightCompatibilityData } from '../instance.service';
import { WineryRepositoryConfigurationService } from '../../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { SubMenuItem } from '../../model/subMenuItem';

@Component({
    selector: 'winery-instance-header',
    templateUrl: './instanceHeader.component.html',
    styleUrls: [
        './instanceHeader.component.css'
    ],
    providers: [
        RemoveWhiteSpacesPipe
    ],
})

export class InstanceHeaderComponent implements OnInit {

    @Input() toscaComponent: ToscaComponent;
    @Input() versions: WineryVersion[];
    @Input() typeUrl: string;
    @Input() typeId: string;
    @Input() typeOf: string;
    @Input() subMenu: SubMenuItem[];
    @Input() imageUrl: string;
    @Input() toscaLightCompatibilityData: ToscaLightCompatibilityData;
    @Output() deleteConfirmed: EventEmitter<any> = new EventEmitter();

    @ViewChild('confirmDeleteModal') confirmDeleteModal: TemplateRef<any>;
    @ViewChild('toscaLightCompatibilityModal') toscaLightCompatibilityModel: TemplateRef<any>;

    needTwoLines = false;
    selectedTab: string;
    showManagementButtons = true;
    accountabilityEnabled: boolean;
    showEdmmExport: boolean;

    toscaLightCompatibilityErrorReportModalRef: BsModalRef;
    toscaLightErrorKeys: string[];
    deleteConfirmationModalRef: BsModalRef;

    constructor(private router: Router, public sharedData: InstanceService,
                private configurationService: WineryRepositoryConfigurationService,
                private modalService: BsModalService) {
    }

    ngOnInit(): void {
        this.accountabilityEnabled = this.configurationService.configuration.features.accountability;

        if (this.subMenu.length > 7) {
            this.needTwoLines = true;
        }

        if (this.toscaComponent.toscaType === ToscaTypes.Imports || this.toscaComponent.toscaType === ToscaTypes.Admin) {
            this.showManagementButtons = false;
        }

        this.showEdmmExport = this.toscaComponent.toscaType === ToscaTypes.ServiceTemplate && this.configurationService.configuration.features.edmmModeling;
    }

    removeConfirmed() {
        this.deleteConfirmed.emit();
    }

    showErrorReport() {
        this.toscaLightErrorKeys = Object.keys(this.toscaLightCompatibilityData.errorList);
        this.toscaLightCompatibilityErrorReportModalRef = this.modalService.show(this.toscaLightCompatibilityModel);
    }

    openDeleteConfirmationModel() {
        this.deleteConfirmationModalRef = this.modalService.show(this.confirmDeleteModal);
    }
}
