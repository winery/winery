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
import { HttpErrorResponse } from '@angular/common/http';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';
import { CheService } from '../../../../../topologymodeler/src/app/services/che.service';
import { backendBaseURL } from '../../configuration';

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
    requiresTabFix = false;
    radon: boolean;

    toscaLightCompatibilityErrorReportModalRef: BsModalRef;
    toscaLightErrorKeys: string[];
    deleteConfirmationModalRef: BsModalRef;

    constructor(private router: Router, public sharedData: InstanceService,
                public configurationService: WineryRepositoryConfigurationService,
                private modalService: BsModalService,
                private notify: WineryNotificationService,
                private che: CheService) {
    }

    ngOnInit(): void {
        this.accountabilityEnabled = this.configurationService.configuration.features.accountability;

        if (this.subMenu.length > 7) {
            this.needTwoLines = true;
        }

        if (this.configurationService.isYaml()) {
            if (this.toscaComponent.toscaType === ToscaTypes.ServiceTemplate
                || this.toscaComponent.toscaType === ToscaTypes.Admin
                || this.toscaComponent.toscaType === ToscaTypes.PolicyType) {
                this.needTwoLines = true;
            }
        }

        if (this.toscaComponent.toscaType === ToscaTypes.Imports || this.toscaComponent.toscaType === ToscaTypes.Admin) {
            this.showManagementButtons = false;
        }

        this.showEdmmExport = this.toscaComponent.toscaType === ToscaTypes.ServiceTemplate && this.configurationService.configuration.features.edmmModeling;

        if (this.toscaComponent.toscaType === ToscaTypes.Admin
            || this.toscaComponent.toscaType === ToscaTypes.PolicyType) {
            this.requiresTabFix = true;
        }

        this.radon = this.configurationService.configuration.features.radon;
    }

    removeConfirmed() {
        this.deleteConfirmed.emit();
    }

    showErrorReport() {
        this.toscaLightErrorKeys = Object.keys(this.toscaLightCompatibilityData.errorList);
        this.toscaLightCompatibilityErrorReportModalRef = this.modalService.show(this.toscaLightCompatibilityModel);
    }

    exportToFilesystem() {
        this.sharedData.exportToFilesystem()
            .subscribe(
                () => this.handleSuccess(`Successfully exported ${this.toscaComponent.localName} CSAR to filesystem`),
                error => this.handleError(error)
            );
    }

    openDeleteConfirmationModel() {
        this.deleteConfirmationModalRef = this.modalService.show(this.confirmDeleteModal);
    }

    openChe() {
        this.che.openChe(backendBaseURL, this.toscaComponent.localName, this.toscaComponent.namespace, this.toscaComponent.toscaType.valueOf())
            .catch((err) => {
                if (err instanceof HttpErrorResponse) {
                    if (err.status === 500) {
                        this.notify.error('Winery is not properly configured for IDE usage');
                    }
                }
            });
    }

    private handleSuccess(message: string) {
        this.notify.success(message);
    }

    private handleError(error: HttpErrorResponse) {
        this.notify.error(error.message, 'Error');
    }
}
