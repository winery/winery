import { NgModule, ModuleWithProviders } from '@angular/core';

import { NotificationService } from './notificationservice';
import { DatePipe } from '@angular/common';

@NgModule({
    imports: [],
    exports: [],
    declarations: [],
    providers: [DatePipe],
})
export class NotificationModule {
    static forRoot(): ModuleWithProviders {
        return {
            ngModule: NotificationModule,
            providers: [NotificationService]
        };
    }
}

