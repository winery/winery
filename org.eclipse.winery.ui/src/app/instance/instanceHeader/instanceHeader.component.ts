/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */

import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { RemoveWhiteSpacesPipe } from '../../pipes/removeWhiteSpaces.pipe';
import { InstanceService } from '../instance.service';
import { backendBaseUri } from '../../configuration';

@Component({
    selector: 'winery-instance-header',
    templateUrl: './instanceHeader.component.html',
    styleUrls: [
        './instanceHeader.component.css'
    ],
    providers: [
        RemoveWhiteSpacesPipe
    ],
    inputs: [
        'selectedNamespace',
        'selectedComponentId',
        'selectedResource',
        'subMenu'
    ]
})

export class InstanceHeaderComponent implements OnInit {

    @Input() selectedNamespace: string;
    @Input() selectedComponentId: string;
    @Input() selectedResource: string;
    @Input() subMenu: string[];
    @Input() imageUri: string;
    @Output() deleteConfirmed: EventEmitter<any> = new EventEmitter();

    needTwoLines: boolean = false;
    selectedTab: string;
    backendLink: string;

    constructor(private router: Router, private sharedData: InstanceService) {}

    ngOnInit(): void {
        if (this.subMenu.length > 7) {
            this.needTwoLines = true;
        }
        this.backendLink = backendBaseUri + this.sharedData.path;
    }

    removeConfirmed() {
        this.deleteConfirmed.emit();
    }
}
