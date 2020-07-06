import { Component } from '@angular/core';
import { ViewModel } from './viewmodel';
import { Entry } from './entry';
import { ViewModelService } from './viewmodel.service';
import { FacetChangeService } from './facet-change.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'], 
  providers: [FacetChangeService]
})
export class AppComponent {
  viewmodel: ViewModel;
  dataAvailable: boolean;

  constructor(
    private viewModelService: ViewModelService, 
    private facetChangeService: FacetChangeService
  ) {
      facetChangeService.facetChanged$.subscribe(
      fullFacet => {
        console.log('fullFacet = ' + fullFacet);
        this.getViewModel(fullFacet);
      });  
   }

  getViewModel(fullFacet: string): void {
	this.viewModelService.getViewModel(fullFacet).subscribe(viewmodel => {
      this.viewmodel = viewmodel;
      this.dataAvailable = true;
	});
  }

  ngOnInit() {
	this.getViewModel('');
  }
}
