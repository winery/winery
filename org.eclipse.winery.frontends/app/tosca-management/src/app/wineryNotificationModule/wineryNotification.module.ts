/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { ModuleWithProviders, NgModule } from '@angular/core';
import { WineryNotificationService } from './wineryNotification.service';
import { CommonModule, DatePipe } from '@angular/common';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
    imports: [
        CommonModule,
        BrowserAnimationsModule,
        ToastrModule.forRoot()
    ],
    providers: [DatePipe],
})
export class WineryNotificationModule {
    static forRoot(): ModuleWithProviders {
        return {
            ngModule: WineryNotificationModule,
            providers: [WineryNotificationService]
        };
    }
}

