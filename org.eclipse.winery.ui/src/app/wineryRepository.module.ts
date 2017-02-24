/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 *******************************************************************************/

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { EntityContainerComponent } from './entityContainer/entityContainer.component';
import { SectionComponent } from './section/section.component';
import { HeaderComponent } from './header/header.component';
import { OtherComponent } from './other/other.component';
import { WineryRepositoryComponent } from './wineryRepository.component';
import { UrlEncodePipe } from './pipes/urlEncode.pipe';
import { UrlDecodePipe } from './pipes/urlDecode.pipe';
import { WineryRepositoryRoutingModule } from './wineryRepositoryRouting.module';
import { InstanceModule } from './instance/instance.module';
import { NotFoundComponent } from './404/notFound.component';
import { LoaderComponent } from './loader/loader.component';
import { LoaderModule } from './loader/loader.module';
import { QNameSelectorComponent } from './qNameSelector/qNameSelector.component';

@NgModule({
    imports: [
        BrowserModule,
        InstanceModule,
        LoaderModule,
        WineryRepositoryRoutingModule,
    ],
    declarations: [
        EntityContainerComponent,
        HeaderComponent,
        NotFoundComponent,
        OtherComponent,
        SectionComponent,
        WineryRepositoryComponent,
        UrlDecodePipe,
        UrlEncodePipe,
    ],
    bootstrap: [WineryRepositoryComponent]
})
export class WineryRepositoryModule {
}
