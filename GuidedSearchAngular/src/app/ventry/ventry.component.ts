import { Component, OnInit, Input } from '@angular/core';
import { ViewModel } from '../viewmodel';
import { Entry } from '../entry';
import { ViewModelService } from '../viewmodel.service';

@Component({
  selector: 'app-ventry',
  templateUrl: './ventry.component.html',
  styleUrls: ['./ventry.component.css']
})
export class VentryComponent implements OnInit {
  viewmodel: ViewModel;
  entries: Entry[];
  entry: Entry;
  isDataAvailable:boolean = false;
  constructor(private viewModelService: ViewModelService) { }

  getViewModel(): void {
	this.viewModelService.getViewModel('').subscribe(viewmodel => {
      this.entries = viewmodel.entries;
      this.isDataAvailable = true;
	});
  }

  selectHome() {
	this.viewModelService.getViewModel('').subscribe(viewmodel => {
		this.viewmodel = viewmodel;
		this.entries = viewmodel.entries;
	});
  }

  onSelect(entry) {
	this.viewModelService.getViewModel(entry.fullFacet).subscribe(viewmodel => {
	  this.viewmodel = viewmodel;
      this.entries = viewmodel.entries;
      if ( this.viewmodel.pathPart == true ) {
        this.entry = this.viewmodel.entries[0];
        while ( this.entry.pathPart == true ) {
          this.entries = this.entry.entries;
          this.entry = this.entry.entries[0];
        }
      }
	});
  }
  
  ngOnInit() {
	this.getViewModel();
  }

}
