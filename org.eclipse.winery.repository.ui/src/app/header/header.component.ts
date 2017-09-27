/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { ModalDirective } from 'ngx-bootstrap';
import { ToscaTypes } from '../wineryInterfaces/enums';
import { Utils } from '../wineryUtils/utils';

@Component({
    selector: 'winery-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.style.css']
})
export class HeaderComponent implements OnInit {

    selectedOtherComponent = '';
    otherActive = false;
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
            if (others.length > 0 &&
                !(others.includes(ToscaTypes.ServiceTemplate) || others.includes(ToscaTypes.NodeType) ||
                    others.includes(ToscaTypes.RelationshipType) || others.includes('other') ||
                    others.includes('admin'))
            ) {
                this.otherActive = true;
                this.selectedOtherComponent = ': ' + Utils.getToscaTypeNameFromToscaType(Utils.getToscaTypeFromString(others), true);
            } else {
                this.otherActive = others.includes('other');
                this.selectedOtherComponent = '';
            }
        });
    }
}
