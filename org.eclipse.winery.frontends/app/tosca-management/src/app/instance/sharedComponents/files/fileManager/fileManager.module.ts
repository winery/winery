/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
 *******************************************************************************/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NewFolderDialogComponent } from './dialogs/newFolderDialog.component';
import { RenameDialogComponent } from './dialogs/renameDialog.component';
import { FileManagerComponent } from './fileManager.component';
import { ConfirmDialogComponent } from './dialogs/confirmDialog.component';
import {
    MatFormFieldModule, MatSortModule, MatTableModule, MatDividerModule, MatTooltipModule
} from '@angular/material';


@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        MatToolbarModule,
        MatIconModule,
        MatGridListModule,
        MatMenuModule,
        MatDialogModule,
        MatInputModule,
        MatButtonModule,
        MatTableModule,
        MatIconModule,
        MatSortModule,
        MatFormFieldModule,
        MatDividerModule,
        MatTooltipModule
    ],
    declarations: [
        FileManagerComponent,
        NewFolderDialogComponent,
        RenameDialogComponent,
        ConfirmDialogComponent
    ],
    exports: [
        FileManagerComponent
    ],
    entryComponents: [
        NewFolderDialogComponent,
        RenameDialogComponent,
        ConfirmDialogComponent
    ]
})
export class FileManagerModule {
}
