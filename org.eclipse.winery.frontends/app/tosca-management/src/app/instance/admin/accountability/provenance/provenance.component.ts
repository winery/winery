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
 *******************************************************************************/

import { Component, TemplateRef, ViewChild } from '@angular/core';
import { AccountabilityService } from '../accountability.service';
import { FileProvenanceElement, ModelProvenanceElement } from '../../../../model/provenance';
import { AccountabilityParentComponent } from '../accountabilityParent.component';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { WineryFileComparisonComponent } from '../../../../wineryFileComparisonModule/wineryFileComparison.component';

@Component({
    templateUrl: 'provenance.component.html',
    styleUrls: ['provenance.component.css']
})
export class ProvenanceComponent extends AccountabilityParentComponent {
    modalRef: BsModalRef;
    modelProvenance: ModelProvenanceElement[];
    selectedFileProvenance: FileProvenanceElement[];
    selectedFileProvenanceElement: FileProvenanceElement;
    @ViewChild(WineryFileComparisonComponent) comparer: WineryFileComparisonComponent;

    constructor(protected service: AccountabilityService, protected notify: WineryNotificationService, private modalService: BsModalService) {
        super(service, notify);
    }

    private getModelProvenance() {
        this.loading = true;
        this.service.getModelProvenance(this.selectedProvenanceId.id)
            .subscribe(
                (provenance: ModelProvenanceElement[]) => this.handleModelProvenanceData(provenance),
                error => this.handleError(error));
    }

    handleModelProvenanceData(modelProvenance: ModelProvenanceElement[]) {
        this.loading = false;
        this.modelProvenance = modelProvenance.reverse();
    }

    getVerificationClass(isVerified: boolean): string {
        if (isVerified) {
            return 'green';
        }

        return 'red';
    }

    downloadFileFromImmutableStorage(fileAddress: string, fileName: string): void {
        const provenanceId = this.selectedProvenanceId.id;
        const url = AccountabilityService.getDownloadURLForFile(fileAddress, fileName, provenanceId);
        window.open(url, '_blank');
    }

    openModal(modalTemplate: TemplateRef<any>, file: FileProvenanceElement) {
        this.selectedFileProvenanceElement = file;
        this.selectedFileProvenance = this.getFileProvenance(file.fileName);
        this.modalRef = this.modalService.show(modalTemplate);
    }

    getFileProvenance(fileId: string): FileProvenanceElement[] {

        return this
            .modelProvenance
            .map((currModel) => currModel.files
                .find((currFile) => currFile.fileName === fileId))
            .filter((item) => item !== null && item !== undefined);
    }
}
