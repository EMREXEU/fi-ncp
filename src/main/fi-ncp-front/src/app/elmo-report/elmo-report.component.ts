import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-elmo-report',
  templateUrl: './elmo-report.component.html',
  styleUrls: ['./elmo-report.component.css']
})
export class ElmoReportComponent implements OnInit {
  @Input() report: any;

  constructor() {
  }

  ngOnInit(): void {
  }

}
