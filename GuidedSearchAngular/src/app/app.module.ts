import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms'; // <-- NgModel lives here
import { HttpClientModule }    from '@angular/common/http';
import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService }  from './in-memory-data.service';

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
	  // The HttpClientInMemoryWebApiModule module intercepts HTTP requests
	  // and returns simulated server responses.
	  // Remove it when a real server is ready to receive requests.
	  HttpClientInMemoryWebApiModule.forRoot(
	    InMemoryDataService, { dataEncapsulation: false }
	  )
  ],
  providers: [],
  bootstrap: [AppComponent], 
})
export class AppModule { 
	
}
