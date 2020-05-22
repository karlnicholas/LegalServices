import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms'; // <-- NgModel lives here
import { HttpClientModule }    from '@angular/common/http';

import { AppComponent } from './app.component';
import { StatutesComponent } from './statutes/statutes.component';
import { StatuteDetailComponent } from './statute-detail/statute-detail.component';

@NgModule({
  declarations: [
    AppComponent,
    StatutesComponent,
    StatuteDetailComponent
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
