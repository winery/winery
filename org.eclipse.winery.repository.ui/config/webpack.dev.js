/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Michael Wurster - initial API and implementation
 *    Lukas Harzenetter - support debugging and dots in the url
 */
const webpack = require('webpack');
const webpackMerge = require('webpack-merge');

const ExtractTextPlugin = require('extract-text-webpack-plugin');

const common = require('./webpack.common.js');
const helpers = require('./helpers');

const ENV = process.env.ENV = process.env.NODE_ENV = 'development';

module.exports = webpackMerge(common, {

    // http://webpack.github.io/docs/configuration.html#devtool
    devtool: 'eval-source-map',

    /*
     * https://webpack.github.io/docs/list-of-plugins.html
     */
    plugins: [
        /*
         * https://webpack.js.org/plugins/loader-options-plugin
         */
        new webpack.LoaderOptionsPlugin({
            debug: true
        }),

        new ExtractTextPlugin('[name].css'),

        /*
         * https://webpack.github.io/docs/list-of-plugins.html#defineplugin
         */
        new webpack.DefinePlugin({
            'process.env': {
                'ENV': JSON.stringify(ENV),
                'NODE_ENV': JSON.stringify(ENV)
            }
        })
    ],

    /*
     * https://webpack.js.org/configuration/dev-server
     */
    devServer: {
        port: 3000,
        historyApiFallback: {
            index: '/',
            // rewrite rule in order to support dots in the url
            rewrites: [
                { from: /[\/]+.*.\%.*.[.].*/, to: '/' }
            ]
        },
        watchOptions: {aggregateTimeout: 300, poll: 1000},
        contentBase: helpers.root('dist'),
        publicPath: '/'
    }

});
