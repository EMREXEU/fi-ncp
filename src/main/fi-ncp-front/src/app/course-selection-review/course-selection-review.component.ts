import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-course-selection-review',
  templateUrl: './course-selection-review.component.html',
  styleUrls: ['./course-selection-review.component.css']
})
export class CourseSelectionReviewComponent implements OnInit {
  @Input() opiskelija: any;

  constructor() {
  }

  ngOnInit(): void {
  }

  exportElmo() {
    // TOOO: POST to returnUrl...
    window.alert("Tuodaan kurssit...");
  }
}
