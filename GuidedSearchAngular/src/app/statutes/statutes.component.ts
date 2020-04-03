import { Component, OnInit } from '@angular/core';
import { Statute } from '../statute';
import { StatuteService } from '../statute.service';

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

  statutes: Statute[];
  constructor(private statuteService: StatuteService) { }

  getStatutes(): void {
	this.statuteService.getStatutes()
	  .subscribe(statutes => this.statutes = statutes);
  }
  ngOnInit() {
	this.getStatutes();
  }

  selectedStatute: Statute;
  onSelect(statute: Statute): void {
	this.selectedStatute = statute;
  }

}
