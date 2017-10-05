/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
export class ImplementationAPIData {
    namespace = '';
    localname = '';
    displayName: string;

    public constructor(namespace: string,
                       name: string) {
        this.namespace = namespace;
        this.localname = this.displayName = name;
    }
}
