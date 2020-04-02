import { Component, OnInit } from '@angular/core';
import { Statute } from '../statute';
import { STATUTES } from '../mock-statutes';

@Component({
  selector: 'app-statutes',
  templateUrl: './statutes.component.html',
  styleUrls: ['./statutes.component.css']
})
export class StatutesComponent implements OnInit {

  statute: Statute = {
    id: 1,
    name: 'Windstorm'
  };

  statutes = STATUTES;
  constructor() { }

  ngOnInit() {
  }

  selectedStatute: Statute;
  onSelect(statute: Statute): void {
	this.selectedStatute = statute;
  }

}
