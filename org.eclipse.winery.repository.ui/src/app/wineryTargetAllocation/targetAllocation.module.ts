/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 ********************************************************************************/

import { NgModule } from '@angular/core';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { TargetAllocationComponent } from './targetAllocation.component';
import { SelectModule } from 'ng2-select';
import { FormsModule } from '@angular/forms';
import { PolicySelectionModule } from './policySelection/policySelection.module';
import { CommonModule } from '@angular/common';
import { WineryTableModule } from '../wineryTableModule/wineryTable.module';
import { TargetAllocationService } from './targetAllocation.service';

@NgModule({
    imports: [
        WineryModalModule,
        SelectModule,
        FormsModule,
        PolicySelectionModule,
        CommonModule,
        WineryTableModule
    ],
    exports: [TargetAllocationComponent],
    declarations: [TargetAllocationComponent],
    providers: [TargetAllocationService]
})
export class TargetAllocationModule {
}
