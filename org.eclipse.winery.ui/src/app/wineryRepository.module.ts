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

@NgModule({
    imports: [
        BrowserModule,
        InstanceModule,
        LoaderModule,
        WineryRepositoryRoutingModule,
    ],
    declarations: [
        EntityContainerComponent,
        SectionComponent,
        HeaderComponent,
        NotFoundComponent,
        OtherComponent,
        WineryRepositoryComponent,
        UrlDecodePipe,
        UrlEncodePipe,
    ],
    bootstrap: [WineryRepositoryComponent]
})
export class WineryRepositoryModule {
}
