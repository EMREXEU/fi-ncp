import { Component } from '@angular/core';
import { I18nService } from 'src/i18n/i18n.service';
import { UserService } from '../user/user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  user$ = this.userService.user$;
  lang$ = this.i18nService.langAction$;
  i18n = this.i18nService.i18n;

  constructor(private userService: UserService, private i18nService: I18nService) { }

  logout(): void {
    window.location.href = '/Shibboleth.sso/Logout';
  }

  changeLang(lang: string): void {
    this.i18nService.changeLanguage(lang);
  }
}
