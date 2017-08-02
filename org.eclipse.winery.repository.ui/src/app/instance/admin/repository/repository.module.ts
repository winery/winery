/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { NgModule } from '@angular/core';
import { RepositoryComponent } from './repository.component';
import { WineryUploaderModule } from '../../../wineryUploader/wineryUploader.module';
import { RepositoryService } from './repository.service';
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';

@NgModule({
    imports: [
        WineryUploaderModule,
        WineryModalModule
    ],
    exports: [RepositoryComponent],
    declarations: [RepositoryComponent],
    providers: [RepositoryService],
})
export class RepositoryModule {
}

