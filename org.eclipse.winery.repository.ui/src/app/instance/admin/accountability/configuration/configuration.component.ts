/********************************************************************************
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

import { Component, OnInit } from '@angular/core';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { Configuration } from './Configuration';
import { ConfigurationService } from './configuration.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    templateUrl: 'configuration.component.html'
})
export class ConfigurationComponent implements OnInit {
    configuration: Configuration;
    loading = true;
    error: string;
    selectedKeystoreFile: File = undefined;

    constructor(protected service: ConfigurationService, protected notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        this.loading = true;
        this.service.loadConfiguration()
            .subscribe(result => {
                    this.handleDataLoaded(result);
                },
                e => this.handleError(e));
    }

    handleDataLoaded(data: Configuration) {
        if (data !== null && data !== undefined) {
            this.configuration = data;
        } else {
            this.notify.error('Configuration data unavailable!');
        }

        this.loading = false;
    }

    handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.selectedKeystoreFile = undefined;
        this.notify.error(error.message);
    }

    keyStoreSelected(files: FileList) {
        if (files.length > 0) {
            this.selectedKeystoreFile = files[0];
        } else {
            this.selectedKeystoreFile = undefined;
        }
    }

    onSave() {
        this.loading = true;
        this.service.saveConfiguration(this.selectedKeystoreFile, this.configuration)
            .subscribe(() => this.handleConfigurationSaved(),
                e => this.handleError(e));
    }

    onRestore() {
        this.loading = true;
        this.service.restoreDefaults()
            .subscribe(
                data => this.handleDataLoaded(data),
                e => this.handleError(e));
    }

    handleConfigurationSaved() {
        this.loading = false;
        // selectedKeystoreFile can be undefined at this point if the configuration change did not include changing
        // the keystore
        if (this.selectedKeystoreFile !== null && this.selectedKeystoreFile !== undefined) {
            this.configuration.activeKeystore = this.selectedKeystoreFile.name;
        }
        this.selectedKeystoreFile = undefined;
        this.notify.success('Configuration applied successfully!');
    }
}
