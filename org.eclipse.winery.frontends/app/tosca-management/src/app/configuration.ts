/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
const wineryContext = '/winery';
// when running in development mode on port 4200, use default port 8080
// otherwise, assume that backend runs on the same port#
export const hostURL = location.protocol + '//' + location.hostname + ':' + (location.port === '4200' ? '8080' : location.port);
export const editorURL = location.protocol + '//' + location.hostname + ':' + (location.port === '4200' ? '4201' : location.port + '/winery-topologymodeler');
export const backendBaseURL = hostURL + wineryContext;
export const webSocketURL = 'ws://' + location.hostname + ':' + (location.port === '4200' ? '8080' : location.port) + wineryContext;

const positionOfFirstColon = backendBaseURL.indexOf(':');
const positionOfSecondColon = backendBaseURL.indexOf(':', positionOfFirstColon + 1);
  // example: http://localhost:4242
  // positionOfFirstColon:4, positionOfSecondColon:16
  // same as the following?
  // export const modelerURL = location.protocol + '//' + location.hostname + ':' + '4242';
export const modelerURL = backendBaseURL.substring(0, positionOfSecondColon + 1) + '4242';
