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
import { ActivatedRoute } from '@angular/router';

import { PageParameter } from './model/page-parameter';
import { WineryService } from './services/winery.service';

/**
 * main component
 */
@Component({
    selector: 'b4t-app',
    templateUrl: 'app.component.html',
})
export class AppComponent implements OnInit {
    constructor(private route: ActivatedRoute,
                private wineryService: WineryService) {

    }

    public ngOnInit() {
        this.route.queryParams.subscribe(params => this.wineryService.setRequestParam(<PageParameter>params));
    }
}
