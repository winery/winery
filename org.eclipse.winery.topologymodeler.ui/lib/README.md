# Topologymodeler Library Bundling

This subdirectory of the topologymodeler contains the tools and configurations that are required for bundling the topologymodeler as a npm package.

## Prerequisites

You have to be part of the @winery organization on npmjs.org to be able to publish a new version of this library.
Before you publish a new version, make sure you increased the version number (either manually or via `npm patch` and similar commands).

## Build

Run `npm start packagr` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `npm start packagr-mac` command for a build on MacOS/Unix systems.

## Publishing

After you've successfully built the library cd into the `dist/` directory and execute `npm publish --access public`.
This flag makes sure you're publishing the org-scoped package publicly.
If you get an error you might have to `npm login` or ask somebody from the @winery npmjs organization to give you publishing rights.


## In the NPM repository

To find the latest build of the topologymodeler check out [@winery/topologymodeler](https://github.com/angular/angular-cli/blob/master/README.md) in the NPM repository.
