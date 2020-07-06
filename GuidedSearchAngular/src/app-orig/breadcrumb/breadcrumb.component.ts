import { Component, Input } from '@angular/core';
import { Entry } from '../entry';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.css']
})
export class BreadcrumbComponent {

  constructor() { }
  
  private _entriesData;
  @Input()
  set entries(entries: Entry[]) {
    //You can add some custom logic here
    this._entriesData = entries;
    console.log(entries);
    console.log(entries[0].entries.length);
  }
  get entries(): Entry[] { return this._entriesData; }
}
