/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */

import { Component, Input, OnChanges, OnInit, ViewChild } from '@angular/core';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { PropertyRenameService } from './propertyRename.service';
import { ToscaComponent } from '../../../wineryInterfaces/toscaComponent';
import { NgForm } from '@angular/forms';
import { ToscaTypes } from '../../../wineryInterfaces/enums';

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
    @ViewChild('renameComponentForm') renameComponentForm: NgForm;
    editMode = false;
    disableEditing = true;
    propertyValue = '';

    constructor(private service: PropertyRenameService,
                private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        this.service.setToscaComponent(this.toscaComponent);
        this.service.setPropertyName(this.propertyName);
        this.disableEditing = this.toscaComponent.toscaType === ToscaTypes.Imports
            || this.toscaComponent.toscaType === ToscaTypes.Admin;
    }

    ngOnChanges() {
        this.service.setToscaComponent(this.toscaComponent);
    }

    updateValue() {
        this.service.setPropertyValue(this.propertyValue).subscribe(
            data => this.handleUpdateValue(),
            error => this.handleError(error)
        );
    }

    onClickEdit() {
        if (this.propertyName === 'localName') {
            this.propertyValue = this.toscaComponent.localName;
        } else {
            this.propertyValue = this.toscaComponent.namespace;
        }
        this.editMode = true;
    }

    onSaveValue() {
        this.editMode = false;
        this.updateValue();
    }

    onCancel() {
        this.editMode = false;
    }

    private handleUpdateValue() {
        this.notify.success('Renamed ' + this.propertyName + ' to ' + this.propertyValue);
        this.service.reload(this.propertyValue);
    }

    private handleError(error: any): void {
        this.notify.error('id/name ' + this.propertyValue
            + ' already exists in the current namespace, please enter a different ' + this.propertyName);
    }
}
