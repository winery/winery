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

import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { FileProvenanceElement, ProvenanceElement } from '../model/provenance';
import { DatePipe } from '@angular/common';
import { AccountabilityService } from '../instance/admin/accountability/accountability.service';
import { forkJoin } from 'rxjs/index';
import { SelectData } from '../model/selectData';
import { HttpErrorResponse } from '@angular/common/http';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { SelectItem } from 'ng2-select';

@Component({
    selector: 'winery-file-comparison',
    templateUrl: 'wineryFileComparison.component.html',
    styleUrls: ['wineryFileComparison.component.css']
})
export class WineryFileComparisonComponent implements OnChanges {
    /**
     * The selected drop-down item representing the version of the model corresponding to the left file to compare.
     * Has the format {id: modelTransactionId, text: textualRepresentationOfModelVersion}
     */
    leftVersion: SelectItem;

    /**
     * The left file version to compare
     */
    leftFile: FileProvenanceElement;

    /**
     * The content of the left file to compare
     */
    leftFileText: string;

    /**
     * The selected drop-down item representing the version of the model corresponding to the right file to compare.
     * Has the format {id: modelTransactionId, text: textualRepresentationOfModelVersion}
     */
    rightVersion: SelectItem;

    /**
     * The content of the right file to compare
     */
    rightFileText: string;

    /**
     * The right file version to compare
     */
    rightFile: FileProvenanceElement;

    @Input() fileProvenance: FileProvenanceElement[];
    @Input() selectedFileProvenanceElement: FileProvenanceElement;
    @Input() selectedServiceTemplate: SelectData;

    /**
     * instead of constructor injection to avoid importing the
     * Admin Module (circular dependency)
     */
    @Input() accountabilityService: AccountabilityService;
    private datePipe = new DatePipe('en-US');
    private readonly nonComparableFileExtensions: string[] = ['exe', 'war', 'jpg', 'jpeg', 'png', 'bmp', 'jar', 'zip', 'gz'];

    constructor(private notify: WineryNotificationService) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.initializeSelection();
    }

    private initializeSelection() {
        this.leftVersion = this.convertProvenanceElementToSelectEntry(this.selectedFileProvenanceElement);
        this.leftFile = this.selectedFileProvenanceElement;
        this.leftFileText = null;
        this.rightVersion = null;
        this.rightFile = null;
        this.rightFileText = null;
    }

    leftFileVersionSelected(fileVersion: SelectItem) {
        this.leftVersion = fileVersion;
        this.leftFile = this.locateSelectedFileVersion(fileVersion);
        this.leftFileText = null;
    }

    rightFileVersionSelected(fileVersion: SelectItem) {
        this.rightVersion = fileVersion;
        this.rightFile = this.locateSelectedFileVersion(fileVersion);
        this.rightFileText = null;
    }

    locateSelectedFileVersion(version: SelectItem): FileProvenanceElement {
        return this.fileProvenance
            .find((file: FileProvenanceElement) => file.transactionHash.toLowerCase() === version.id.toLowerCase());
    }

    retrieveFilesForComparison() {
        if (!this.canCompareFile(this.leftFile.fileName)) {
            // this triggers showing comparison result;
            this.leftFileText = 'a';
            this.rightFileText = 'a';
        } else {
            // retrieve file contents only when compring them makes sense.
            const observables = [
                this.accountabilityService.retrieveFileContent(this.leftFile.addressInImmutableStorage, this.selectedServiceTemplate.id),
                this.accountabilityService.retrieveFileContent(this.rightFile.addressInImmutableStorage, this.selectedServiceTemplate.id)];

            forkJoin(observables)
                .subscribe((texts: string[]) => {
                        if (texts !== null && texts !== undefined && texts.length === 2) {
                            this.leftFileText = texts[0];
                            this.rightFileText = texts[1];
                        }
                    },
                    error => this.handleError(error)
                );
        }

    }

    /**
     * Gets all file versions of the specified file
     * @param {string} fileId the name of the file for which to find versions
     * @returns {SelectItem[]} the set of file versions (SelectItem {id, text}) that refer to the specified file
     */
    getProvenanceVersionTitles(fileId: string): SelectItem[] {
        if (this.fileProvenance !== null && this.fileProvenance !== undefined) {
            return this.fileProvenance
                .map((version: FileProvenanceElement) => {
                    return this.convertProvenanceElementToSelectEntry(version);
                });
        }

        return [];
    }

    convertProvenanceElementToSelectEntry(element: ProvenanceElement): SelectItem {
        const formattedTimestamp = this.datePipe.transform(element.unixTimestamp * 1000, 'yyyy-MM-dd HH:mm:ss');
        const text = `${formattedTimestamp} (${element.authorName ? element.authorName : 'unauthorized'})`;
        const id = element.transactionHash;

        const result = new SelectItem('');
        result.id = id;
        result.text = text;

        return result;
    }

    canCompareFile(fileId: string): boolean {
        const splits = fileId.split('.');

        if (splits.length > 1) {
            return this.nonComparableFileExtensions.indexOf(splits[splits.length - 1].toLowerCase()) < 0;
        }

        // if the file has no extension, e.g., LICENSE, and README, then we consider it comparable
        return true;
    }

    handleError(error: HttpErrorResponse) {
        this.notify.error(error.message);
    }
}
