/**
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     ZTE - initial API and implementation and/or initial documentation
 */

Error.stackTraceLimit = Infinity;

require("core-js/es6");
require("reflect-metadata");

require("zone.js/dist/zone");
require("zone.js/dist/long-stack-trace-zone");
require("zone.js/dist/proxy");
require("zone.js/dist/sync-test");
require("zone.js/dist/jasmine-patch");
require("zone.js/dist/async-test");
require("zone.js/dist/fake-async-test");

var appContext = require.context("../src", true, /\.spec\.ts/);

appContext.keys().forEach(appContext);

var testing = require("@angular/core/testing");
var browser = require("@angular/platform-browser-dynamic/testing");

testing.TestBed.initTestEnvironment(browser.BrowserDynamicTestingModule, browser.platformBrowserDynamicTesting());
