import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import 'ng2-toastr/ng2-toastr.css';

enableProdMode();
platformBrowserDynamic().bootstrapModule(AppModule);
