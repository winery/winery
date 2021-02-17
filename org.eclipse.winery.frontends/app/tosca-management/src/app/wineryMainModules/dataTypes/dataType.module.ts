/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
import { DataTypeRouterModule } from './dataTypeRouter.module';
import { CommonModule } from '@angular/common';
import { WineryLoaderModule } from '../../wineryLoader/wineryLoader.module';
import { SelectModule } from 'ng2-select';
import { WineryTableModule } from '../../wineryTableModule/wineryTable.module';
import { InheritanceModule } from '../../instance/sharedComponents/inheritance/inheritance.module';
import { WineryPipesModule } from '../../wineryPipes/wineryPipes.module';
import { AlertModule } from 'ngx-bootstrap';
import { PropertiesDefinitionModule } from '../../instance/sharedComponents/propertiesDefinition/propertiesDefinition.module';
import { YamlConstraintsComponent } from '../../instance/sharedComponents/yaml/constraints/yaml-constraints.component';

@NgModule({
    imports: [
        CommonModule,
        DataTypeRouterModule,
        WineryLoaderModule,
        SelectModule,
        WineryTableModule,
        InheritanceModule,
        WineryPipesModule,
        AlertModule,
        PropertiesDefinitionModule,
    ],
    declarations: [
        YamlConstraintsComponent,
    ],
    exports: [
        YamlConstraintsComponent,
    ]
})
export class DataTypeModule {
}
