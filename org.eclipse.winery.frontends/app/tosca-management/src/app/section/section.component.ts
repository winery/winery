/*******************************************************************************
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
import { ChangeDetectorRef, Component, OnDestroy, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { SectionResolverData } from '../model/resolverData';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { SectionService } from './section.service';
import { SectionData } from './sectionData';
import { backendBaseURL } from '../configuration';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { ToscaTypes } from '../model/enums';
import { WineryUploaderComponent } from '../wineryUploader/wineryUploader.component';
import { WineryAddComponent } from '../wineryAddComponentModule/addComponent.component';
import { isNullOrUndefined } from 'util';
import { Utils } from '../wineryUtils/utils';
import { HttpErrorResponse } from '@angular/common/http';
import { ImportMetaInformation } from '../model/importMetaInformation';
import { KeyValueItem } from '../model/keyValueItem';
import { ConfigurationService } from '../instance/admin/accountability/configuration/configuration.service';
import { AccountabilityService } from '../instance/admin/accountability/accountability.service';
import { FileProvenanceElement } from '../model/provenance';
import { WineryVersion } from '../model/wineryVersion';

const showAll = 'Show all Items';
const showGrouped = 'Group by Namespace';

@Component({
    selector: 'winery-section-component',
    templateUrl: 'section.component.html',
    styleUrls: [
        'section.component.css'
    ],
    providers: [
        SectionService,
    ]
})
export class SectionComponent implements OnInit, OnDestroy {
    loading = true;
    toscaType: ToscaTypes;
    toscaTypes = ToscaTypes;
    routeSub: Subscription;
    filterString = '';
    itemsPerPage = 10;
    currentPage = 1;
    showNamespace = 'all';
    changeViewButtonTitle = showGrouped;
    componentData: SectionData[];
    allElements: SectionData[];
    elementToRemove: SectionData;
    overwriteValue = false;
    validateInput = false;
    isAccountabilityCheckEnabled = false;

    importMetadata: ImportMetaInformation;
    fileProvenance: Map<string, FileProvenanceElement[]> = new Map<string, FileProvenanceElement[]>();

    /* File Comparison-Related */
    modalRef: BsModalRef;
    selectedFileProvenance: FileProvenanceElement[];
    selectedFile: FileProvenanceElement;

    importXsdSchemaType: string;

    fileUploadUrl = backendBaseURL + '/';

    @ViewChild('addModal') addModal: WineryAddComponent;
    @ViewChild('addCsarModal') addCsarModal: ModalDirective;
    @ViewChild('removeElementModal') removeElementModal: ModalDirective;
    @ViewChild('addYamlModal') addYamlModal: ModalDirective;
    @ViewChild('validationModal') validationModal: ModalDirective;
    @ViewChild('fileUploader') fileUploader: WineryUploaderComponent;

    constructor(private route: ActivatedRoute,
                private change: ChangeDetectorRef,
                private router: Router,
                private service: SectionService,
                private notify: WineryNotificationService,
                private accountabilityConfig: ConfigurationService,
                protected accountability: AccountabilityService,
                private modalService: BsModalService) {
    }

    /**
     * @override
     *
     * Subscribe to the url on initialisation in order to get the corresponding resource type.
     */
    ngOnInit(): void {
        this.isAccountabilityCheckEnabled = this.accountabilityConfig.isAccountablilityCheckEnabled();
        this.loading = true;
        this.routeSub = this.route
            .data
            .subscribe(
                data => this.handleResolverData(data),
                error => this.handleError(error)
            );
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }

    onChangeView() {
        if (this.showNamespace === 'group') {
            this.changeViewButtonTitle = showGrouped;
            this.showNamespace = 'all';
        } else {
            this.changeViewButtonTitle = showAll;
            this.showNamespace = 'group';
        }

        localStorage.setItem(this.toscaType + '_showNamespace', this.showNamespace);
    }

    onAdd() {
        this.addModal.namespace = (this.showNamespace !== 'all' && this.showNamespace !== 'group') ? this.showNamespace : '';
        this.addModal.onAdd();
    }

    showSpecificNamespaceOnly(): boolean {
        return !(this.showNamespace === 'group' || this.showNamespace === 'all');
    }

    getSectionsData() {
        this.loading = true;
        let url = '/' + this.toscaType;

        if (this.toscaType === ToscaTypes.Imports) {
            url += '/' + encodeURIComponent(encodeURIComponent(this.importXsdSchemaType));
        }

        this.service.getSectionData(url)
            .subscribe(
                res => this.handleData(res),
                error => this.handleError(error)
            );
    }

    onUploadSuccess(response: string) {
        const metadata = JSON.parse(response);
        this.fileProvenance = new Map<string, FileProvenanceElement[]>();

        if (this.validateInput) {
            if (metadata.valid) {
                this.notify.success('CSAR validation successful!', 'CSAR valid');
            } else {
                this.notify.error('CSAR validation failed! See modal for details.', 'CSAR invalid');
            }

            const keys = Object.keys(metadata.verificationMap);
            const files: KeyValueItem[] = [];

            for (const key of keys) {
                files.push({ key: key, value: metadata.verificationMap[key] });
            }

            metadata.verificationMap = files;
            this.importMetadata = metadata;

            this.validationModal.show();
        }
    }

    onPageChange(page: number) {
        this.currentPage = page;
    }

    importOptionsChanged() {
        // validation currently only works, if the overwrite flag is set to true...
        this.overwriteValue = this.overwriteValue || this.validateInput;
        this.fileUploader.getUploader().setOptions({
            url: this.fileUploadUrl,
            additionalParameter: {
                overwrite: this.overwriteValue,
                validate: this.validateInput
            }
        });
    }

    /**
     * Handle the resolved data.
     * @param data needs to be of type any because there is no specific type specified by angular
     */
    private handleResolverData(data: any) {
        const resolved: SectionResolverData = data.resolveData;

        this.toscaType = resolved.section;
        this.importXsdSchemaType = resolved.xsdSchemaType;

        const storedNamespace = localStorage.getItem(this.toscaType + '_showNamespace') !== null ?
            localStorage.getItem(this.toscaType + '_showNamespace') : 'all';
        this.showNamespace = resolved.namespace ? resolved.namespace : storedNamespace;

        this.service.setPath(resolved.path);
        this.getSectionsData();
    }

    private handleData(resources: SectionData[]) {
        this.allElements = resources;
        this.componentData = [];

        resources.forEach(item => {
            const container = new SectionData();
            item.version = new WineryVersion(item.version.componentVersion,
                item.version.wineryVersion, item.version.workInProgressVersion, item.version.currentVersion,
                item.version.latestVersion, item.version.releasable, item.version.editable);
            item.name = Utils.getNameWithoutVersion(item.name);
            container.createContainerCopy(item);

            if (this.componentData.length === 0) {
                this.componentData.push(container);
                return;
            }

            // works because the elements are ordered, and find() or some() would be taking much more time here
            const lastIndex = this.componentData.length - 1;
            const lastElement = this.componentData[lastIndex];

            if (lastElement.namespace === item.namespace
                && Utils.getNameWithoutVersion(lastElement.id) === container.id) {

                const index = lastElement.versionInstances.length - 1;
                const last = lastElement.versionInstances[index];

                if (last.version.componentVersion === container.version.componentVersion &&
                    last.version.wineryVersion === container.version.wineryVersion) {
                    if (isNullOrUndefined(last.versionInstances)) {
                        const copy = (new SectionData()).createCopy(last);
                        last.hasChildren = true;
                        const wip = last.id.match(/(-wip[0-9]*$)/);
                        if (!isNullOrUndefined(wip)) {
                            last.id = last.id.substr(0, wip.index);
                            last.name = last.name.substr(0, wip.index);
                        }
                        last.versionInstances = [copy, item];
                    } else {
                        last.versionInstances.push(item);
                    }
                } else {
                    lastElement.versionInstances.push(item);
                }

                lastElement.hasChildren = true;
            } else {
                this.componentData.push(container);
            }
        });

        if (!this.showSpecificNamespaceOnly() && (this.componentData.length > 50)) {
            this.showNamespace = 'group';
            this.changeViewButtonTitle = showAll;
        } else if (!this.showSpecificNamespaceOnly()) {
            if (this.showNamespace === 'group') {
                this.changeViewButtonTitle = showAll;
            } else {
                this.changeViewButtonTitle = showGrouped;
            }
        } else {
            this.changeViewButtonTitle = showGrouped;
        }
        this.loading = false;
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message);
    }

    getVerificationClass(value: string): string {
        switch (value) {
            case 'VERIFIED':
                return 'green';
            default:
                return 'red';
        }
    }

    getProvenance(file: KeyValueItem) {
        this.accountability.getFileProvenance(this.importMetadata.entryServiceTemplate.qname, file.key)
            .subscribe(
                value => this.fileProvenance[file.key] = value,
                error => this.handleError(error)
            );
    }

    getAuthorizedClass(authorized: boolean): string {
        return this.getVerificationClass(authorized ? 'VERIFIED' : '');
    }

    onUploadError(response: string) {
        if (this.validateInput) {
            this.onUploadSuccess(response);
        }
    }

    downloadFileFromImmutableStorage(fileAddress: string, fileName: string): void {
        const provenanceId = this.importMetadata.entryServiceTemplate.qname;
        const url = AccountabilityService.getDownloadURLForFile(fileAddress, fileName, provenanceId);
        window.open(url, '_blank');
    }


    openFileComparisonModal(modalTemplate: TemplateRef<any>, file: FileProvenanceElement) {
        this.selectedFile = file;
        this.selectedFileProvenance = this.fileProvenance[file.fileName];
        this.modalRef = this.modalService.show(modalTemplate);
    }

}
