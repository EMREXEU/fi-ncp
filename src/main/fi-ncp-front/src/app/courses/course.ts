export interface ICourseResponse {
  virta: Virta;
}

export interface Tila {
  alkuPvm: Date;
  loppuPvm?: Date;
  koodi: string;
}

export interface Jakso {
  alkuPvm: Date;
  loppuPvm?: any;
  koulutuskoodi: string;
  koulutuskunta: string;
  koulutuskieli: string;
  rahoituslahde: string;
  patevyys: any[];
  luokittelu: any[];
  koulutusmoduulitunniste: string;
  valtakunnallinenKoulutusmoduulitunniste?: any;
}

export interface Ensisijaisuus {
  alkuPvm: Date;
  loppuPvm?: any;
}

export interface Koulutusala {
  value: string;
  versio: string;
}

export interface Laajuus {
  opintopiste: number;
  opintoviikko?: any;
}

export interface Opiskeluoikeus {
  alkuPvm: Date;
  loppuPvm: Date;
  tila: Tila[];
  tyyppi: string;
  myontaja: string;
  organisaatio: any[];
  jakso: Jakso[];
  ensisijaisuus: Ensisijaisuus[];
  koulutusala: Koulutusala;
  erikoistumiskoulutus?: any;
  liittyvyys: any[];
  siirtoOpiskelija?: any;
  laajuus: Laajuus;
  avain: string;
  opiskelijaAvain: string;
}

export interface Opiskeluoikeudet {
  opiskeluoikeus: Opiskeluoikeus[];
}

export interface LukukausiIlmoittautuminen {
  myontaja: string;
  tila: string;
  ilmoittautumisPvm: Date;
  alkuPvm: Date;
  loppuPvm: Date;
  ylioppilaskuntaJasen?: boolean;
  lukuvuosiMaksu?: any;
  opiskelijaAvain: string;
  opiskeluoikeusAvain: string;
  ythsmaksu?: boolean;
}

export interface LukukausiIlmoittautumiset {
  lukukausiIlmoittautuminen: LukukausiIlmoittautuminen[];
}

export interface Laajuus2 {
  opintopiste: number;
  opintoviikko?: any;
}

export interface Arvosana {
  viisiportainen?: string;
  toinenKotimainen?: string;
  hyvaksytty?: string;
  naytetyo?: string;
  tutkielma?: string;
  eiKaytossa?: string;
  muu?: any;  // Not implemented yet
}

export interface Nimi {
  value: string;
  kieli: string;
}

export interface Koodi {
  value: string;
  versio: string;
}

export interface Koulutusala2 {
  koodi: Koodi;
  osuus?: any;
}

export interface Sisaltyvyys {
  opintopiste: number;
  sisaltyvaOpintosuoritusAvain: string;
}

export interface Tkilaajuus {
  opintopiste: number;
  opintoviikko?: any;
}

export interface TkilaajuusMuu {
  opintopiste: number;
  opintoviikko?: any;
}

export interface Opintosuoritus {
  suoritusPvm: Date;
  laajuus: Laajuus2;
  arvosana: Arvosana;
  myontaja: string;
  organisaatio: any[];
  laji: string;
  nimi: Nimi[];
  kieli: string;
  koulutuskoodi?: any;
  koulutusala: Koulutusala2[];
  sisaltyvyys: Sisaltyvyys[];
  hyvaksilukuPvm?: any;
  opetusharjoitteluTyyppi?: any;
  opinnaytetyo?: boolean;
  hankkeistettu?: boolean;
  patevyys: string[];
  luokittelu: string[];
  julkinenLisatieto: any[];
  avain: string;
  opiskelijaAvain: string;
  koulutusmoduulitunniste: string;
  valtakunnallinenKoulutusmoduulitunniste?: any;
  opiskeluoikeusAvain: string;
  tkilaajuus: Tkilaajuus;
  tkilaajuusHarjoittelu?: any;
  tkilaajuusMuu: TkilaajuusMuu;
  hasPart?: Opintosuoritus[];
  weight?: number;
  isDegree?: boolean;
  isPartOfDegree?: boolean;
  degree?: string;
  isModule?: boolean;
  isPartOfModule?: boolean;
  module?: string;
  type?: string;
}

export interface Opintosuoritukset {
  opintosuoritus: Opintosuoritus[];
}

export interface Opiskelija {
  henkilotunnus: string;
  sukunimi?: any;
  etunimet?: any;
  sukupuoli?: any;
  kansalaisuus: any[];
  aidinkieli?: any;
  asuinkunta?: any;
  kirjoihintuloPvm?: any;
  kansallinenOppijanumero?: any;
  avain: string;
  opiskeluoikeudet: Opiskeluoikeudet;
  lukukausiIlmoittautumiset: LukukausiIlmoittautumiset;
  opintosuoritukset: Opintosuoritukset;
  liikkuvuusjaksot?: any;
}

export interface Virta {
  opiskelija: Opiskelija[];
}

export interface IssuerResponseData {
  [key: string]: Issuer
}

interface Issuer {
  code: string
  countryCode: string
  identifierType: string
  identifier: string
  title: string
  domain: string
  url: string
}
