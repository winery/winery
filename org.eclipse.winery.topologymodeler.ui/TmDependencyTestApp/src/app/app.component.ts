import { Component } from '@angular/core';
import { TopologyModelerInputDataFormat } from '../topologyModelerInputDataFormat';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';

  mockData: TopologyModelerInputDataFormat = {
    configuration: {
      id: 'FoodProvider',
      ns: 'http://www.opentosca.org/providers/FoodProvider',
      repositoryURL: 'http://localhost:8080/winery',
      uiURL: 'http://localhost:8080/',
      compareTo: null,
      readonly: false
    },
    // topologyTemplate: topologytemplate,
    topologyTemplate: undefined,
    // visuals: visuals,
    visuals: undefined
  };
}
