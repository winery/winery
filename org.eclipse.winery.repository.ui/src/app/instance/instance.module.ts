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
 *     Niko Stadelmaier - add admin component
 */
import { NgModule } from '@angular/core';
import { InstanceComponent } from './instance.component';
import { InstanceHeaderComponent } from './instanceHeader/instanceHeader.component';
import { WineryLoaderModule } from '../wineryLoader/wineryLoader.module';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { WineryPipesModule } from '../wineryPipes/wineryPipes.module';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PropertyRenameComponent } from './instanceHeader/propertyRename/propertyRename.component';
import { FormsModule } from '@angular/forms';

@NgModule({
    imports: [
        CommonModule,
        RouterModule,
        WineryLoaderModule,
        WineryModalModule,
        WineryPipesModule,
        FormsModule
    ],
    exports: [InstanceComponent],
    declarations: [
        InstanceComponent,
        InstanceHeaderComponent,
        PropertyRenameComponent
    ],
    providers: [],
})
export class InstanceModule {
}
