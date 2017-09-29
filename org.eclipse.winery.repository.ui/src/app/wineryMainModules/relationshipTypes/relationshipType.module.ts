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
import { RelationshipTypeRouterModule } from './relationshipTypeRouter.module';
import { CommonModule } from '@angular/common';
import { ValidSourcesAndTargetsComponent } from '../../instance/relationshipTypes/validSourcesAndTargets/validSourcesAndTargets.component';
import { SelectModule } from 'ng2-select';
import { WineryLoaderModule } from '../../wineryLoader/wineryLoader.module';
import { WineryReadmeModule } from '../../wineryReadmeModule/wineryReadme.module';
import { WineryLicenseModule } from '../../wineryLicenseModule/wineryLicense.module';

@NgModule({
    imports: [
        CommonModule,
        SelectModule,
        WineryLoaderModule,
        RelationshipTypeRouterModule,
        WineryReadmeModule,
        WineryLicenseModule
    ],
    declarations: [
        ValidSourcesAndTargetsComponent,
    ]
})
export class RelationshipTypeModule {

}
