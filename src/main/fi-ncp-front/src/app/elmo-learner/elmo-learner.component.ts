import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-elmo-learner',
  templateUrl: './elmo-learner.component.html',
  styleUrls: ['./elmo-learner.component.css']
})
export class ElmoLearnerComponent implements OnInit {
  @Input() learner: any;

  constructor() {
  }

  ngOnInit(): void {
  }

}
