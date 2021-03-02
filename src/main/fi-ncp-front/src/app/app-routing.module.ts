import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CoursesComponent } from './courses/courses.component';
import { PreviewComponent } from './preview/preview.component';

const routes: Routes = [
  { path: 'courses', component: CoursesComponent },
  { path: 'preview', component: PreviewComponent },
  { path: '', pathMatch: 'full', redirectTo: '/courses' },
  { path: '**', redirectTo: '/courses' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
