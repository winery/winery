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

var restify = require("restify");
var partition = require("./partition/index");

var server = restify.createServer();
server.use(restify.queryParser());
server.use(restify.requestLogger());
server.use(restify.bodyParser());
server.listen(8889, function () {
    console.log("Mock Server is Started at 8889!!!");
});
partition(server);
