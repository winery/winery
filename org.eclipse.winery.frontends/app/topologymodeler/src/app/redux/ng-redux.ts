/********************************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * AI Disclosure: This file was largely AI-generated. The AI-generated
 * portions are made available under CC0-1.0 and not subject to the
 * project's licence. The human contributor has reviewed and verified
 * that the code is correct.
 *
 * SPDX-License-Identifier: (EPL-2.0 OR Apache-2.0) AND CC0-1.0
 * Assisted-by: Anthropic claude-opus-5-5
 ********************************************************************************/

import { Injectable, NgModule, NgZone } from '@angular/core';
import { AnyAction, applyMiddleware, compose, createStore, Middleware, Reducer, Store, StoreEnhancer, Unsubscribe } from 'redux';
import { BehaviorSubject, Observable } from 'rxjs';
import { distinctUntilChanged, map } from 'rxjs/operators';

/**
 * Minimal replacement for the parts of @angular-redux/store used by the topology modeler.
 */
@Injectable({ providedIn: 'root' })
export class NgRedux<T> {
    private store: Store<T>;
    private state$: BehaviorSubject<T>;

    constructor(private zone: NgZone) {
    }

    configureStore(reducer: Reducer<T>, initState: T, middleware: Middleware[] = [], enhancers: StoreEnhancer[] = []): void {
        const enhancer: StoreEnhancer = compose(applyMiddleware(...middleware), ...enhancers);
        this.store = createStore(reducer as Reducer<any>, initState, enhancer);
        this.state$ = new BehaviorSubject(this.store.getState());
        this.store.subscribe(() => this.state$.next(this.store.getState()));
    }

    dispatch = <A extends AnyAction>(action: A): A => this.zone.run(() => this.store.dispatch(action));

    getState = (): T => this.store.getState();

    subscribe = (listener: () => void): Unsubscribe => this.store.subscribe(listener);

    select<S>(selector: (state: T) => S): Observable<S> {
        return this.state$.pipe(map(selector), distinctUntilChanged());
    }
}

// ponytail: time-travel state changes from the extension run outside NgZone and do not trigger change detection.
@Injectable({ providedIn: 'root' })
export class DevToolsExtension {
    isEnabled = (): boolean => !!(window as any).__REDUX_DEVTOOLS_EXTENSION__;

    enhancer = (): StoreEnhancer => (window as any).__REDUX_DEVTOOLS_EXTENSION__();
}

@NgModule()
export class NgReduxModule {
}
