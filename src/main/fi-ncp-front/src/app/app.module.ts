import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { CoursesComponent } from './courses/courses.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { PreviewComponent } from './preview/preview.component';
import { LearnerComponent } from './preview/learner/learner.component';
import { IdentifiersComponent } from './preview/identifiers/identifiers.component';
import { IssuerComponent } from './preview/issuer/issuer.component';
import { LosComponent } from './preview/los/los.component';
import { LoiComponent } from './preview/loi/loi.component';
import { LoadingComponent } from './loading/loading.component';

@NgModule({
  declarations: [
    AppComponent,
    CoursesComponent,
    HeaderComponent,
    FooterComponent,
    PreviewComponent,
    LearnerComponent,
    IdentifiersComponent,
    IssuerComponent,
    LosComponent,
    LoiComponent,
    LoadingComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    NoopAnimationsModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
