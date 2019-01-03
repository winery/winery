/********************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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
import { CommonModule } from '@angular/common';
import { TopologyTemplateModule } from '../../instance/sharedComponents/topologyTemplate/topologyTemplate.module';
import { InstanceModule } from '../../instance/instance.module';
import { WineryReadmeModule } from '../../wineryReadmeModule/wineryReadme.module';
import { WineryLicenseModule } from '../../wineryLicenseModule/wineryLicense.module';
import { PatternRefinementModelRouterModule } from './patternRefinementModelRouter.module';
import { RefinementModelsModule } from '../../instance/refinementModels/refinementModels.module';

@NgModule({
    imports: [
        CommonModule,
        InstanceModule,
        TopologyTemplateModule,
        WineryReadmeModule,
        WineryLicenseModule,
        RefinementModelsModule,
        PatternRefinementModelRouterModule,
    ]
})
export class PatternRefinementModelModule {
}
