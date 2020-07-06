import { Component, Input } from '@angular/core';
import { ViewModel } from '../viewmodel';
import { Entry } from '../entry';
import { FacetChangeService } from '../facet-change.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent {

  constructor(private facetChangeService: FacetChangeService) { }

  @Input() viewmodel: ViewModel;

}
