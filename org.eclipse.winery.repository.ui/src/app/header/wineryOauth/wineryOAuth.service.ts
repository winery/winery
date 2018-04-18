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
import { Injectable } from '@angular/core';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';
import { isNullOrUndefined } from 'util';
import { Utils } from '../../wineryUtils/utils';
import { ActivatedRoute, Params } from '@angular/router';
import { backendBaseURL } from '../../configuration';
import { Observable } from 'rxjs/Observable';
import { Subscriber } from 'rxjs/Subscriber';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { LoginData, StorageElements, Token } from './oAuthInterfaces';

/**
 * This service provides OAuth login service. If the credentials are not set, it defaults
 * to login with GitHub.
 */
@Injectable()
export class WineryOAuthService {

    public clientId = 'b106f7f4e3393fad0529';

    public loginUrl = 'https://github.com/login/oauth/authorize';
    public redirectUri = '';
    public scope = 'repo';
    public alwaysUseCurrentUrlAsRedirect = true;

    private storage: Storage = localStorage;
    private observer: Subscriber<LoginData>;

    constructor(private http: HttpClient, private activatedRoute: ActivatedRoute,
                private notify: WineryNotificationService) {
    }

    /**
     * Tries to parse the code and the state from the url parameters and continues
     * the login process in order to get the <code>accessToken</code>.
     */
    tryGetAccessToken(): Observable<LoginData> {
        return new Observable(observer => {
            this.observer = observer;

            if (!isNullOrUndefined(this.storage.getItem(StorageElements.accessToken))) {
                this.getUserInformation();
            } else if (isNullOrUndefined(this.storage.getItem(StorageElements.state))) {
                observer.next({ success: false });
                observer.complete();
            } else {
                const subscription = this.activatedRoute.queryParams
                    .subscribe(params => this.parseParamsAndGetToken(params));
            }
        });
    }

    /**
     * Initiates the login with the provider:
     *  - user needs to login at provider
     *  - user needs to approve the requested scopes
     */
    login() {
        this.logout();
        this.storage.setItem(StorageElements.state, Utils.generateRandomString());

        if (this.alwaysUseCurrentUrlAsRedirect) {
            this.redirectUri = location.origin + location.pathname;
        }

        location.href = this.loginUrl
            + '?client_id=' + encodeURIComponent(this.clientId)
            + '&state=' + encodeURIComponent(this.storage.getItem(StorageElements.state))
            + '&redirect_uri=' + encodeURIComponent(this.redirectUri)
            + '&scope=' + encodeURIComponent(this.scope);
    }

    /**
     * Logs the user out by deleting all saved key values paris in the local storage.
     */
    logout() {
        this.storage.removeItem(StorageElements.state);
        this.storage.removeItem(StorageElements.accessToken);
        this.storage.removeItem(StorageElements.tokenType);
        this.storage.removeItem(StorageElements.userName);
    }

    getUserInformation() {
        if (isNullOrUndefined(this.storage.getItem(StorageElements.accessToken))) {
            this.observer.next({ success: false });
            this.observer.complete();
            return;
        }

        const headers = new HttpHeaders({ 'Accept': 'application/json' });

        this.http.get('https://api.github.com/user?access_token=' + this.storage.getItem(StorageElements.accessToken))
            .subscribe(
                data => this.handleUserInformation(data),
                error => this.handleError(error)
            );
    }

    private parseParamsAndGetToken(params: Params) {
        if (!isNullOrUndefined(params['code']) && !isNullOrUndefined(params['state'])) {
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
                        data => this.processAccessToken(data),
                        error => this.handleError(error)
                    );
            } else {
                this.observer.error(false);
                this.notify.error('States are different! Login could not be completed!');
            }
        }
    }

    private processAccessToken(token: Token) {
        this.storage.setItem(StorageElements.accessToken, token.access_token);
        this.storage.setItem(StorageElements.tokenType, token.token_type);

        this.getUserInformation();
    }

    private handleUserInformation(data: any) {
        this.storage.setItem(StorageElements.userName, data.name);

        this.observer.next({ success: true, userName: data.name });
        this.observer.complete();
    }

    private handleError(error: any) {
        this.observer.error('An error happened in the communication with GitHub.');
        this.observer.complete();
        this.notify.error('Login failed!');
        this.logout();
    }

    get accessToken(): string {
        return this.storage.getItem(StorageElements.accessToken);
    }

}

