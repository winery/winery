// ng2-select declares its CommonJS files as "module" (ESM), so ngcc cannot find its NgModule.
// Let ngcc read those files as CommonJS instead. Remove together with ng2-select.
module.exports = {
    packages: {
        'ng2-select': {
            entryPoints: {
                '.': { override: { module: undefined, main: 'index.js' } }
            }
        }
    }
};
