import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { InstanceComponent } from './instance/instance.component';
import { OtherComponent } from './other/other.component';
import { SectionComponent } from './section/section.component';
import { SectionResolver } from './section/sectionResolver';
import { NotFoundComponent } from './404/notFound.component';

const appRoutes: Routes = [
    { path: 'admin', component: InstanceComponent },
    { path: 'other', component: OtherComponent },
    { path: ':section', component: SectionComponent },
    { path: ':section/:namespace', component: SectionComponent },
    { path: '', redirectTo: '/servicetemplates', pathMatch: 'full' },
    { path: 'notfound', component: NotFoundComponent },
    { path: '**', component: NotFoundComponent },
    // TODO: add namespaces, other routes available in other, etc...
];

@NgModule({
    imports: [
        BrowserModule,
        RouterModule.forRoot(appRoutes),
    ],
    exports: [
        RouterModule
    ],
    providers: [
        SectionResolver
    ]
})
export class WineryRepositoryRoutingModule {
}
