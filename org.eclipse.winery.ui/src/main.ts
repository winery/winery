import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { enableProdMode }         from '@angular/core';

import { WineryRepositoryModule } from './app/wineryRepository.module';

import './css/wineryCommon.css';
import './css/wineryRepository.css';
import './css/bootstrap.min.css';

if (process.env.ENV === 'production') {
    enableProdMode();
}

platformBrowserDynamic().bootstrapModule(WineryRepositoryModule);
