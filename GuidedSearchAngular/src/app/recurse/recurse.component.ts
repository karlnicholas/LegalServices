import { Component, Input } from '@angular/core';
import { Entry } from '../entry';

@Component({
  selector: 'app-recurse',
  templateUrl: './recurse.component.html',
  styleUrls: ['./recurse.component.css']
})
export class RecurseComponent {
  constructor() { }

  @Input() entries: Entry[];

}
