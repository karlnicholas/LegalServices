import { Component, OnInit, Input } from '@angular/core';
import { Statute } from '../statute';

@Component({
  selector: 'app-statute-detail',
  templateUrl: './statute-detail.component.html',
  styleUrls: ['./statute-detail.component.css']
})
export class StatuteDetailComponent implements OnInit {
  @Input() statute: Statute;
  constructor() { }

  ngOnInit() {
  }

}
