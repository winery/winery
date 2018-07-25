import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { WineryModule } from '@winery/topologymodeler';


import { AppComponent } from './app.component';


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    WineryModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
