var path = require('path');
var webpack = require('webpack');

module.exports = {
    context: 'assets/javascripts',
    entry: {
        contributePage: 'src/contributePage',
        thankYouPage: 'src/thankYouPage'
    },

    output: {
        path: 'public/',
        chunkFilename: 'webpack/[chunkhash].js',
        filename: "javascripts/[name].js",
        publicPath: '/assets/'
    },

    resolve: {
        root: [
            path.resolve(__dirname, "assets/javascripts"),
            path.resolve(__dirname, "node_modules")
        ],
        extensions: ["", ".js", ".jsx", ".es6"],
        alias: {
            'respimage': 'respimage/respimage',
            'lazySizes': 'lazysizes/lazysizes'
        }
    },

    module: {
        loaders: [
            {
                test: /\.es6$/,
                exclude: /node_modules/,
                loader: 'babel',
                query: {
                    presets: ['es2015'],
                    plugins: [
                        'transform-object-rest-spread',
                        'transform-object-assign',
                        'transform-es2015-classes',
                        'transform-runtime'
                    ],
                    cacheDirectory: ''
                }
            },

            {
                test: /\.jsx$/,
                exclude: /node_modules/,
                loader: 'babel',
                query: {
                    presets: ['react', 'es2015'],
                    plugins: [
                        'transform-object-rest-spread',
                        'transform-object-assign',
                        'transform-es2015-classes',
                        'transform-runtime'
                    ],
                    cacheDirectory: ''
                }
            }
        ]
    },

    resolveLoader: {
        root: path.join(__dirname, "node_modules")
    },

    progress: true,
    failOnError: true,
    keepalive: false,
    inline: true,

    stats: {
        modules: true,
        reasons: true,
        colors: true
    },

    debug: false,
    devtool: 'source-map',

    devServer: {
        proxy: {
            '**': {
                target: 'http://localhost:9112',
                secure: false
            }
        }
    }
};
