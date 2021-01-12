import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-course-selection',
  templateUrl: './course-selection.component.html',
  styleUrls: ['./course-selection.component.css']
})
export class CourseSelectionComponent implements OnInit {
  @Input() opiskelija: any;

  selectedCourses: Set<any>;
  organizationNames: Map<any, any>;

  constructor() {
    this.selectedCourses = new Set();
    this.organizationNames = new Map<any, any>();
    // TODO: fetch real mappings
    this.organizationNames.set("02536", "Testikoulu");
  }

  ngOnInit(): void {
  }

  courseSelected(x: any) {
    if (this.selectedCourses.has(x)) {
      this.selectedCourses.delete(x);
      console.log("Removed course:" + x);
    } else {
      console.log("Added course:" + x);
      this.selectedCourses.add(x);
    }
    console.log("Courses to review:" + Array.from(this.selectedCourses.values()).toString());
  }

  reviewSelectedCourses() {
    console.log("Courses to review:" + Array.from(this.selectedCourses.values()).toString());
    window.alert("Valitsit kurssit:" + Array.from(this.selectedCourses.values()).toString());
  }
}
