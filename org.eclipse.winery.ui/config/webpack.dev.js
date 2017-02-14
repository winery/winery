const webpack = require('webpack');
const webpackMerge = require('webpack-merge');

const ExtractTextPlugin = require('extract-text-webpack-plugin');

const common = require('./webpack.common.js');
const helpers = require('./helpers');

const ENV = process.env.ENV = process.env.NODE_ENV = 'development';

module.exports = webpackMerge(common, {

    debug: true,

    // http://webpack.github.io/docs/configuration.html#devtool
    devtool: 'cheap-module-eval-source-map',

    /*
     * https://webpack.github.io/docs/list-of-plugins.html
     */
    plugins: [

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
     * http://webpack.github.io/docs/configuration.html#devserver
     */
    devServer: {
        port: 3000,
        historyApiFallback: {
            index: '/',
            // rewrite rule in order to support dots in the url
            rewrites: [
                { from: /[\/]+.*[.].*[\/]/, to: '/' }
            ]
        },
        watchOptions: {aggregateTimeout: 300, poll: 1000},
        outputPath: helpers.root('dist'),
        publicPath: '/'
    }

});
