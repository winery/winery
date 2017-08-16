import { Org.Eclipse.Winery.Topologymodeler.UiPage } from './app.po';

describe('org.eclipse.winery.topologymodeler.ui App', () => {
  let page: Org.Eclipse.Winery.Topologymodeler.UiPage;

  beforeEach(() => {
    page = new Org.Eclipse.Winery.Topologymodeler.UiPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
