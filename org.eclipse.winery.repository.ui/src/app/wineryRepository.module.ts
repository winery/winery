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
 *     Niko Stadelmaier - add notifications module
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { ToastModule, ToastOptions } from 'ng2-toastr/ng2-toastr';
import { NotFoundComponent } from './404/notFound.component';
import { HeaderComponent } from './header/header.component';
import { InstanceModule } from './instance/instance.module';
import { WineryLoaderModule } from './wineryLoader/wineryLoader.module';
import { WineryNotificationModule } from './wineryNotificationModule/wineryNotification.module';
import { WineryCustomOption } from './wineryNotificationModule/wineryNotificationOptions';
import { OtherComponent } from './other/other.component';
import { SectionModule } from './section/section.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { WineryModalModule } from './wineryModalModule/winery.modal.module';
import { WineryRepositoryComponent } from './wineryRepository.component';
import { WineryRepositoryRoutingModule } from './wineryRepositoryRouting.module';
import { ExistService } from './wineryUtils/existService';
import { WineryOAuthComponent } from './header/wineryOauth/wineryOAuth.component';
import { TooltipModule } from 'ngx-bootstrap';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        CommonModule,
        BrowserAnimationsModule,
        InstanceModule,
        WineryLoaderModule,
        WineryModalModule,
        ToastModule.forRoot(),
        WineryNotificationModule.forRoot(),
        SectionModule,
        WineryModalModule,
        WineryRepositoryRoutingModule,
        TooltipModule.forRoot(),
    ],
    declarations: [
        HeaderComponent,
        NotFoundComponent,
        OtherComponent,
        WineryRepositoryComponent,
        WineryOAuthComponent,
    ],
    providers: [
        {provide: ToastOptions, useClass: WineryCustomOption},
        ExistService
    ],
    bootstrap: [WineryRepositoryComponent]
})
export class WineryRepositoryModule {
}
