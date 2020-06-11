import { Component, OnInit, Input } from '@angular/core';
import { Entry } from '../entry';

@Component({
  selector: 'app-recurse',
  templateUrl: './recurse.component.html',
  styleUrls: ['./recurse.component.css']
})
export class RecurseComponent implements OnInit {
  constructor() { }

  @Input() entries: Entry[];

  ngOnInit() {
  }

}
