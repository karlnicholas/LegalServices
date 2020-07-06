import { Component, Input, Output,  OnDestroy } from '@angular/core';
import { Entry } from '../entry';
import { FacetChangeService } from '../facet-change.service';

@Component({
  selector: 'app-display-reference',
  templateUrl: './display-reference.component.html',
  styleUrls: ['./display-reference.component.css']
})
export class DisplayReferenceComponent {
  constructor(private facetChangeService: FacetChangeService) { }

  @Input() entry: Entry;
  
  onSelect(entry: Entry) {
    this.facetChangeService.changeFacet(entry.fullFacet);
  }
}
