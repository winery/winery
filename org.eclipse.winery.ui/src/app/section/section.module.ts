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
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { FileUploadModule } from 'ng2-file-upload';
import { Ng2PaginationModule } from 'ng2-pagination';
import { SelectModule } from 'ng2-select';
import { EntityContainerComponent } from '../entityContainer/entityContainer.component';
import { LoaderModule } from '../loader/loader.module';
import { NamespaceSelectorModule } from '../namespaceSelector/namespaceSelector.module';
import { NotificationModule } from '../notificationModule/notification.module';
import { UrlDecodePipe } from '../pipes/urlDecode.pipe';
import { UrlEncodePipe } from '../pipes/urlEncode.pipe';
import { DuplicateValidatorModule } from '../validators/duplicateValidator.module';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { SectionComponent } from './section.component';
import { SectionPipe } from './section.pipe';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        CommonModule,
        Ng2PaginationModule,
        DuplicateValidatorModule,
        NamespaceSelectorModule,
        LoaderModule,
        WineryModalModule,
        NotificationModule.forRoot(),
        FileUploadModule,
        RouterModule,
        SelectModule,
    ],
    exports: [SectionComponent],
    declarations: [
        SectionComponent,
        EntityContainerComponent,
        UrlDecodePipe,
        UrlEncodePipe,
        SectionPipe
    ],
    providers: [],
})
export class SectionModule {
}
