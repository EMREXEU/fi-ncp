import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { i18n } from './i18n';

@Injectable({
  providedIn: 'root'
})
export class I18nService {
  private lang = new BehaviorSubject<string>('FI');
  i18n: Record<string, any> = i18n;
  langAction$ = this.lang.asObservable();

  constructor() { }

  changeLanguage(lang: string): void {
    this.lang.next(lang);
  }
}
