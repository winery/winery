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
 ********************************************************************************/
import { Component, Input, ViewChild } from '@angular/core';
import { InstanceService } from '../instance/instance.service';
import { ModalDirective } from 'ngx-bootstrap';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { WineryVersionTypesEnum } from '../wineryInterfaces/enums';
import { WineryVersion } from '../wineryInterfaces/wineryVersion';
import { WineryValidatorObject } from '../wineryValidators/wineryDuplicateValidator.directive';
import { AbstractControl, ValidatorFn } from '@angular/forms';
import { QNameWithTypeApiData } from '../wineryInterfaces/qNameWithTypeApiData';
import { WineryAddVersionService } from './wineryVersion.service';
import { Router } from '@angular/router';
import { ReferencedDefinitionsComponent } from './referencedDefinitions/referencedDefinitions.component';
import { WineryVersionActions, WineryVersionModalConfig } from './wineryVersionModalConfig';
import { isNullOrUndefined } from 'util';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'winery-version',
    templateUrl: 'wineryVersion.component.html',
    providers: [
        WineryAddVersionService
    ]
})

export class WineryVersionComponent {

    @Input() versions: WineryVersion[];

    modalConfig: WineryVersionModalConfig;
    currentSelected: WineryVersionTypesEnum;
    newVersion: WineryVersion;
    newComponentVersion = new WineryVersion('', 0, 0);
    newWineryVersion = new WineryVersion('', 0, 0);
    newWIPVersion = new WineryVersion('', 0, 0);
    releasedName: string;

    readonly versionTypes = WineryVersionTypesEnum;
    readonly validatorObject = new WineryValidatorObject(null);
    readonly versionActions = WineryVersionActions;

    @ViewChild('modal') modal: ModalDirective;
    @ViewChild('referencedDefsComponent') referencedDefsComponent: ReferencedDefinitionsComponent;

    // in this case, the type defines the qName's ToscaType
    referencedDefinitions: QNameWithTypeApiData[];
    private webSocket: WebSocket;

    constructor(public sharedData: InstanceService,
                private service: WineryAddVersionService,
                private notify: WineryNotificationService,
                private router: Router) {
    }

    onAddNewVersion() {
        this.onShowModal();
        this.generateNewVersions();

        this.currentSelected = null;
        this.newVersion = new WineryVersion('', 0, 0);

        this.modalConfig.action = WineryVersionActions.AddNewVersion;
        this.modalConfig.title = 'Add a new version';
        this.modalConfig.okButtonLabel = 'Add';
        this.modalConfig.valid = false;
        this.modal.show();
    }

    onReleaseVersion() {
        this.onShowModal();

        const releasedVersion = new WineryVersion(
            this.sharedData.currentVersion.componentVersion,
            this.sharedData.currentVersion.wineryVersion,
            0);
        this.releasedName = this.sharedData.toscaComponent.localNameWithoutVersion
            + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR
            + releasedVersion.toString();

        this.modalConfig.action = WineryVersionActions.ReleaseVersion;
        this.modalConfig.title = 'Release the current version';
        this.modalConfig.okButtonLabel = 'Release';
        this.modal.show();
    }

    onCommitVersion() {
        this.onShowModal();
        this.modalConfig.action = WineryVersionActions.CommitVersion;
        this.modalConfig.title = 'Commit the current version';
        this.modalConfig.okButtonLabel = 'Commit';
        this.modal.show();
    }

    onVersionSelected(version: WineryVersion) {
        const component = this.sharedData.toscaComponent;
        let componentName = component.localNameWithoutVersion;

        if (version.toString().length > 0) {
            componentName += WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + version.toString();
        }

        this.router.navigate([
            component.toscaType + '/' +
            encodeURIComponent(component.namespace) + '/' +
            componentName
        ]);
    }

    private handleSubComponents(data: QNameWithTypeApiData[]) {
        this.modalConfig.loading = false;
        this.referencedDefinitions = data;
    }

    private handleError(error: HttpErrorResponse) {
        this.notify.error(error.message, 'Error');
    }

    private onShowModal() {
        this.modalConfig = new WineryVersionModalConfig();
        this.modalConfig.valid = true;
        // Future Work: also update referencing definitions
        /*this.modalConfig.loading = true;
        this.service.getReferencedDefinitions()
            .subscribe(
                data => this.handleSubComponents(data),
                error => this.handleError(error)
            );*/
    }

