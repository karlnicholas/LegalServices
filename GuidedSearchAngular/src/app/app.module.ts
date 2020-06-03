import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms'; // <-- NgModel lives here
import { HttpClientModule }    from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { ViewModelComponent } from './viewmodel/viewmodel.component';
import { VentryComponent } from './ventry/ventry.component';


@NgModule({
  declarations: [
    AppComponent,
    ViewModelComponent,
    VentryComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule, 
    HttpClientModule, 
    RouterModule.forRoot([]),
  ],
  providers: [],
  bootstrap: [AppComponent], 
})
export class AppModule { 
	
}
