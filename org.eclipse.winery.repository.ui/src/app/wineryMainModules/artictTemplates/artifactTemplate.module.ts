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
import { ArtifactTemplateRouterModule } from './artifactTemplateRouter.module';
import { FilesComponent } from '../../instance/artifactTemplates/filesTag/files.component';
import { PropertiesComponent } from '../../instance/sharedComponents/properties/properties.component';
import { WineryModalModule } from '../../wineryModalModule/winery.modal.module';
import { WineryTableModule } from '../../wineryTableModule/wineryTable.module';
import { WineryUploaderModule } from '../../wineryUploader/wineryUploader.module';
import { CommonModule } from '@angular/common';
import { WineryLoaderModule } from '../../wineryLoader/wineryLoader.module';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { ArtifactSourceComponent } from '../../instance/artifactTemplates/artifactSource/artifactSource.component';
import { TabsModule } from 'ngx-bootstrap';
import { WineryEditorModule } from '../../wineryEditorModule/wineryEditor.module';
import { WineryDuplicateValidatorModule } from '../../wineryValidators/wineryDuplicateValidator.module';
import { ReadmeComponent } from '../../instance/artifactTemplates/readme/readme.component';
import { WineryMarkdownModule } from '../../wineryMarkdownComponent/wineryMarkdown.module';

@NgModule({
    imports: [
        HttpModule,
        CommonModule,
        FormsModule,
        WineryLoaderModule,
        WineryModalModule,
        WineryTableModule,
        WineryUploaderModule,
        TabsModule,
        WineryEditorModule,
        WineryDuplicateValidatorModule,
        ArtifactTemplateRouterModule,
        WineryMarkdownModule
    ],
    declarations: [
        FilesComponent,
        ArtifactSourceComponent,
        ReadmeComponent,
        PropertiesComponent,
    ]
})
export class ArtifactTemplateModule {
}
