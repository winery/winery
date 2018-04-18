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
import { WineryOAuthService } from './wineryOAuth.service';
import { LoginData } from './oAuthInterfaces';

/**
 * This component adds support for a OAuth login. For now, it only allows to login using GitHub.
 * However, the main work is done by the {@link WineryOAuthService}.
 */
@Component({
    selector: 'winery-oauth',
    templateUrl: 'wineryOAuth.component.html',
    providers: [
        WineryOAuthService,
    ]
})
export class WineryOAuthComponent implements OnInit {

    buttonLabel = 'Login with GitHub';
    loggedIn = false;

    constructor(private service: WineryOAuthService) {
    }

    ngOnInit() {
        this.service.tryGetAccessToken()
            .subscribe(data => this.handleLogin(data));
    }

    onButtonClicked() {
        if (!this.loggedIn) {
            this.service.login();
        } else {
            this.service.logout();
            this.loggedIn = false;
            this.buttonLabel = 'Login with GitHub';
        }
    }

    private handleLogin(loginData: LoginData) {
        if (loginData.success) {
            this.loggedIn = true;
            this.buttonLabel = 'Logout as ' + loginData.userName;
        }
    }
}
