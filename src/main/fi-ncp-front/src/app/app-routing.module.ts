import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CourseSelectionViewComponent} from "./course-selection-view/course-selection-view.component";
import {CourseSelectionReviewViewComponent} from "./course-selection-review-view/course-selection-review-view.component";

const routes: Routes = [
  {path: 'a', component: CourseSelectionViewComponent},
  {path: 'b', component: CourseSelectionReviewViewComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
