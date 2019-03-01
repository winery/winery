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
import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {RemoveWhiteSpacesPipe} from '../../wineryPipes/removeWhiteSpaces.pipe';
import {ModalDirective} from 'ngx-bootstrap';
import {ToscaComponent} from '../../model/toscaComponent';
import {ToscaTypes} from '../../model/enums';
import {WineryVersion} from '../../model/wineryVersion';
import {InstanceService} from '../instance.service';
import { AccountabilityService } from '../admin/accountability/accountability.service';
import { ConfigurationService } from '../admin/accountability/configuration/configuration.service';

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
    @Input() versions: WineryVersion[];
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
    accountabilityEnabled: boolean;

    constructor(private router: Router, private accountabilityConfig: ConfigurationService, public sharedData: InstanceService) {
    }

    ngOnInit(): void {
        this.accountabilityEnabled = this.accountabilityConfig.isAccountablilityCheckEnabled();

        if (this.subMenu.length > 7) {
            this.needTwoLines = true;
        }

        if (this.toscaComponent.toscaType === ToscaTypes.Imports || this.toscaComponent.toscaType === ToscaTypes.Admin) {
            this.showManagementButtons = false;
        }
    }

    removeConfirmed() {
        this.deleteConfirmed.emit();
    }
}
