import { ModuleWithProviders, NgModule } from '@angular/core';

import { NotificationService } from './notification.service';

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

