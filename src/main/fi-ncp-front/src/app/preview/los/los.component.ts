import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-los',
  templateUrl: './los.component.html',
  styleUrls: ['../preview.component.css' ,'./los.component.css']
})
export class LosComponent implements OnInit {
  @Input() los: any;
  collapse = {
    los: false,
    hasPart: false
  };

  constructor() { }

  ngOnInit(): void {
  }

}
