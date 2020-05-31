import { Component, OnInit } from '@angular/core';
import { ViewModel } from '../viewmodel';
import { ViewModelService } from '../viewmodel.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-viewmodel',
  templateUrl: './viewmodel.component.html',
  styleUrls: ['./viewmodel.component.css']
})
export class ViewModelComponent implements OnInit {

  viewmodel: ViewModel;
  isDataAvailable:boolean = false;
  constructor(private viewModelService: ViewModelService, private http: HttpClient) { }

  getViewModel(): void {
	this.viewModelService.getViewModel().subscribe(viewmodel => {
		this.viewmodel = viewmodel;
		this.isDataAvailable = true;
	});
  }
  
  ngOnInit() {
	this.getViewModel();
  }

}
