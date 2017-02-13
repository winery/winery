const webpack = require('webpack');

const CommonsChunkPlugin = webpack.optimize.CommonsChunkPlugin;

const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');

const TypedocWebpackPlugin = require('typedoc-webpack-plugin');

const helpers = require('./helpers');

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
        extensions: ['', '.ts', '.js', '.json', '.css', '.scss', '.html'],
        // Make sure root is src
        root: helpers.root('src'),
    },

    module: {

        preLoaders: [],

        /*
         * http://webpack.github.io/docs/configuration.html#module-loaders
         * http://webpack.github.io/docs/list-of-loaders.html
         */
        loaders: [
            {
                test: /\.ts$/,
                loaders: ['ts', 'angular2-template-loader'],
            },
            {test: /\.html$/, loader: 'html'},
            {
                test: /\.css$/,
                exclude: helpers.root('src', 'app'),
                loader: ExtractTextPlugin.extract(['css?sourceMap', 'postcss'])
            },
            {
                test: /\.css$/,
                include: helpers.root('src', 'app'),
                loaders: ['raw', 'postcss']
            },
            {
                test: /\.scss$/,
                exclude: helpers.root('src', 'app'),
                loader: ExtractTextPlugin.extract(['css?sourceMap', 'postcss', 'resolve-url', 'sass'])
            },
            {
                test: /\.scss$/,
                include: helpers.root('src', 'app'),
                loaders: ['raw', 'postcss', 'resolve-url', 'sass']
            },
            {test: /\.json$/, loader: 'json'},
            {
                test: /\.(png|jpe?g|gif|ico|ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
                loader: 'file?name=assets/[name].[hash].[ext]'
            },
            {
                test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
                loader: 'url?limit=8192&mimetype=application/font-woff&name=assets/[name].[hash].[ext]'
            },
        ],

        postLoaders: [],
    },

    /*
     * https://webpack.github.io/docs/list-of-plugins.html
     */
    plugins: [

        /*
         * https://webpack.github.io/docs/list-of-plugins.html#commonschunkplugin
         */
        new CommonsChunkPlugin({
            name: ['main', 'vendor', 'polyfills'],
        }),

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

    /*
     * PostCSS
     * https://github.com/postcss/postcss-loader
     */
    postcss: function () {
        return [require('autoprefixer')];
    },

    /*
     * Include polyfills or mocks for various node stuff
     * https://webpack.github.io/docs/configuration.html#node
     */
    node: {
        global: 'window',
        crypto: 'empty',
        process: false,
        module: false,
        clearImmediate: false,
        setImmediate: false,
    },
};
