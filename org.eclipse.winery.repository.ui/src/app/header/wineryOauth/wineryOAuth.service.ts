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
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';
import { isNullOrUndefined } from 'util';
import { Utils } from '../../wineryUtils/utils';
import { ActivatedRoute, Params } from '@angular/router';
import { backendBaseURL } from '../../configuration';
import { Observable } from 'rxjs/Observable';
import { Subscriber } from 'rxjs/Subscriber';

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

    constructor(private http: Http, private activatedRoute: ActivatedRoute,
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

        const headers = new Headers();
        const options = new RequestOptions({ headers: headers });
        headers.set('Accept', 'application/json');

        this.http.get('https://api.github.com/user?access_token=' + this.storage.getItem(StorageElements.accessToken), options)
            .map(res => res.json())
            .subscribe(
                data => this.handleUserInformation(data),
                error => this.handleError(error)
            );
    }

    private parseParamsAndGetToken(params: Params) {
        if (!isNullOrUndefined(params['code']) && !isNullOrUndefined(params['state'])) {
            if (params['state'] === this.storage.getItem(StorageElements.state)) {
                const headers = new Headers();
                const options = new RequestOptions({ headers: headers });
                headers.set('Content-Type', 'application/json');

                const payload = {
                    code: params['code'],
                    state: params['state']
                };

                this.http.post(backendBaseURL + '/admin/githubaccesstoken', payload, options)
                    .map(res => res.json())
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

enum StorageElements {
    state = 'state',
    accessToken = 'accessToken',
    tokenType = 'tokenType',
    userName = 'name'
}

interface Token {
    access_token: string;
    token_type: string;
    scope: string;
}

export interface LoginData {
    success: boolean;
    userName?: string;
    message?: string;
}
