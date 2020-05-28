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
import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { InstanceService, ToscaLightCompatibilityData } from './instance.service';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { RemoveWhiteSpacesPipe } from '../wineryPipes/removeWhiteSpaces.pipe';
import { ExistService } from '../wineryUtils/existService';
import { WineryInstance } from '../model/wineryComponent';
import { ToscaTypes } from '../model/enums';
import { ToscaComponent } from '../model/toscaComponent';
import { Utils } from '../wineryUtils/utils';
import { WineryVersion } from '../model/wineryVersion';
import { HttpErrorResponse } from '@angular/common/http';
import { SubMenuItem } from '../model/subMenuItem';

@Component({
    templateUrl: 'instance.component.html',
    providers: [
        InstanceService,
        RemoveWhiteSpacesPipe,
    ]
})
export class InstanceComponent implements OnDestroy {

    availableTabs: SubMenuItem[];
    toscaComponent: ToscaComponent;
    versions: WineryVersion[];
    typeUrl: string;
    typeId: string;
    typeOf: string;
    imageUrl: string;
    newVersionAvailable: boolean;
    editable = true;
    loadingVersions = true;
    loadingData = true;

    routeSub: Subscription;
    toscaLightCompatibilityData: ToscaLightCompatibilityData;

    constructor(private route: ActivatedRoute,
                private router: Router,
                private service: InstanceService,
                private notify: WineryNotificationService,
                private existService: ExistService) {
        this.routeSub = this.route
            .data
            .subscribe(data => {
                    this.newVersionAvailable = false;
                    // For convenience, we accept editing already existing components  without versions
                    this.editable = true;
                    this.toscaComponent = data['resolveData'] ? data['resolveData'] : new ToscaComponent(ToscaTypes.Admin, '', '');

                    this.service.setSharedData(this.toscaComponent);

                    if (this.toscaComponent
                        && this.toscaComponent.toscaType !== ToscaTypes.Imports
                        && this.toscaComponent.toscaType !== ToscaTypes.Admin) {
                        if (this.toscaComponent.toscaType === ToscaTypes.NodeType) {
                            const img = this.service.path + '/appearance/50x50';
                            this.existService.check(img)
                                .subscribe(
                                    () => this.imageUrl = img,
                                    () => this.imageUrl = null,
                                );
                        }
                        this.service.getComponentData()
                            .subscribe(
                                compData => this.handleComponentData(compData)
                            );
                        this.getVersionInfo();
                        if (this.toscaComponent.toscaType === ToscaTypes.ServiceTemplate) {
                            this.getToscaLightCompatibility();
                        }
                    } else {
                        this.loadingVersions = false;
                        this.loadingData = false;
                        this.editable = this.toscaComponent.toscaType === ToscaTypes.Admin;
                    }

                    this.availableTabs = this.service.getSubMenuByResource();
                },
                error => this.handleError(error)
            );
    }

    private getVersionInfo() {
        this.service.getVersions()
            .subscribe(
                versions => this.handleVersions(versions),
                error => this.handleError(error)
            );
    }

    deleteComponent() {
        this.service.deleteComponent().subscribe(
            data => this.handleDelete(),
            error => this.handleError(error)
        );
    }

    private handleComponentData(data: WineryInstance) {
        this.typeUrl = Utils.getTypeOfTemplateOrImplementation(this.toscaComponent.toscaType);

        if (this.typeUrl) {
            this.typeUrl = '/' + this.typeUrl;
            const tempOrImpl = data.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0];
            let qName: string[];

            if (tempOrImpl.type) {
                qName = tempOrImpl.type.slice(1).split('}');
                this.typeOf = 'Type: ';
            } else if (tempOrImpl.nodeType) {
                qName = tempOrImpl.nodeType.slice(1).split('}');
                this.typeOf = 'Implementation for ';
            } else if (tempOrImpl.relationshipType) {
                qName = tempOrImpl.relationshipType.slice(1).split('}');
                this.typeOf = 'Implementation for ';
            }

            if (qName.length === 2) {
                this.typeUrl += '/' + encodeURIComponent(qName[0]) + '/' + qName[1];
                this.typeId = qName[1];
            } else {
                this.typeUrl = null;
            }
        }

        this.loadingData = false;
    }

    private handleVersions(list: WineryVersion[]) {
        // create instances of class {@link WineryVersion}
        const versions: WineryVersion[] = [];
        for (const obj of list) {
            versions.push(
                new WineryVersion(
                    obj.componentVersion,
                    obj.wineryVersion,
                    obj.workInProgressVersion,
                    obj.currentVersion,
                    obj.latestVersion,
                    obj.releasable,
                    obj.editable)
            );
        }
        this.versions = this.service.versions = versions;
        this.loadingVersions = false;

        const version = this.versions.find(v => v.currentVersion);
        if (version) {
            this.service.currentVersion = version;
            this.newVersionAvailable = !version.latestVersion;
            this.editable = version.editable;
        }
    }

    private handleDelete() {
        this.notify.success('Successfully deleted ' + this.toscaComponent.localName);
        this.router.navigate(['/' + this.toscaComponent.toscaType]);
    }

    private handleError(error: HttpErrorResponse) {
        this.notify.error(error.message, 'Error');
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }

    private getToscaLightCompatibility() {
        this.service.getToscaLightCompatibility()
            .subscribe(
                data => this.handleToscaLightCompatibilityData(data),
                error => this.handleError(error)
            );
    }

    private handleToscaLightCompatibilityData(data: ToscaLightCompatibilityData) {
        this.toscaLightCompatibilityData = data;
    }
}
