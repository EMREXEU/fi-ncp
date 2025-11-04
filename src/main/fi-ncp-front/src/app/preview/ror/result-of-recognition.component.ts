import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-result-of-recognition',
  templateUrl: './result-of-recognition.component.html',
  styleUrls: ['../preview.component.css' ,'./recognition-result.component.css']
})
export class ResultOfRecognitionComponent {
  @Input() ror: any;

  collapse = {
    ror: false,
    basis: true,
    results: false,
    mapping: true,
    attachments: true,
  };
}
