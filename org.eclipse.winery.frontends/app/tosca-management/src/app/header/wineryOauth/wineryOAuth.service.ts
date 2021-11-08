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
import { Injectable, OnInit } from '@angular/core';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';
import { Utils } from '../../wineryUtils/utils';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { backendBaseURL } from '../../configuration';
import { Observable } from 'rxjs';
import { Subscriber } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { LoginData, StorageElements, Token, User } from './oAuthInterfaces';
import { WineryRepositoryConfigurationService } from '../../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';

/**
 * This service provides OAuth login service. If the credentials are not set, it defaults
 * to login with GitHub.
 */
@Injectable()
export class WineryOAuthService implements OnInit {

    public loginUrl = 'https://github.com/login/oauth/authorize';
    public redirectUri = '';
    public scope = 'repo';
    public alwaysUseCurrentUrlAsRedirect = true;

    private storage: Storage = localStorage;
    private observer: Subscriber<LoginData>;

    constructor(private http: HttpClient, private activatedRoute: ActivatedRoute, private router: Router,
                private notify: WineryNotificationService, private config: WineryRepositoryConfigurationService) {

    }

    ngOnInit(): void {
    }

    /**
     * Tries to parse the code and the state from the url parameters and continues
     * the login process in order to get the <code>accessToken</code>.
     */
    tryGetAccessToken(): Observable<LoginData> {
        return new Observable(observer => {
            this.observer = observer;

            if (this.config.configuration.git.accessToken) {
                this.getUserInformation();
            } else if (!this.storage.getItem(StorageElements.state)) {
                observer.next({ success: false });
                observer.complete();
            } else {
                this.activatedRoute.queryParams
                    .subscribe((params: Params) => this.parseParamsAndGetToken(params));
            }
        });
    }

    /**
     * Initiates the login with the provider:
     *  - user needs to login at provider
     *  - user needs to approve the requested scopes
     */
    login() {
        this.storage.removeItem(StorageElements.state);
        this.storage.removeItem(StorageElements.userName);
        this.storage.setItem(StorageElements.state, Utils.generateRandomString());

        if (this.alwaysUseCurrentUrlAsRedirect) {
            this.redirectUri = location.href;
        }

        location.href = this.loginUrl
            + '?client_id=' + encodeURIComponent(this.config.configuration.git.clientId)
            + '&state=' + encodeURIComponent(this.storage.getItem(StorageElements.state))
            + '&redirect_uri=' + encodeURIComponent(this.redirectUri)
            + '&scope=' + encodeURIComponent(this.scope);
    }

    /**
     * Logs the user out by deleting all saved key values paris in the local storage.
     */
    logout() {
        this.http
            .post(
                backendBaseURL + '/admin/githublogout', {},
            )
            .subscribe(
                () => this.handleLogout(),
                (error: HttpErrorResponse) => this.handleError(error)
            );
    }

    getUserInformation() {
        if (!this.config.configuration.git.accessToken) {
            this.observer.next({ success: false });
            this.observer.complete();
            return;
        }
        if (this.storage.getItem(StorageElements.userName)) {
            this.observer.next({ success: true, userName: this.storage.getItem(StorageElements.userName) });
            this.observer.complete();
            return;
        }

        const headers = new HttpHeaders({
            'Accept': 'application/json', 'Authorization': 'token ' + this.config.configuration.git.accessToken
        });

        this.http.get('https://api.github.com/user', { headers })
            .subscribe(
                (data: User) => this.handleUserInformation(data),
                (error: HttpErrorResponse) => this.handleError(error)
            );
    }

    private parseParamsAndGetToken(params: Params) {
        if (params['code'] && params['state']) {
            if (params['state'] === this.storage.getItem(StorageElements.state)) {
                const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
                const payload = {
                    code: params['code'],
                    state: params['state']
                };

                this.http
                    .post<Token>(
                        backendBaseURL + '/admin/githubaccesstoken',
                        payload,
                        { headers: headers }
                    )
                    .subscribe(
                        (data: Token) => this.processAccessToken(data),
                        (error: HttpErrorResponse) => this.handleError(error)
                    );
            } else {
                this.observer.error(false);
                this.notify.error('States are different! Login could not be completed!');
            }
        }
    }

    private processAccessToken(token: Token) {
        this.config.configuration.git.accessToken = token.access_token;
        this.config.configuration.git.tokenType = token.access_token;
        this.getUserInformation();
    }

    private handleUserInformation(data: User) {
        this.storage.setItem(StorageElements.userName, data.name);
        this.observer.next({ success: true, userName: data.name });
        this.observer.complete();
        this.router.navigate([], {
            queryParams: { code: null, state: null }, queryParamsHandling: 'merge', replaceUrl: true
        });
    }

    private handleLogout() {
        this.storage.removeItem(StorageElements.state);
        this.storage.removeItem(StorageElements.userName);
        this.observer.complete();
        this.notify.success('Logout Successful!');
    }

    private handleError(error: HttpErrorResponse) {
        this.observer.error(`An error happened in the communication with GitHub. Status: ${error.status}`);
        this.observer.complete();
        this.notify.error(`Login failed! Status: ${error.status}`);
    }

    get accessToken(): string {
        return this.config.configuration.git.accessToken;
    }

}

