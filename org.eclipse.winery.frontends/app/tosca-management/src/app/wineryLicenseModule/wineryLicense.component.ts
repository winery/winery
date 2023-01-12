/*******************************************************************************
 * Copyright (c) 2017-2022 Contributors to the Eclipse Foundation
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
import { ViewChild, Component, OnInit, TemplateRef } from '@angular/core';
import { WineryLicenseService } from './wineryLicense.service';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../instance/instance.service';
import { ToscaTypes } from '../model/enums';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, map } from 'rxjs/internal/operators';
import { forkJoin } from 'rxjs';
import { Observable } from 'rxjs/Rx';
import {
    WineryRepositoryConfigurationService
} from '../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { saveAs } from 'file-saver';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { LicenseEngineService } from './licenseEngine.service';
import { License } from './LicenseEngineApiData';

@Component({
    templateUrl: 'wineryLicense.component.html',
    styleUrls: ['wineryLicense.component.css'],
    providers: [WineryLicenseService, LicenseEngineService]
})

export class WineryLicenseComponent implements OnInit {

    loading = true;
    loadingLicense = false;
    isEditable = false;
    licenseAvailable = true;
    showForm = false;
    loadingbar = false;

    foundLicenses = '';
    currentLicenseText = '';
    initialLicenseText = '';
    selectedLicenseText = '';
    licenseType = '';
    options: string[] = [];
    selectedOptions: string[] = [];
    compatibleLicenses: License[] = [];

    toscaType: ToscaTypes;
    licenseEngine: boolean;
    firstFormGroup: FormGroup;
    secondFormGroup: FormGroup;
    confirmSaveModalRef: BsModalRef;
    confirmDownloadModalRef: BsModalRef;

    @ViewChild('stepper') stepper: MatStepper;
    @ViewChild('confirmSaveModal') confirmSaveModal: TemplateRef<any>;
    @ViewChild('confirmDownloadModal') confirmDownloadModal: TemplateRef<any>;

    constructor(private notify: WineryNotificationService,
                private configurationService: WineryRepositoryConfigurationService,
                private wlService: WineryLicenseService, private leService: LicenseEngineService,
                public sharedData: InstanceService, private formBuilder: FormBuilder,
                private modalService: BsModalService) {
        this.licenseEngine = configurationService.configuration.features.licenseEngine;
        this.toscaType = this.sharedData.toscaComponent.toscaType;
    }

    ngOnInit() {
        const observables = [];
        observables.push(this.wlService.getData()
            .pipe(
                map(
                    (data) => {
                        this.currentLicenseText = data;
                        this.initialLicenseText = data;
                        this.loading = false;
                    }),
                catchError(() => {
                    this.handleMissingLicense();
                    return Observable.of(null);
                })
            ));

        if (this.licenseEngine) {
            this.firstFormGroup = this.formBuilder.group({
                link: ['', Validators.required],
                branch: ['']
            });
            this.secondFormGroup = this.formBuilder.group({
                secondCtrl: ['', Validators.required]
            });

            // when license engine is used and is running, get licenses from there
            observables.push(this.leService.getAllLicenses().pipe(
                map(
                    (licenses) => {
                        this.options = licenses;
                    }),
                catchError(() => {
                    return Observable.of(null);
                })
            ));
        } else {
            // else, get licenses from the local files
            observables.push(
                this.wlService.loadLocalLicenses()
                    .pipe(
                        catchError((e) => {
                            this.handleError(e);
                            return Observable.of(null);
                        })
                    ));
            this.options = this.wlService.getLicenseNames();
        }

        forkJoin(observables).subscribe(() => {
            this.loading = false;
            this.selectedOptions = this.options;
        });
    }

    onSubmitTemplateBased() {
        const toscaElements = this.sharedData.path.split('/');
        const toscaElementID = toscaElements[toscaElements.length - 1];
        this.leService.createSoftware(toscaElementID, this.firstFormGroup.value['link'], this.firstFormGroup.value['branch']);

        this.loadingbar = true;
        this.firstFormGroup.disable();

        this.leService.deleteId().subscribe((success) => {
            if (success) {
                this.postSoftware();
            } else {
                this.failed();
            }
        }, () => {
            this.failed();
        });
    }

    postSoftware() {
        this.leService.postSoftware().subscribe(
            (success) => {
                if (success) {
                    this.leService.poll().subscribe(undefined, undefined, () => {
                        this.checkLicenseData();
                    });
                } else {
                    this.failed();
                }
            }, () => {
                this.failed();
            });
    }

    checkLicenseData() {
        if (this.leService.isFinished()) {
            this.foundLicenses = 'Extracted Licenses From Source Code: ' + this.leService.getFoundLicenses();
            this.loadingbar = false;
            this.firstFormGroup.enable();
            this.stepper.selected.completed = true;
            this.stepper.next();
        } else {
            this.failed();
        }
    }

    onSubmitCheckCompatibility() {
        this.loadingbar = true;
        this.compatibleLicenses = [];
        this.leService.getCompatibleLicenses().subscribe((licenses) => {
                if (licenses.length > 0) {
                    this.getLicenseInformation(licenses);
                } else {
                    this.compatibleLicenses = null;
                }
            },
            (error) => {
                this.handleError(error);
            });
    }

    getLicenseInformation(cLicenses: string[]) {
        const observables = [];
        for (const cLicense of cLicenses) {
            observables.push(this.leService.getLicenseInformation(cLicense).pipe(
                map(
                    (license) => {
                        this.compatibleLicenses.push(license);
                    })
            ));
        }
        forkJoin(observables).subscribe(() => {
            this.stepper.selected.completed = true;
            this.stepper.next();
        });
    }

    saveConfirm() {
        if (this.licenseEngine && this.currentLicenseText === this.selectedLicenseText) {
            this.confirmSaveModalRef = this.modalService.show(this.confirmSaveModal);
        } else {
            this.saveLicenseFile();
        }
    }

    saveLicenseFile() {
        this.wlService.save(this.currentLicenseText).subscribe(
            () => {
                this.handleSave();
            },
            (error) => {
                this.handleError(error);
            }
        );
        this.initialLicenseText = this.currentLicenseText;
        if (this.licenseEngine) {
            this.confirmDownloadModalRef = this.modalService.show(this.confirmDownloadModal);
        }
    }

    downloadFile() {
        const blob = new Blob([this.currentLicenseText], { type: 'text/plain;charset=utf-8' });
        saveAs(blob, 'LICENSE');
    }

    dropdownAction(item: string) {
        this.licenseType = item;
        this.showForm = false;
        if (!this.licenseEngine) {
            this.currentLicenseText = this.wlService.getLicenseTextLocal(item);
            this.selectedLicenseText = this.currentLicenseText;
        } else {
            this.loadingLicense = true;
            this.leService.getLicenseTextEngine(item)
                .subscribe(
                    (response) => {
                        this.loadingLicense = false;
                        this.currentLicenseText = response;
                        this.selectedLicenseText = this.currentLicenseText;
                    });
        }
    }

    reset() {
        if (this.checkLicensesAvailability()) {
            this.firstFormGroup.enable();
            this.showForm = true;
            this.leService.resetLicenseData();
            this.loadingbar = false;
        }
    }

    back() {
        this.loadingbar = false;
    }

    cancelEdit() {
        this.currentLicenseText = this.initialLicenseText;
        this.licenseType = '';
        this.isEditable = false;
        if (this.licenseEngine) {
            this.leService.resetLicenseData();
            this.loadingbar = false;
            this.showForm = false;
            this.firstFormGroup.enable();
        }
    }

    selectLicense(license: string) {
        const index = this.options.findIndex(item => license.toLowerCase() === item.toLowerCase());
        if (index >= 0) {
            license = this.options[index];
            this.dropdownAction(license);
        } else {
            this.notify.error('Unknown license selected!');
        }
    }

    getStatus() {
        return this.leService.getStatus();
    }

    failed() {
        this.leService.failed();
    }

    isFailed() {
        return this.leService.isFailed();
    }

    checkLicensesAvailability(): boolean {
        if (this.licenseEngine && this.options.length === 0) {
            this.notify.error('Unable to access the License Engine!');
            return false;
        }
        return true;
    }

    search(eventTarget: EventTarget) {
        const value = (eventTarget as HTMLInputElement).value;
        this.selectedOptions = this.select(value);
    }

    select(query: string): string[] {
        const result: string[] = [];
        for (const license of this.options) {
            if (license.toLowerCase().startsWith(query.toLowerCase())) {
                result.push(license);
            }
        }
        return result;
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }

    private handleMissingLicense() {
        this.loading = false;
        this.licenseAvailable = false;
    }

    private handleSave() {
        this.notify.success('Successfully saved LICENSE');
    }
}
