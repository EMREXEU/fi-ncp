import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {MatSliderModule} from '@angular/material/slider';
import {MatPseudoCheckboxModule} from "@angular/material/core";
import {MatTooltipModule} from "@angular/material/tooltip";
import { CourseSelectionComponent } from './course-selection/course-selection.component';
import { CourseSelectionReviewComponent } from './course-selection-review/course-selection-review.component';
import { StudentDetailsComponent } from './student-details/student-details.component';
import { CourseSelectionViewComponent } from './course-selection-view/course-selection-view.component';
import { CourseSelectionReviewViewComponent } from './course-selection-review-view/course-selection-review-view.component';


@NgModule({
  declarations: [
    AppComponent,
    CourseSelectionComponent,
    CourseSelectionReviewComponent,
    StudentDetailsComponent,
    CourseSelectionViewComponent,
    CourseSelectionReviewViewComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    NoopAnimationsModule,
    MatSliderModule,
    MatPseudoCheckboxModule,
    MatTooltipModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
