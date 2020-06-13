import { Injectable } from '@angular/core';
import { Subject }    from 'rxjs';

@Injectable()
export class FacetChangeService {

  // Observable string sources
  private facetChangedSource = new Subject<string>();

  // Observable string streams
  facetChanged$ = this.facetChangedSource.asObservable();

  changeFacet(fullFacet: string) {
    this.facetChangedSource.next(fullFacet);
  }
}