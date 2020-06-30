import { Component, Input } from '@angular/core';
import { Entry } from '../entry';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.css']
})
export class BreadcrumbComponent {

  constructor() { }
  
  @Input() entries: Entry[];

}
