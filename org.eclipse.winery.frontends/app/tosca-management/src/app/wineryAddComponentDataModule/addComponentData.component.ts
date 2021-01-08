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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { InheritanceService } from '../instance/sharedComponents/inheritance/inheritance.service';
import { TooltipConfig } from 'ngx-bootstrap';
import { AddComponentValidation } from '../wineryAddComponentModule/addComponentValidation';
import { WineryVersion } from '../model/wineryVersion';
import { SelectData } from '../model/selectData';
import { ToscaTypes } from '../model/enums';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { ExistService } from '../wineryUtils/existService';
import { backendBaseURL } from '../configuration';
import { ToscaComponent } from '../model/toscaComponent';
import { Utils } from '../wineryUtils/utils';

export function getToolTip(): TooltipConfig {
    return Object.assign(new TooltipConfig(), { placement: 'right' });
}

@Component({
    selector: 'winery-add-component-data-component',
    templateUrl: 'addComponentData.component.html',
    providers: [
        InheritanceService,
        WineryNotificationService,
        {
            provide: TooltipConfig,
            useFactory: getToolTip
        }
    ]
})

export class WineryAddComponentDataComponent {

    @Input() toscaType: ToscaTypes;
    @Input() types: SelectData[];
    @Input() typeRequired: boolean;
    @Input() newComponentName: string;
    @Input() newComponentSelectedType: SelectData = new SelectData();
    @Input() validation: AddComponentValidation;
    @Output() typeChanged: EventEmitter<SelectData> = new EventEmitter();
    @Output() newComponentNameEvent: EventEmitter<string> = new EventEmitter();
    @Output() newComponentNamespaceEvent: EventEmitter<string> = new EventEmitter();
    @Output() validFormEvent: EventEmitter<boolean> = new EventEmitter<boolean>();

    loading: boolean;
    newComponentFinalName: string;
    newComponentVersion: WineryVersion = new WineryVersion('', 1, 1);
    newComponentNamespace: string;
    useComponentVersion = true;
    collapseVersioning = true;
    hideHelp = true;
    storage: Storage = localStorage;
    useStartNamespace = true;

    private readonly storageKey = 'hideVersionHelp';
    private artifactUrl: string;

    constructor(private notify: WineryNotificationService,
                private existService: ExistService) {
    }

    onInputChange() {
        this.validation = new AddComponentValidation();

        if (!this.newComponentName) {
            this.validFormEvent.emit(false);
            return { noNameAvailable: true };
        }
        this.newComponentFinalName = this.newComponentName;

        if (this.typeRequired && !this.newComponentSelectedType) {
            this.validation.noTypeAvailable = true;
            this.validFormEvent.emit(false);
            return { noTypeAvailable: true };
        }

        this.determineFinalName();

        if (this.newComponentVersion.componentVersion && this.useComponentVersion) {
            this.validation.noUnderscoresAllowed = this.newComponentVersion.componentVersion.includes('_');
            if (this.validation.noUnderscoresAllowed) {
                this.validFormEvent.emit(false);
                return { noUnderscoresAllowed: true };
            }
        }

        this.validation.noVersionProvidedWarning = !this.newComponentVersion.componentVersion
            || this.newComponentVersion.componentVersion.length === 0 || !this.useComponentVersion;
        this.newComponentNameEvent.emit(this.newComponentFinalName);
        this.newComponentNamespaceEvent.emit(this.newComponentNamespace);
    }

    onToggleUseVersion() {
        this.useComponentVersion = !this.useComponentVersion;
        this.onInputChange();
    }

    showHelp() {
        if (this.hideHelp) {
            this.storage.removeItem(this.storageKey);
        } else {
            this.storage.setItem(this.storageKey, 'true');
        }
        this.hideHelp = !this.hideHelp;
    }

    typeSelected(type: SelectData) {
        this.newComponentSelectedType = type;
        this.typeChanged.emit(this.newComponentSelectedType);
    }

    versioning() {
        this.collapseVersioning = !this.collapseVersioning;
    }

    private determineFinalName() {
        if (this.newComponentFinalName && this.newComponentFinalName.length > 0) {
            if (this.useComponentVersion) {
                this.newComponentFinalName += WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + this.newComponentVersion.toString();
            }
            this.createUrlAndCheck();
        }
    }

    createUrlAndCheck() {
        const namespace = encodeURIComponent(encodeURIComponent(this.newComponentNamespace));
        if (this.toscaType && namespace && this.newComponentFinalName) {
            this.artifactUrl = backendBaseURL + '/' + this.toscaType + '/' + encodeURIComponent(encodeURIComponent(
                this.newComponentNamespace)) + '/' + this.newComponentFinalName + '/';

            this.existService.check(this.artifactUrl)
                .subscribe(
                    () => this.validate(false),
                    () => this.validate(true)
                );
        }
        this.newComponentNameEvent.emit(this.newComponentFinalName);
        this.newComponentNamespaceEvent.emit(this.newComponentNamespace);
    }

    private validate(create: boolean) {
        this.validation = new AddComponentValidation();
        if (!create) {
            this.validation.noDuplicatesAllowed = true;
            this.validFormEvent.emit(false);
            return { noDuplicatesAllowed: true };

        } else {
            this.validation.noDuplicatesAllowed = false;
            this.validFormEvent.emit(true);
            return { noDuplicatesAllowed: false };
        }
    }

    createNoteTypeImplementationName(fullName: SelectData) {
        const version = Utils.getVersionFromString(fullName.text);
        this.newComponentVersion.componentVersion = version ? version.toString() : '';
        // we need to set both as it is required in the determineFinalName
        this.newComponentFinalName = this.newComponentName = Utils.getNameWithoutVersion(fullName.text) + '-Impl';
        this.determineFinalName();
    }

    createArtifactName(toscaComponent: ToscaComponent, nodeTypeQName: string, operation: string,
                       isImplementationArtifact: boolean, nodeType: string) {
        const artifactType = isImplementationArtifact ? 'IA' : 'DA';
        const wineryVersion = Utils.getVersionFromString(nodeTypeQName);
        const newVersion = WineryVersion.WINERY_VERSION_PREFIX + 1 + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_WORK_IN_PROGRESS_PREFIX + 1;
        this.newComponentFinalName = nodeType;
        if (operation) {
            this.newComponentVersion.componentVersion = (wineryVersion.componentVersion
                ? wineryVersion.componentVersion + WineryVersion.WINERY_VERSION_SEPARATOR
                : '')
                + operation
                + (isImplementationArtifact ? '' : '-' + artifactType);
        } else {
            this.newComponentVersion.componentVersion = (wineryVersion.getComponentVersion()
                ? wineryVersion.componentVersion + WineryVersion.WINERY_VERSION_SEPARATOR
                : '')
                + artifactType;
        }
        this.newComponentFinalName += (this.newComponentVersion.componentVersion ? WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR
            + this.newComponentVersion.componentVersion : '')
            + WineryVersion.WINERY_VERSION_SEPARATOR + newVersion;
        this.createUrlAndCheck();
    }
}
