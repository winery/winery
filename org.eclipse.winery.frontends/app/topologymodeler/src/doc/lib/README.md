# @Winery/Topologymodeler
This makes the [OpenTOSCA/Winery topologymodeler](https://github.com/OpenTOSCA/winery) module available as npm package.

## Install
### `npm install @winery/topologymodeler --save`

## Usage
### 1. Add the `WineryModule` to your app.module.ts:
```
import { WineryModule } from '@winery/topologymodeler';

...

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    ...,
    WineryModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

### 2. Use Topologymodeler to render a TopologyTemplate:
```
<winery-topologymodeler [topologyModelerData]="...">
</winery-topologymodeler>
```

#### `topologyModelerData` has to be of this type:
```
export interface TopologyModelerInputDataFormat {
        configuration: {
            readonly: boolean,
            endpointConfig: TopologyModelerConfiguration
        };
        topologyTemplate: TTopologyTemplate;
        visuals: Visuals;
}

TopologyModelerConfiguration {
    readonly id: string;
    readonly ns: string;
    readonly repositoryURL: string;
    readonly uiURL: string;
    readonly compareTo: string;
}
```

__Attention: this package has the following `peerDependencies`:__

__Please make sure that you install them manually or by adding them to your `dependencies`__

```
"peerDependencies": {
    "@angular-redux/store": "6.5.7",
    "@angular/animations": "6.0.6",
    "@angular/cdk": "6.2.1",
    "@angular/common": "6.0.6",
    "@angular/core": "6.0.6",
    "@angular/forms": "6.0.6",
    "@angular/http": "6.0.6",
    "@angular/platform-browser": "6.0.6",
    "@angular/platform-browser-dynamic": "6.0.6",
    "@angular/router": "6.0.6",
    "angular2-hotkeys": "2.0.4",
    "bootstrap": "4.1.1",
    "core-js": "2.5.6",
    "css-element-queries": "0.4.0",
    "element-resize-detector": "1.1.12",
    "elkjs": "0.0.1",
    "font-awesome": "4.7.0",
    "jsplumb": "2.7.9",
    "ngx-bootstrap": "2.0.4",
    "ngx-toastr": "^8.5.1",
    "redux": "3.7.2",
    "rxjs": "6.1.0",
    "zone.js": "0.8.26",
    "rxjs-compat": "^6.0.0-rc.0"
}
```
