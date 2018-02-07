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
import {AdminRouterModule} from './adminRouter.module';
import {LoggerComponent} from '../../instance/admin/logger/logger.component';
import {NamespacesComponent} from '../../instance/admin/namespaces/namespaces.component';
import {TypeWithShortNameComponent} from '../../instance/admin/typesWithShortName/typeWithShortName.component';
import {WineryModalModule} from '../../wineryModalModule/winery.modal.module';
import {WineryTableModule} from '../../wineryTableModule/wineryTable.module';
import {RepositoryModule} from '../../instance/admin/repository/repository.module';
import {WineryLoaderModule} from '../../wineryLoader/wineryLoader.module';
import {CommonModule} from '@angular/common';
import {WineryDuplicateValidatorModule} from '../../wineryValidators/wineryDuplicateValidator.module';
import {WineryNamespaceSelectorModule} from '../../wineryNamespaceSelector/wineryNamespaceSelector.module';
import {FormsModule} from '@angular/forms';
import {ConsistencyCheckComponent} from '../../instance/admin/consistencyCheck/consistencyCheck.component';
import {ProgressbarModule} from 'ngx-bootstrap';
import {ErrorElementToLinkPipe} from '../../instance/admin/consistencyCheck/errorElementToLink.pipe';

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
        ProgressbarModule.forRoot(),
        AdminRouterModule,
    ],
    declarations: [
        NamespacesComponent,
        LoggerComponent,
        TypeWithShortNameComponent,
        ConsistencyCheckComponent,
        ErrorElementToLinkPipe
    ]
})
export class AdminModule {
}
