/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
const webpack = require('webpack');
const webpackMerge = require('webpack-merge');

const ExtractTextPlugin = require('extract-text-webpack-plugin');

const common = require('./webpack.common.js');
const TypedocWebpackPlugin = require('typedoc-webpack-plugin');
const ENV = process.env.ENV = process.env.NODE_ENV = 'production';

module.exports = webpackMerge(common, {

    devtool: 'source-map',

    htmlLoader: {
        // Workaround for Angular2
        minimize: false
    },

    /*
     * https://webpack.github.io/docs/list-of-plugins.html
     */
    plugins: [

        new ExtractTextPlugin('[name].[hash].css'),

        new webpack.NoErrorsPlugin(),
        new webpack.optimize.DedupePlugin(),
        new webpack.optimize.UglifyJsPlugin(),

        /*
         * https://github.com/Microsoft/Typedoc-Webpack-Plugin
         */
        new TypedocWebpackPlugin({
            "mode": "modules",
            "out": "doc",
            "theme": "default",
            "ignoreCompilerErrors": "true",
            "experimentalDecorators": "true",
            "emitDecoratorMetadata": "true",
            "target": "ES5",
            "moduleResolution": "node",
            "preserveConstEnums": "true",
            "stripInternal": "true",
            "suppressExcessPropertyErrors": "true",
            "suppressImplicitAnyIndexErrors": "true",
            "module": "commonjs"
        }),

        /*
         * https://webpack.github.io/docs/list-of-plugins.html#defineplugin
         */
        new webpack.DefinePlugin({
            'process.env': {
                'ENV': JSON.stringify(ENV),
                'NODE_ENV': JSON.stringify(ENV),
            }
        }),
    ],

});
