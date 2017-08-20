/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Yannic Sowoidnich - initial API and implementation
 */
import { ModuleWithProviders, NgModule } from '@angular/core';
import { WineryAlertService } from './winery-alert.service';
import { DatePipe } from '@angular/common';

@NgModule({
  providers: [DatePipe],
})
export class WineryAlertModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: WineryAlertModule,
      providers: [WineryAlertService]
    };
  }
}

