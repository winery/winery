import { NgModule, ModuleWithProviders } from '@angular/core';

import { NotificationService } from './notificationservice';

@NgModule({
    imports: [],
    exports: [],
    declarations: [],
    providers: [],
})
export class NotificationModule {
    static forRoot(): ModuleWithProviders {
        return {
            ngModule: NotificationModule,
            providers: [NotificationService]
        };
    }
}

