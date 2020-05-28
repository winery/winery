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
import { SectionService } from '../section/section.service';
import { InheritanceService } from '../instance/sharedComponents/inheritance/inheritance.service';
import { TooltipConfig } from 'ngx-bootstrap';
import { AddComponentValidation } from '../wineryAddComponentModule/addComponentValidation';
import { isNullOrUndefined } from 'util';
import { WineryVersion } from '../model/wineryVersion';
import { SelectData } from '../model/selectData';
import { ToscaTypes } from '../model/enums';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { ExistService } from '../wineryUtils/existService';
import { backendBaseURL } from '../configuration';
import { ToscaComponent } from '../model/toscaComponent';

export function getToolTip(): TooltipConfig {
    return Object.assign(new TooltipConfig(), { placement: 'right' });
}

@Component({
    selector: 'winery-add-component-data-component',
    templateUrl: 'addComponentData.component.html',
    providers: [
        SectionService,
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

    constructor(private sectionService: SectionService, private notify: WineryNotificationService,
                private existService: ExistService) {
    }

    onInputChange() {
        this.validation = new AddComponentValidation();
        this.newComponentFinalName = this.newComponentName;

        if (this.typeRequired && isNullOrUndefined(this.newComponentSelectedType)) {
            this.validation.noTypeAvailable = true;
            return { noTypeAvailable: true };
        }

        this.determineFinalName();

        if (this.newComponentVersion.componentVersion && this.useComponentVersion) {
            this.validation.noUnderscoresAllowed = this.newComponentVersion.componentVersion.includes('_');
            if (this.validation.noUnderscoresAllowed) {
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

    private versioning() {
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
        this.artifactUrl = backendBaseURL + '/' + this.toscaType + '/' + encodeURIComponent(encodeURIComponent(
            this.newComponentNamespace)) + '/' + this.newComponentFinalName + '/';

        this.existService.check(this.artifactUrl)
            .subscribe(
                () => this.validate(false),
                () => this.validate(true)
            );
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
        const name = fullName.text.substring(0, fullName.text.lastIndexOf('_'));
        const newVersion = fullName.text.slice(fullName.text.indexOf('_'), fullName.text.length);
        this.newComponentVersion.componentVersion = newVersion.substring(1, newVersion.indexOf('-'));
        this.newComponentName = name + '-Impl';
        this.newComponentFinalName = this.newComponentName + newVersion;
        this.createUrlAndCheck();
    }

    createArtifactName(toscaComponent: ToscaComponent, wineryVersion: WineryVersion, operation: string,
                       isImplementationArtifact: boolean, nodeType: string) {
        const artifactType = isImplementationArtifact ? 'IA' : 'DA';
        const newVersion = wineryVersion.getWineryAndWipVersion();
        this.newComponentFinalName = nodeType;
        if (operation) {
            this.newComponentVersion.componentVersion = operation + WineryVersion.WINERY_VERSION_SEPARATOR
                + (wineryVersion.componentVersion
                    ? wineryVersion.componentVersion + WineryVersion.WINERY_VERSION_SEPARATOR
                    : '')
                + artifactType;
            this.newComponentFinalName += WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR
                + this.newComponentVersion.componentVersion + WineryVersion.WINERY_VERSION_SEPARATOR + newVersion;
        } else {
            this.newComponentVersion.componentVersion = (wineryVersion.getComponentVersion()
                ? wineryVersion.componentVersion + WineryVersion.WINERY_VERSION_SEPARATOR
                : '')
                + artifactType;
            this.newComponentFinalName += WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR
                + this.newComponentVersion.componentVersion + WineryVersion.WINERY_VERSION_SEPARATOR + newVersion;
        }
        this.createUrlAndCheck();
    }
}
