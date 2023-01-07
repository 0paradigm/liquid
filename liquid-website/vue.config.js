const {defineConfig} = require('@vue/cli-service')
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin')

module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
        proxy: {
            '/gitd': {
                target: 'http://127.0.0.1:5001',
                ws: true,//代理websocked
                changeOrigin: true,//虚拟的站点需要更管origin
                secure: true,
                pathRewrite: {
                    '^/gitd': '' // 重写请求
                }
            }
        }
    },
    configureWebpack: {
        plugins: [
            new MonacoWebpackPlugin()
        ]
    }
})
