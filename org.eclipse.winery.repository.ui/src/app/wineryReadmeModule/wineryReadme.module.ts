/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 */
import { NgModule } from '@angular/core';
import { WineryReadmeComponent } from './wineryReadme.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WineryLoaderModule } from '../wineryLoader/wineryLoader.module';
import { WineryMarkdownModule } from '../wineryMarkdownComponent/wineryMarkdown.module';
import { TabsModule } from 'ngx-bootstrap';
import { WineryPipesModule } from '../wineryPipes/wineryPipes.module';

@NgModule({
    imports: [
        CommonModule,
        WineryLoaderModule,
        FormsModule,
        WineryMarkdownModule,
        TabsModule,
        WineryPipesModule
    ],
    exports: [
        WineryReadmeComponent
    ],
    declarations: [WineryReadmeComponent],
    providers: [],
})
export class WineryReadmeModule {
}
