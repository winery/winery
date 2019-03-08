/*******************************************************************************
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
 *******************************************************************************/
import { Component } from '@angular/core';
import { AccountabilityService } from '../accountability.service';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { SelectData } from '../../../../model/selectData';
import { AccountabilityParentComponent } from '../accountabilityParent.component';
import { AuthorizationElement } from '../../../../model/provenance';

@Component({
    templateUrl: 'authorization.component.html'
})
export class AuthorizationComponent extends AccountabilityParentComponent {
    participant: AuthorizationElement = {identity: '', address: '', transactionHash: '', unixTimestamp: 0};

    constructor(protected service: AccountabilityService, protected notify: WineryNotificationService) {
        super(service, notify);
    }

    handleAddition() {
        this.notify.success('Successfully added participant ' + this.participant.identity
            + ' to ' + this.selectedProvenanceId.id);
        this.loading = false;
    }

    onOk() {
        this.addNewParticipant();
    }

    private addNewParticipant() {
        this.loading = true;
        this.service.authorize(this.selectedProvenanceId.id, this.participant)
            .subscribe(
                () => this.handleAddition(),
                error => this.handleError(error)
            );
    }
}
