import { NgModule } from '@angular/core';
import { RepositoryComponent } from './repository.component';
import { WineryUploaderModule } from '../../fileUploaderModal/wineryUploader.module';
import { RepositoryService } from './repository.service';

@NgModule({
    imports: [WineryUploaderModule],
    exports: [RepositoryComponent],
    declarations: [RepositoryComponent],
    providers: [RepositoryService],
})
export class RepositoryModule {
}

