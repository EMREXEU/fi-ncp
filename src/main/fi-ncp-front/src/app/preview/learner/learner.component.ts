import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-learner',
  templateUrl: './learner.component.html',
  styleUrls: ['../preview.component.css', './learner.component.css'],
})
export class LearnerComponent implements OnInit {
  @Input() learner;
  collapse = false;

  constructor() {}

  ngOnInit(): void {}
}
