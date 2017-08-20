import { Injectable } from '@angular/core';

@Injectable()
export class PaletteService {
  paletteData =
    [{
        nodeTitle: 'AWS',
        imgUrl: 'http://google.de',
      },
      {
        nodeTitle: 'AWS2',
        imgUrl: 'http://google.de'
      }
      ,
      {
        nodeTitle: 'AWS3',
        imgUrl: 'http://google.de'
      }
      ,
      {
        nodeTitle: 'AWS4',
        imgUrl: 'http://google.de'
      }];

  constructor() {
  }

  public getPaletteData(): any {
    return this.paletteData;
  }

}
