import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loi',
  templateUrl: './loi.component.html',
  styleUrls: ['../preview.component.css', './loi.component.css'],
})
export class LoiComponent {
  @Input() loi: any;
  collapse = false;
}
