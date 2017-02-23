/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 *******************************************************************************/

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
