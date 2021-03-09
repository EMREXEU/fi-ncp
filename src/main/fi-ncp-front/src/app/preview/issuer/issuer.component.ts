import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-issuer',
  templateUrl: './issuer.component.html',
  styleUrls: ['../preview.component.css', './issuer.component.css'],
})
export class IssuerComponent implements OnInit {
  @Input() issuer;
  collapse = false;

  constructor() {}

  ngOnInit(): void {}
}
