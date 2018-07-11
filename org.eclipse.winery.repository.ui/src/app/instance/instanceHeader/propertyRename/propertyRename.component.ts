/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

import { Component, Input, OnChanges, OnInit, ViewChild } from '@angular/core';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { PropertyRenameService } from './propertyRename.service';
import { ToscaComponent } from '../../../model/toscaComponent';
import { NgForm } from '@angular/forms';
import { ToscaTypes } from '../../../model/enums';
import { ModalDirective } from 'ngx-bootstrap';
import { Router } from '@angular/router';
import { InstanceService } from '../../instance.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

/**
 * This adds a an editable field to the html that manipulates either the namespace or the id/name of a ToscaComponent
 *
 * @Input id: this is the id of the property which must either be 'id' or 'namespace'
 * @Input toscaComponent: the toscaComponent which's id/namespace is edited/displayed
 */
@Component({
    selector: 'winery-property-rename',
    templateUrl: 'propertyRename.component.html',
    providers: [PropertyRenameService],
    styleUrls: [
        'propertyRename.component.css'
    ]
})
export class PropertyRenameComponent implements OnInit, OnChanges {

    @Input() propertyName: string;
    @Input() toscaComponent: ToscaComponent;
    @Input() multipleVersionsAvailable: boolean;
    @ViewChild('renameComponentForm') renameComponentForm: NgForm;
    @ViewChild('confirmRenameModal') confirmRenameModal: ModalDirective;
    editMode = false;
    disableEditing = true;
    propertyValue = '';

    constructor(public sharedData: InstanceService,
                private service: PropertyRenameService,
                private notify: WineryNotificationService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.service.setToscaComponent(this.toscaComponent);
        this.service.setPropertyName(this.propertyName);
        this.disableEditing = this.toscaComponent.toscaType === ToscaTypes.Imports
            || this.toscaComponent.toscaType === ToscaTypes.Admin;
    }

    ngOnChanges() {
        this.service.setToscaComponent(this.toscaComponent);
        this.setPropertyValue();
    }

    onClickEdit() {
        this.editMode = true;
    }

    onCancel() {
        this.editMode = false;
        this.setPropertyValue();
        this.confirmRenameModal.hide();
    }

    renameAllVersions() {
        this.confirmRenameModal.hide();
        this.updateValue(true);
    }

    renameThisVersionOnly() {
        this.confirmRenameModal.hide();
        this.updateValue(false);
    }

    onSaveClicked() {
        if (this.multipleVersionsAvailable) {
            this.confirmRenameModal.show();
        } else {
            this.renameThisVersionOnly();
        }
    }

    private updateValue(renameAllComponents: boolean) {
        this.editMode = false;
        this.service.setPropertyValue(this.propertyValue, renameAllComponents).subscribe(
            data => this.handleUpdateValue(data),
            error => this.handleError(error)
        );
    }

    private setPropertyValue() {
        if (this.propertyName === 'localName') {
            this.propertyValue = this.toscaComponent.localNameWithoutVersion;
        } else {
            this.propertyValue = this.toscaComponent.namespace;
        }
    }

    private handleUpdateValue(data: HttpResponse<string>) {
        this.notify.success('Renamed ' + this.propertyName + ' to ' + this.propertyValue);
        const sliceFrom = data.body.indexOf(this.toscaComponent.toscaType);
        this.router.navigate([decodeURIComponent(data.body.slice(sliceFrom))]);
    }

    private handleError(error: HttpErrorResponse): void {
        this.notify.error(error.message);
    }

}
