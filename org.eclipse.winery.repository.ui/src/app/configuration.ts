/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */

// when running in development mode on port 4200, use default port 8080
// otherwise, assume that backend runs on the some port
export const hostURL = location.protocol + '//' + location.hostname + ':' + (location.port === '4200' ? '8080' : location.port);
export const backendBaseURL = hostURL + '/winery';
// when running in development mode, use the workflow modelers development port
// it also is not running on /winery-workflowmodeler in dev-mode.
export const workflowModelerURL = location.protocol + '//' + location.hostname + ':' + (location.port === '4200' ? '9527' : location.port
    + '/winery-workflowmodeler');
