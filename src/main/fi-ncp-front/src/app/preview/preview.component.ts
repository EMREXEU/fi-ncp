import { Component, OnInit } from '@angular/core';
import { CoursesService } from '../courses/courses.service';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css'],
})
export class PreviewComponent implements OnInit {
  constructor(private coursesService: CoursesService) {}

  ngOnInit(): void {
    this.coursesService.getPreview().subscribe((data) =>
      console.log(data)
    );
  }
}
