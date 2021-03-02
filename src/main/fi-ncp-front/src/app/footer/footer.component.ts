import { Component, OnInit } from '@angular/core';
import { I18nService } from 'src/i18n/i18n.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {
  lang$ = this.i18nService.langAction$;
  i18n = this.i18nService.i18n;

  constructor(private i18nService: I18nService) { }

  ngOnInit(): void {
  }

}
