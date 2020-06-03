import { Component, OnInit } from '@angular/core';
import { ViewModel } from '../viewmodel';
import { ViewModelService } from '../viewmodel.service';

@Component({
  selector: 'app-viewmodel',
  templateUrl: './viewmodel.component.html',
  styleUrls: ['./viewmodel.component.css']
})
export class ViewModelComponent implements OnInit {

  viewmodel: ViewModel;
  isDataAvailable:boolean = false;
  constructor(private viewModelService: ViewModelService) { }

  getViewModel(): void {
	this.viewModelService.getViewModel('').subscribe(viewmodel => {
		this.viewmodel = viewmodel;
		this.isDataAvailable = true;
	});
  }

  selectHome() {
	this.viewModelService.getViewModel('').subscribe(viewmodel => {
		this.viewmodel = viewmodel;
		this.isDataAvailable = true;
	});
  }

  onSelect(entry) {
	this.viewModelService.getViewModel(entry.fullFacet).subscribe(viewmodel => {
		this.viewmodel = viewmodel;
		this.isDataAvailable = true;
	});
  }
  
  ngOnInit() {
	this.getViewModel();
  }

}
