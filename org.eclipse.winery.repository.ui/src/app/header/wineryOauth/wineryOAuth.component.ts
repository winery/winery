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
import { Component, OnInit } from '@angular/core';
import { LoginData, WineryOAuthService } from './wineryOAuth.service';

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
