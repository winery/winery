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

const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');

const helpers = require('./helpers');

const path = require('path');

module.exports = {

    /*
     * http://webpack.github.io/docs/configuration.html#entry
     */
    entry: {
        'polyfills': './src/polyfills.ts',
        'vendor': './src/vendor.ts',
        'main': './src/main.ts'
    },

    /*
     * http://webpack.github.io/docs/configuration.html#output
     */
    output: {
        path: helpers.root('dist'),
        filename: '[name].bundle.js',
        sourceMapFilename: '[name].map',
        chunkFilename: '[id].chunk.js',
    },

    /*
     * http://webpack.github.io/docs/configuration.html#resolve
     */
    resolve: {
        // http://webpack.github.io/docs/configuration.html#resolve-extensions
        extensions: ['.ts', '.js', '.json', '.css', '.html'],
        modules: [
            path.join(__dirname, "src"),
            "node_modules",
        ]
    },

    module: {
        rules: [
            {
                test: /\.ts$/,
                loaders: ['awesome-typescript-loader', 'angular2-template-loader'],
            },
            {
                test: /\.html$/,
                loader: 'html-loader'},
            {
                test: /\.css$/,
                exclude: helpers.root('src', 'app'),
                // workaround for the css loader: https://github.com/webpack-contrib/css-loader/issues/296
                loader: ExtractTextPlugin.extract({ fallback: 'style-loader', loader: 'css-loader' })
            },
            {
                test: /\.css$/,
                include: helpers.root('src', 'app'),
                use: ['to-string-loader', 'css-loader', 'resolve-url-loader']
            },
            {
                test: /\.(png|jpe?g|gif|ico|ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
                loader: 'file-loader?name=assets/[name].[hash].[ext]'
            },
            {
                test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
                loader: 'url-loader?limit=8192&mimetype=application/font-woff&name=assets/[name].[hash].[ext]'
            },
        ],
    },

    /*
     * https://webpack.github.io/docs/list-of-plugins.html
     */
    plugins: [

        new webpack.NamedModulesPlugin(),
        /*
         * https://webpack.github.io/docs/list-of-plugins.html#commonschunkplugin
         */
        new webpack.optimize.CommonsChunkPlugin({
            name: ['main', 'vendor', 'polyfills'],
        }),

        /*
         * https://github.com/angular/angular/issues/11580
         */
        new webpack.ContextReplacementPlugin(
            /angular(\\|\/)core(\\|\/)@angular/,
            path.resolve(__dirname, '../src')
        ),

        /*
         * https://www.npmjs.com/package/copy-webpack-plugin
         */
        new CopyWebpackPlugin([
            {from: 'src/static'},
        ]),

        /*
         * https://github.com/ampedandwired/html-webpack-plugin
         */
        new HtmlWebpackPlugin({
            template: 'src/index.html',
        }),

    ],

    externals: [
        require("webpack-require-http"),
    ]
};
