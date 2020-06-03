import { Component, OnInit, Input } from '@angular/core';
import { ViewModelService } from '../viewmodel.service';
import { Entry } from '../entry';

@Component({
  selector: 'app-ventry',
  templateUrl: './ventry.component.html',
  styleUrls: ['./ventry.component.css']
})
export class VentryComponent implements OnInit {
  @Input() entries: Entry[];
  constructor(private viewModelService: ViewModelService) { }

  ngOnInit() {
  }

}
