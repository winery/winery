/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { ToastModule, ToastOptions } from 'ng2-toastr/ng2-toastr';
import { NotFoundComponent } from './404/notFound.component';
import { HeaderComponent } from './header/header.component';
import { WineryLoaderModule } from './wineryLoader/wineryLoader.module';
import { WineryNotificationModule } from './wineryNotificationModule/wineryNotification.module';
import { WineryCustomOption } from './wineryNotificationModule/wineryNotificationOptions';
import { OtherComponent } from './other/other.component';
import { SectionModule } from './section/section.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { WineryRepositoryComponent } from './wineryRepository.component';
import { WineryRepositoryRoutingModule } from './wineryRepositoryRouting.module';
import { ExistService } from './wineryUtils/existService';
import { WineryOAuthComponent } from './header/wineryOauth/wineryOAuth.component';
import { TooltipModule } from 'ngx-bootstrap';
import { NodeTypeModule } from './wineryMainModules/nodeTypes/nodeType.module';
import { ServiceTemplateModule } from './wineryMainModules/serviceTemplates/serviceTemplate.module';
import { RelationshipTypeModule } from './wineryMainModules/relationshipTypes/relationshipType.module';
import { WineryModalModule } from './wineryModalModule/winery.modal.module';
import { ArtifactTypeModule } from './wineryMainModules/artifactTypes/artifactType.module';
import { AdminModule } from './wineryMainModules/admin/admin.module';
import { PolicyTypeModule } from './wineryMainModules/policyTypes/policyType.module';
import { RequirementTypeModule } from './wineryMainModules/requirementTypes/requirementType.module';
import { ArtifactTemplateModule } from './wineryMainModules/artictTemplates/artifactTemplate.module';
import { CapabilityTypeModule } from './wineryMainModules/capabilityTypes/capabilityType.module';
import { NodeTypeImplementationModule } from './wineryMainModules/nodeTypeImplementations/nodeTypeImplementation.module';
import { RelationshipTypeImplementationModule } from './wineryMainModules/relationshipTypeImplementations/relationshipTypeImplementation.module';
import { PolicyTemplateModule } from './wineryMainModules/policyTemplates/policyTemplate.module';
import { ImportModule } from './wineryMainModules/imports/imports.module';
import { WineryGitLogComponent } from './wineryGitLog/wineryGitLog.component';
import { TemplatesOfTypeModule } from './instance/sharedComponents/templatesOfTypes/templatesOfType.Module';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        CommonModule,
        BrowserAnimationsModule,
        WineryLoaderModule,
        ToastModule.forRoot(),
        WineryNotificationModule.forRoot(),
        SectionModule,
        WineryModalModule,
        TooltipModule.forRoot(),
        TemplatesOfTypeModule,
        ServiceTemplateModule,
        NodeTypeModule,
        RelationshipTypeModule,
        ArtifactTypeModule,
        AdminModule,
        PolicyTypeModule,
        RequirementTypeModule,
        ArtifactTemplateModule,
        CapabilityTypeModule,
        NodeTypeImplementationModule,
        RelationshipTypeImplementationModule,
        PolicyTemplateModule,
        ImportModule,

        WineryRepositoryRoutingModule,
    ],
    declarations: [
        HeaderComponent,
        NotFoundComponent,
        OtherComponent,
        WineryRepositoryComponent,
        WineryOAuthComponent,
        WineryGitLogComponent
    ],
    providers: [
        { provide: ToastOptions, useClass: WineryCustomOption },
        ExistService
    ],
    bootstrap: [WineryRepositoryComponent]
})
export class WineryRepositoryModule {
}
