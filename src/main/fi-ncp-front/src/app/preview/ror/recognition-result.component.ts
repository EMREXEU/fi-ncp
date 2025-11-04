import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-recognition-result',
  templateUrl: './recognition-result.component.html',
  styleUrls: ['../preview.component.css' ,'./recognition-result.component.css']
})
export class RecognitionResultComponent {
  @Input() rr: any;

  collapse = {
    rr: false
  };
}
