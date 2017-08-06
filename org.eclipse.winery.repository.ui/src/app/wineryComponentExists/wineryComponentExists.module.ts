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
import { NgModule } from '@angular/core';

import { WineryComponentExistsComponent } from './wineryComponentExists.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { ExistService } from '../wineryUtils/existService';
import { WineryNamespaceSelectorModule } from '../wineryNamespaceSelector/wineryNamespaceSelector.module';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        CommonModule,
        WineryNamespaceSelectorModule,
    ],
    exports: [WineryComponentExistsComponent],
    declarations: [WineryComponentExistsComponent],
    providers: [
        ExistService
    ],
})
export class WineryComponentExistsModule {
}
