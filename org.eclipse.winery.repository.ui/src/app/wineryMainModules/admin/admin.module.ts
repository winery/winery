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
import { AdminRouterModule } from './adminRouter.module';
import { LoggerComponent } from '../../instance/admin/logger/logger.component';
import { NamespacesComponent } from '../../instance/admin/namespaces/namespaces.component';
import { TypeWithShortNameComponent } from '../../instance/admin/typesWithShortName/typeWithShortName.component';
import { WineryModalModule } from '../../wineryModalModule/winery.modal.module';
import { WineryTableModule } from '../../wineryTableModule/wineryTable.module';
import { RepositoryModule } from '../../instance/admin/repository/repository.module';
import { WineryLoaderModule } from '../../wineryLoader/wineryLoader.module';
import { CommonModule } from '@angular/common';
import { WineryDuplicateValidatorModule } from '../../wineryValidators/wineryDuplicateValidator.module';
import { WineryNamespaceSelectorModule } from '../../wineryNamespaceSelector/wineryNamespaceSelector.module';
import { FormsModule } from '@angular/forms';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RepositoryModule,
        WineryDuplicateValidatorModule,
        WineryLoaderModule,
        WineryModalModule,
        WineryTableModule,
        WineryNamespaceSelectorModule,
        AdminRouterModule,
    ],
    declarations: [
        NamespacesComponent,
        LoggerComponent,
        TypeWithShortNameComponent
    ]
})
export class AdminModule {
}