    componentVersionSelected() {
        this.newVersion = this.newComponentVersion;
        this.modalConfig.valid = true;
        this.currentSelected = WineryVersionTypesEnum.ComponentVersion;
        this.validatorObject.validate = (obj: WineryValidatorObject) => this.validateComponentVersion();
    }

    wineryVersionSelected() {
        this.newVersion = this.newWineryVersion;
        this.modalConfig.valid = true;
        this.currentSelected = WineryVersionTypesEnum.WineryVersion;
    }

    wipVersionSelected() {
        this.newVersion = this.newWIPVersion;
        this.modalConfig.valid = true;
        this.currentSelected = WineryVersionTypesEnum.WipVersion;
    }

    onConfirm() {
        switch (this.modalConfig.action) {
            case WineryVersionActions.AddNewVersion:
                this.addComponent();
                break;
            case WineryVersionActions.CommitVersion:
                this.commitVersion();
                break;
            case WineryVersionActions.ReleaseVersion:
                this.releaseVersion();
                break;
        }
    }

    private generateNewVersions() {
        let oldVersion = this.sharedData.currentVersion;
        if (!oldVersion.releasable) {
            this.sharedData.versions.forEach(version => {
                if (version.componentVersion === oldVersion.componentVersion
                    && version.wineryVersion >= oldVersion.wineryVersion
                    && (version.workInProgressVersion === 0
                        || (oldVersion.workInProgressVersion > 0 && version.workInProgressVersion > oldVersion.workInProgressVersion))) {
                    oldVersion = version;
                }
            });
        }

        // generate new WIP version
        const wineryVersion = oldVersion.wineryVersion > 0 ?
            oldVersion.workInProgressVersion > 0 ? oldVersion.wineryVersion : oldVersion.wineryVersion + 1
            : 1;
        const wipVersion = oldVersion.workInProgressVersion + 1;
        this.newWIPVersion = new WineryVersion(oldVersion.componentVersion, wineryVersion, wipVersion);

        // generate new Winery version
        this.newWineryVersion = new WineryVersion(oldVersion.componentVersion, oldVersion.wineryVersion + 1, 1);

        // generate new Component version
        this.newComponentVersion = new WineryVersion('', 1, 1);
    }

    private addComponent() {
        this.modalConfig.loading = true;

        this.service.addNewVersion(this.newVersion, this.referencedDefsComponent.updateReferencedDefinitions)
            .subscribe(
                () => this.onSuccess(),
                (error: HttpErrorResponse) => this.handleError(error),
            );
    }

    private commitVersion() {
        this.modalConfig.loading = true;
        this.newVersion = this.sharedData.currentVersion;
        this.router.routeReuseStrategy.shouldReuseRoute = function () {
            return false;
        };
        this.service.freezeOrRelease('freeze')
            .subscribe(
                () => this.onSuccess('froze'),
                (error: HttpErrorResponse) => this.handleError(error)
            );
    }

    private releaseVersion() {
        this.newVersion = this.sharedData.currentVersion;
        this.newVersion.workInProgressVersion = 0;

        this.service.freezeOrRelease('release')
            .subscribe(
                () => this.onSuccess(),
                (error: HttpErrorResponse) => this.handleError(error)
            );
    }

    private validateComponentVersion(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: any } => {
            const duplicate = this.sharedData.versions.find(value => {
                return value.componentVersion === this.newVersion.componentVersion && value.wineryVersion > 0;
            });
            if (!isNullOrUndefined(duplicate)) {
                this.modalConfig.valid = false;
                return { duplicateFound: true };
            } else if (this.newVersion.componentVersion.indexOf(' ') >= 0) {
                this.modalConfig.valid = false;
                return { componentVersionMustNotContainWhitespaces: true };
            } else if (this.newVersion.componentVersion.includes('_')) {
                this.modalConfig.valid = false;
                return { noUnderscoresAllowed: true };
            } else {
                this.modalConfig.valid = true;
                return null;
            }
        };
    }

    private onSuccess(action = 'added') {
        const newLocalName = this.sharedData.toscaComponent.localNameWithoutVersion
            + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + this.newVersion.toString();

        const url = this.sharedData.toscaComponent.toscaType + '/'
            + encodeURIComponent(this.sharedData.toscaComponent.namespace) + '/'
            + newLocalName;

        this.notify.success('Successfully ' + action + ' ' + newLocalName);
        this.router.navigate([url]);
    }
}
