import { Component, ElementRef, ViewChild } from '@angular/core';
import { I18nService } from 'src/i18n/i18n.service';
import { SessionService } from '../session/session.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  @ViewChild('form') form: ElementRef;
  session$ = this.sessionService.session$;
  lang$ = this.i18nService.langAction$;
  i18n = this.i18nService.i18n;
  returnCode = 'NCP_CANCEL';
  returnMessage = '';

  constructor(
    private sessionService: SessionService,
    private i18nService: I18nService
  ) {}

  logout(): void {
    this.sessionService.logout().subscribe((_) => {
      // document.getElementById('haka-logout').click();
      this.form.nativeElement.submit();
    });
  }

  changeLang(lang: string): void {
    this.i18nService.changeLanguage(lang);
  }
}
