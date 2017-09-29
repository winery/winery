/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { RemoveWhiteSpacesPipe } from '../../wineryPipes/removeWhiteSpaces.pipe';
import { ModalDirective } from 'ngx-bootstrap';
import { ToscaComponent } from '../../wineryInterfaces/toscaComponent';
import { ToscaTypes } from '../../wineryInterfaces/enums';

@Component({
    selector: 'winery-instance-header',
    templateUrl: './instanceHeader.component.html',
    styleUrls: [
        './instanceHeader.component.css'
    ],
    providers: [
        RemoveWhiteSpacesPipe
    ],
})

export class InstanceHeaderComponent implements OnInit {

    @Input() toscaComponent: ToscaComponent;
    @Input() typeUrl: string;
    @Input() typeId: string;
    @Input() typeOf: string;
    @Input() subMenu: string[];
    @Input() imageUrl: string;
    @Output() deleteConfirmed: EventEmitter<any> = new EventEmitter();

    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;

    needTwoLines = false;
    selectedTab: string;
    showManagementButtons = true;

    constructor(private router: Router) {
    }

    ngOnInit(): void {
        if (this.subMenu.length > 7) {
            this.needTwoLines = true;
        }

        if (this.toscaComponent.toscaType === ToscaTypes.Imports) {
            this.showManagementButtons = false;
        }
    }

    removeConfirmed() {
        this.deleteConfirmed.emit();
    }
}
