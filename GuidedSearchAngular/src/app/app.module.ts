import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms'; // <-- NgModel lives here
import { HttpClientModule }    from '@angular/common/http';

import { AppComponent } from './app.component';
import { ViewModelComponent } from './viewmodel/viewmodel.component';


@NgModule({
  declarations: [
    AppComponent,
    ViewModelComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule, 
    HttpClientModule, 
  ],
  providers: [],
  bootstrap: [AppComponent], 
})
export class AppModule { 
	
}
