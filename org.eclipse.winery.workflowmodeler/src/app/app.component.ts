/**
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     ZTE - initial API and implementation and/or initial documentation
 */

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
