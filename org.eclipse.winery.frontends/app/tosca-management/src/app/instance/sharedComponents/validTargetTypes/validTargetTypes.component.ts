/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import { Component, Input, ViewChild } from '@angular/core';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { SelectItem } from 'ng2-select';
import { ValidSourceTypesComponent } from '../validSourceTypes/validSourceTypes.component';
import { ToscaTypes } from '../../../model/enums';
import { InstanceService } from '../../instance.service';
import { ValidSourceTypesService } from '../validSourceTypes/validSourceTypes.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';

export class ValidTargetType {
    name: string;
    namespace: string;
}

@Component({
    templateUrl: 'validTargetTypes.component.html',
    providers: [ValidSourceTypesService]
})
export class ValidTargetTypesComponent extends ValidSourceTypesComponent {

    loading = true;
    resource = 'validtargets';

    validType = ToscaTypes.CapabilityType;


    @ViewChild('addModal') addModal: ModalDirective;
    addModalRef: BsModalRef;

    constructor(public sharedData: InstanceService,
                protected service: ValidSourceTypesService,
                protected notify: WineryNotificationService,
                protected modalService: BsModalService) {
        super(sharedData, service, notify, modalService);
    }
}
