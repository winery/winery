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
 *     Niko Stadelmaier - module refactoring
 */
import { NgModule } from '@angular/core';
import { WineryUploaderComponent } from './wineryUploader.component';
import { WineryUploaderService } from './wineryUploader.service';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { FileUploadModule } from 'ng2-file-upload';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { WineryLoaderModule } from '../wineryLoader/wineryLoader.module';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        FileUploadModule,
        WineryLoaderModule,
    ],
    exports: [WineryUploaderComponent],
    declarations: [WineryUploaderComponent],
})
export class WineryUploaderModule { }
