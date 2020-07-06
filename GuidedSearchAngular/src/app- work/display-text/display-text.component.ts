import { Component, Input, Output,  OnDestroy } from '@angular/core';
import { Entry } from '../entry';
import { FacetChangeService } from '../facet-change.service';

@Component({
  selector: 'app-display-text',
  templateUrl: './display-text.component.html',
  styleUrls: ['./display-text.component.css']
})
export class DisplayTextComponent {

  constructor() { }

  @Input() entry: Entry;

}
