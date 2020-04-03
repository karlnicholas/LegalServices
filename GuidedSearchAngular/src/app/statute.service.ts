import { Injectable } from '@angular/core';
import { Statute } from './Statute';
import { STATUTES } from './mock-statutes';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StatuteService {

  constructor() { }

  getStatutes(): Observable<Statute[]> {
    return of(STATUTES);
  }
}
