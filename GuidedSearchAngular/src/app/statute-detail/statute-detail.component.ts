import { Component, OnInit, Input } from '@angular/core';
import { StatuteRoot } from '../statute-root';

@Component({
  selector: 'app-statute-detail',
  templateUrl: './statute-detail.component.html',
  styleUrls: ['./statute-detail.component.css']
})
export class StatuteDetailComponent implements OnInit {
  @Input() statuteroot: StatuteRoot;
  constructor() { }

  ngOnInit() {  }

}
