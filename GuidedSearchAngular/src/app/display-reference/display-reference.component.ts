import { Component, OnInit, Input } from '@angular/core';
import { Entry } from '../entry';

@Component({
  selector: 'app-display-reference',
  templateUrl: './display-reference.component.html',
  styleUrls: ['./display-reference.component.css']
})
export class DisplayReferenceComponent implements OnInit {
  constructor() { }

  @Input() entry: Entry;

  ngOnInit() {
  }
  
}
