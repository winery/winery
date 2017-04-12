# Winery UI

## Prerequisites
1. Install [git](https://git-scm.com)
2. Install and setup [Node.js](https://nodejs.org/en/) and [NPM](https://www.npmjs.com) for [Angular2](https://angular.io/docs/ts/latest/quickstart.html)

## Setup Local Development Server
1. `npm install`
2. `npm start` 
    - be sure that no other application is listening on port 3000
    - if you need to switch to another port adapt config/webpack.dev.js

## TSLint
1. Go to `File -> Settings -> Language and Frameworks -> TypeScript -> TSLint`
2. Set `Enabled` to true
3. Press `OK` 


## Production Build
After step 3 at setting up local development server do
1. `npm run build` (please note that production build is only triggerd if linting returns with no errors!)
2. optional: if you want to serve the production build
    - `npm run build:serve`
    
## Just do Linting
TODO not working yet
1. `npm run lint`


## Generate WineryUi.war
**BEFORE** building the production build or the war file, it must be ensured, that the ``<base href="/">`` is changed to ``<base href="./">``,
 otherwise it will not work if hosted on tomcat. For more information, [see here](http://stackoverflow.com/questions/39018765/deploy-angular-2-app-with-webpack-to-tomcat-404-errors).

1. Do production build
2. `npm run war`

or alternatively
1. Do production build
2. `cd dist`
3. `jar cvf WineryUi.war .`