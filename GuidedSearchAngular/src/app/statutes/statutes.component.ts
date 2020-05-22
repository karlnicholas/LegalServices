import { Component, OnInit } from '@angular/core';
import { StatuteRoot } from '../statute-root';
import { StatuteRootService } from '../statuteroot.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-statuteroots',
  templateUrl: './statutes.component.html',
  styleUrls: ['./statutes.component.css']
})
export class StatutesComponent implements OnInit {

  statuteroots: StatuteRoot[];
  constructor(private statuteRootService: StatuteRootService, private http: HttpClient) { }

  getStatuteRoots(): void {
	this.statuteRootService.getStatuteRoots().subscribe(statuteroots => this.statuteroots = statuteroots);
  }
  
  ngOnInit() {
	this.getStatuteRoots();
  }

  selectedStatuteRoot: StatuteRoot;
  onSelect(statuteroot: StatuteRoot): void {
	this.selectedStatuteRoot = statuteroot;
  }
}
