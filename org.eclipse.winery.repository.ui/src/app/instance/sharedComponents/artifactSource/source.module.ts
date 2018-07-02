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
import {NgModule} from '@angular/core';

import {CommonModule} from '@angular/common';
import {WineryLoaderModule} from '../../../wineryLoader/wineryLoader.module';
import {FormsModule} from '@angular/forms';
import {WineryModalModule} from '../../../wineryModalModule/winery.modal.module';
import {WineryUploaderModule} from '../../../wineryUploader/wineryUploader.module';
import {SourceComponent} from './source.component';
import {WineryEditorModule} from '../../../wineryEditorModule/wineryEditor.module';
import {WineryDuplicateValidatorModule} from '../../../wineryValidators/wineryDuplicateValidator.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        WineryLoaderModule,
        WineryModalModule,
        WineryUploaderModule,
        WineryEditorModule,
        WineryDuplicateValidatorModule,
    ],
    exports: [
        SourceComponent
    ],
    declarations: [
        SourceComponent
    ],
    providers: [],
})
export class WinerySourceModule {
}
