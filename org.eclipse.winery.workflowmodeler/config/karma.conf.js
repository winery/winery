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

var webpackConfig = require("./webpack.test");

module.exports = function (config) {
    var _config = {
        basePath: "",

        frameworks: ["jasmine"],

        files: [
            {pattern: "./config/karma-test-shim.js", watched: false}
        ],

        preprocessors: {
            "./config/karma-test-shim.js": ["webpack", "sourcemap"]
        },

        webpack: webpackConfig,

        webpackMiddleware: {
            stats: "errors-only"
        },

        webpackServer: {
            noInfo: true
        },

        reporters: ["progress"],
        port: 9876,
        colors: true,
        logLevel: config.LOG_INFO,
        autoWatch: false,
        browsers: ["PhantomJS"],
        singleRun: true
    };

    config.set(_config);
};
