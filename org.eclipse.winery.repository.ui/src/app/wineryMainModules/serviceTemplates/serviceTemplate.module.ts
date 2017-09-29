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

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServiceTemplateRouterModule } from './serviceTemplateRouter.module';
import { TopologyTemplateComponent } from '../../instance/serviceTemplates/topologyTemplate/topologyTemplate.component';
import { PlansComponent } from '../../instance/serviceTemplates/plans/plans.component';
import { WineryLoaderModule } from '../../wineryLoader/wineryLoader.module';
import { WineryModalModule } from '../../wineryModalModule/winery.modal.module';
import { WineryIoParameterModule } from '../../wineryIoParameter/wineryIoParameters.module';
import { WineryUploaderModule } from '../../wineryUploader/wineryUploader.module';
import { SelectModule } from 'ng2-select';
import { FormsModule } from '@angular/forms';
import { WineryTableModule } from '../../wineryTableModule/wineryTable.module';
import { InstanceModule } from '../../instance/instance.module';
import { SelfServicePortalModule } from '../../instance/serviceTemplates/selfServicePortal/selfServicePortal.module';
import { BoundaryDefinitionsModule } from '../../instance/serviceTemplates/boundaryDefinitions/boundaryDefinitions.module';
import { TagModule } from '../../instance/serviceTemplates/tag/tag.module';
import { DocumentationModule } from '../../instance/sharedComponents/documentation/documentation.module';
import { WineryReadmeModule } from '../../wineryReadmeModule/wineryReadme.module';
import { WineryLicenseModule } from '../../wineryLicenseModule/wineryLicense.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        SelectModule,
        InstanceModule,
        SelfServicePortalModule,
        BoundaryDefinitionsModule,
        TagModule,
        DocumentationModule,
        WineryLoaderModule,
        WineryModalModule,
        WineryIoParameterModule,
        WineryUploaderModule,
        WineryTableModule,
        ServiceTemplateRouterModule,
        WineryReadmeModule,
        WineryLicenseModule
    ],
    declarations: [
        TopologyTemplateComponent,
        PlansComponent,
    ]
})
export class ServiceTemplateModule {
}
