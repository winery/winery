/********************************************************************************
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
 ********************************************************************************/

// when running in development mode on port 4201, use default port 8080
// otherwise, assume that backend runs on the some port
export const hostURL = location.protocol + '//' + location.hostname + ':' + (location.port === '4201' || location.port === '4202' ? '8080' : location.port);
export const backendBaseURL = hostURL + '/winery';
// when running in development mode, use the workflow modelers development port
// it also is not running on /winery-workflowmodeler in dev-mode.
export const workflowModelerURL = location.protocol + '//' + location.hostname + ':' + (location.port === '4200' ? '9527' : location.port
    + '/winery-workflowmodeler');
