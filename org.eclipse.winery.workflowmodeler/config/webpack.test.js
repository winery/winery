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

var helpers = require("./helpers");

module.exports = {
    devtool: "inline-source-map",

    resolve: {
        extensions: ["", ".ts", ".js"]
    },

    module: {
        loaders: [
            {
                test: /\.ts$/,
                use: ["awesome-typescript-loader", "angular2-template-loader"]
            },
            {
                test: /\.html$/,
                use: "html-loader"

            },
            {
                test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
                use: "null-loader"
            },
            {
                test: /\.css$/,
                exclude: helpers.root("src", "app"),
                loader: "null-loader"
            },
            {
                test: /\.css$/,
                include: helpers.root("src", "app"),
                loader: "raw-loader"
            }
        ]
    }
};
