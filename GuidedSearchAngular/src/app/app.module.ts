import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms'; // <-- NgModel lives here
import { HttpClientModule }    from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { ViewModelComponent } from './viewmodel/viewmodel.component';
import { VentryComponent } from './ventry/ventry.component';
import { DisplayReferenceComponent } from './display-reference/display-reference.component';
import { RecurseComponent } from './recurse/recurse.component';

import { ViewModelService } from './viewmodel.service';
import { DisplayTextComponent } from './display-text/display-text.component';
import { NavigationComponent } from './navigation/navigation.component';
import { BreadcrumbComponent } from './breadcrumb/breadcrumb.component';


@NgModule({
  declarations: [
    AppComponent,
    ViewModelComponent,
    VentryComponent,
    DisplayReferenceComponent,
    RecurseComponent,
    DisplayTextComponent,
    NavigationComponent,
    BreadcrumbComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule, 
    HttpClientModule, 
    RouterModule.forRoot([]),
  ],
  providers: [ViewModelService],
  bootstrap: [AppComponent], 
})
export class AppModule { 
	
}
