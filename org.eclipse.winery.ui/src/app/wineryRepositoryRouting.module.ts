import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InstanceComponent } from './instance/instance.component';
import { OtherComponent } from './other/other.component';
import { SectionComponent } from './section/section.component';
import { SectionResolver } from './resolver/section.resolver';
import { NotFoundComponent } from './404/notFound.component';
import { NamespaceResolver } from './resolver/namespace.resolver';
import { HttpModule } from '@angular/http';

const appRoutes: Routes = [
    { path: 'admin', component: InstanceComponent },
    { path: 'other', component: OtherComponent },
    { path: 'notfound', component: NotFoundComponent },
    { path: ':section', component: SectionComponent, resolve: { resolveData: SectionResolver } },
    { path: ':section/:namespace', component: SectionComponent, resolve: { resolveData: NamespaceResolver } },
    { path: '', redirectTo: '/servicetemplates', pathMatch: 'full' },
    { path: '**', component: NotFoundComponent },
    // TODO: add namespaces, other routes available in other, etc...
];

@NgModule({
    imports: [
        BrowserModule,
        HttpModule,
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
