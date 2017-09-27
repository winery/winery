/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
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
import { NgxPaginationModule } from 'ngx-pagination';
import { SelectModule } from 'ng2-select';
import { EntityContainerComponent } from './entityContainer/entityContainer.component';
import { WineryLoaderModule } from '../wineryLoader/wineryLoader.module';
import { WineryNamespaceSelectorModule } from '../wineryNamespaceSelector/wineryNamespaceSelector.module';
import { WineryDuplicateValidatorModule } from '../wineryValidators/wineryDuplicateValidator.module';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { SectionComponent } from './section.component';
import { SectionPipe } from './section.pipe';
import { WineryUploaderModule } from '../wineryUploader/wineryUploader.module';
import { TooltipModule } from 'ngx-bootstrap';
import { WineryPipesModule } from '../wineryPipes/wineryPipes.module';
import { XaasPackagerComponent } from './xaasPackager/xaasPackager.component';
import { TagInputModule } from 'ngx-chips';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        CommonModule,
        NgxPaginationModule,
        WineryDuplicateValidatorModule,
        WineryNamespaceSelectorModule,
        WineryLoaderModule,
        WineryModalModule,
        RouterModule,
        SelectModule,
        WineryUploaderModule,
        TooltipModule,
        WineryPipesModule,
        TagInputModule
    ],
    exports: [SectionComponent],
    declarations: [
        SectionComponent,
        EntityContainerComponent,
        SectionPipe,
        XaasPackagerComponent
    ],
    providers: [],
})
export class SectionModule {
}
