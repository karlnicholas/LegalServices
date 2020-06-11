import { Component, OnInit } from '@angular/core';
import { ViewModel } from '../viewmodel';
import { Entry } from '../entry';
import { ViewModelService } from '../viewmodel.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  viewmodel: ViewModel;
  isDataAvailable:boolean = false;

  constructor(private viewModelService: ViewModelService) { }

  getViewModel(): void {
	this.viewModelService.getViewModel('').subscribe(viewmodel => {
      this.viewmodel = viewmodel;
      this.isDataAvailable = true;
	});
  }

  ngOnInit() {
	this.getViewModel();
  }

}
