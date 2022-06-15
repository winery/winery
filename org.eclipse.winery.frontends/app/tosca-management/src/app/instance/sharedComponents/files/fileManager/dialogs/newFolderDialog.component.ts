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
import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
    selector: 'winery-new-folder-dialog',
    templateUrl: 'newFolderDialog.component.html',
    styleUrls: ['fileManagerDialog.component.css']
})
export class NewFolderDialogComponent {

    folderName: string;

    constructor(public dialogRef: MatDialogRef<NewFolderDialogComponent>) {
    }

    forbiddenChar(name: string) {
        const regexp = new RegExp(/[/\\?%*:|"<>]/g);
        return regexp.test(name);
    }
}
