/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { ListDefinedTypesAndElementsService } from './listDefinedTypesAndElements.service';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';
import { Router } from '@angular/router';
import { WineryTableColumn } from '../../wineryTableModule/wineryTable.component';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    templateUrl: 'listDefinedTypesAndElements.component.html',
    providers: [
        ListDefinedTypesAndElementsService
    ]
})

export class ListDefinedTypesAndElementsComponent implements OnInit {

    loading = true;
    elements: SingleColumn[];
    elementOrType: string;
    title: string;

    readonly columns: Array<WineryTableColumn> = [
        { title: 'Local Names', name: 'key' }
    ];

    constructor(private service: ListDefinedTypesAndElementsService, private notify: WineryNotificationService,
                private router: Router) {
    }

    ngOnInit() {
        this.service.getDeclarations()
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );

        const splitUrl = this.router.url.split('/');
        if (splitUrl[splitUrl.length - 1].includes('types')) {
            this.elementOrType = 'Defined Types';
        } else {
            this.elementOrType = 'Declared Elements';
        }
    }

    private handleData(data: string[]) {
        this.loading = false;
        this.elements = data.map(value => {
            return new SingleColumn(value);
        });

        if (this.elements.length === 0) {
            this.title = 'No ' + this.elementOrType + ' available!';
        } else {
            this.title = this.elementOrType;
        }
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message);
    }
}
