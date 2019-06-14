/*******************************************************************************
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
import { ConsistencyCheckComponent } from '../../instance/admin/consistencyCheck/consistencyCheck.component';
import { AccordionModule, AlertModule, CollapseModule, ModalModule, ProgressbarModule } from 'ngx-bootstrap';
import { ErrorElementToLinkPipe } from '../../instance/admin/consistencyCheck/errorElementToLink.pipe';
import { AccountabilityComponent } from '../../instance/admin/accountability/accountability.component';
import { SelectModule } from 'ng2-select';
import { AuthorizationComponent } from '../../instance/admin/accountability/authorization/authorization.component';
import { AuthenticationComponent } from '../../instance/admin/accountability/authentication/authentication.component';
import { ConfigurationComponent } from '../../instance/admin/accountability/configuration/configuration.component';
import { WineryUploaderModule } from '../../wineryUploader/wineryUploader.module';
import { ConfigurationService } from '../../instance/admin/accountability/configuration/configuration.service';
import { AccountabilityService } from '../../instance/admin/accountability/accountability.service';
import { WineryFileComparisonModule } from '../../wineryFileComparisonModule/wineryFileComparison.module';
import { ProvenanceComponent } from '../../instance/admin/accountability/provenance/provenance.component';
import { FeatureConfigurationComponent } from '../../instance/admin/configuration/configuration.component';
import { WineryFeatureToggleModule } from '../../wineryFeatureToggleModule/winery-feature-toggle.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RepositoryModule,
        SelectModule,
        WineryDuplicateValidatorModule,
        WineryLoaderModule,
        WineryModalModule,
        WineryTableModule,
        WineryUploaderModule,
        WineryNamespaceSelectorModule,
        WineryFileComparisonModule,
        ProgressbarModule.forRoot(),
        AlertModule.forRoot(),
        AccordionModule.forRoot(),
        CollapseModule.forRoot(),
        ModalModule.forRoot(),
        AdminRouterModule,
        WineryFeatureToggleModule
    ],
    declarations: [
        NamespacesComponent,
        LoggerComponent,
        TypeWithShortNameComponent,
        ConsistencyCheckComponent,
        ErrorElementToLinkPipe,
        AccountabilityComponent,
        AuthorizationComponent,
        AuthenticationComponent,
        ConfigurationComponent,
        FeatureConfigurationComponent,
        ProvenanceComponent,
    ],
    providers: [ConfigurationService, AccountabilityService]

})
export class AdminModule {
}
