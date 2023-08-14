import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-identifiers',
  templateUrl: './identifiers.component.html',
  styleUrls: ['../preview.component.css', './identifiers.component.css']
})
export class IdentifiersComponent implements OnInit {
  @Input() identifiers: any;
  constructor() { }

  ngOnInit(): void {
  }

}
