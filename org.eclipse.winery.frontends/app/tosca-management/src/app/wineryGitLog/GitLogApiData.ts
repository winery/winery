/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

export class GitData {
    pull: boolean;
    push: boolean;
    reset: boolean;
    refresh: boolean;
    commit: boolean;
    branches: boolean;
    checkout: string;
    commitMessage: string;
    itemsToCommit: string[];
    repository: string;
}

export class Repos {
    name: string;
    changes: GitChange[];
}

export class GitResponseData {
    error: string;
    success: string;
    repos: Repos[];
    branches: String[];
    resetSuccess: boolean;
    lfsAvailable: boolean;
}

export class GitChange {
    name: string;
    type: string;
    path: string;
    diffs: string;
}
