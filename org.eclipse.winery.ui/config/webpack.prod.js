const webpack = require('webpack');
const webpackMerge = require('webpack-merge');

const ExtractTextPlugin = require('extract-text-webpack-plugin');

const common = require('./webpack.common.js');

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
        new TypedocWebpackPlugin({}),

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
