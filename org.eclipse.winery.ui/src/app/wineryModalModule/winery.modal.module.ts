import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ModalModule } from 'ng2-bootstrap';
import { WineryModalBodyComponent } from './winery.modal.body.component';
import { WineryModalComponent } from './winery.modal.component';
import { WineryModalFooterComponent } from './winery.modal.footer.component';
import { WineryModalHeaderComponent } from './winery.modal.header.component';

/**
 * This module must be imported in order to use the {@link WineryModalComponent}. Documentation on how to use
 * this component can also be found at the {@link WineryModalComponent}.
 */
@NgModule({
    imports: [
        CommonModule,
        BrowserModule,
        ModalModule.forRoot(),
    ],
    exports: [
        WineryModalComponent,
        WineryModalBodyComponent,
        WineryModalHeaderComponent,
        WineryModalFooterComponent,
        ModalModule
    ],
    declarations: [
        WineryModalComponent,
        WineryModalBodyComponent,
        WineryModalHeaderComponent,
        WineryModalFooterComponent,
    ],
    providers: [],
})
export class WineryModalModule {
}
