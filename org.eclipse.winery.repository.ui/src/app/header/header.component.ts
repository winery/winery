/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { sections } from '../configuration';
import { isNullOrUndefined } from 'util';
import { ModalDirective } from 'ngx-bootstrap';

@Component({
    selector: 'winery-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.style.css']
})
export class HeaderComponent implements OnInit {

    selectedOtherComponent = '';
    @ViewChild('aboutModal') aboutModal: ModalDirective;

    constructor(public router: Router) {
    }

    ngOnInit(): void {
        this.router.events.subscribe(data => {
            if (!(data instanceof NavigationEnd)) {
                return;
            }

            let others: string = data.url.slice(1);

            if (others.includes('/')) {
                others = others.split('/')[0];
            }

            if (!isNullOrUndefined(sections[others]) && !(
                    others.includes('servicetemplates') ||
                    others.includes('nodetypes') ||
                    others.includes('relationshiptypes') ||
                    others.includes('other') ||
                    others.includes('admin')
                )
            ) {
                this.selectedOtherComponent = ': '
                    + sections[others]
                        .replace(/([A-Z])/g, ' $1')
                        .replace(/^./, (str: string) => str.toUpperCase())
                    + 's';
            } else {
                this.selectedOtherComponent = '';
            }
        });
    }
}
