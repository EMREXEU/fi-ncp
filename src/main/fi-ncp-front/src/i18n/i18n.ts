'use strict';

export const i18n = {
  header: {
    title: {
      FI: 'Suomen kansallinen yhteyspiste',
      SV: 'Finlands nationella kontaktpunkt',
      EN: 'Finnish National Contact Point',
    },
    lang: { FI: 'Suomeksi', SV: 'På svenska', EN: 'In English' },
    logout: { FI: 'Kirjaudu ulos', SV: 'Logga ut', EN: 'Logout' },
  },
  footer: {
    accessibilitystatement: {
      FI: 'Saavutettavuusseloste',
      SV: 'Tillgänglighetsutlåtande',
      EN: 'Accessibility statement',
    },
  },
  courses: {
    loading: { FI: 'Haetaan suorituksia...', SV: 'Prestationer hämtas...', EN: 'Fetching courses...' },
    issuers: {
      prompt: {
        FI: 'Sinulla on opintosuorituksia useammalta organisaatiolta, ole hyvä ja valitse organisaatio jonka suorituksia tahdot lähettää.',
        SV: 'Du har studieprestationer från flera organisationer, välj vänligen den organisation vars prestationer du vill sända',
        EN: 'You have transcripts from multiple organizations, please select the organization whose transcript you wish to export.',
      },
      select: {
        FI: 'Valitse organisaatio jonka suorituksia tahdot lähettää',
        SV: 'Välj den organisation vars prestationer du vill sända',
        EN: 'Select the organization whose transcript to export',
      },
    },
    organization: { FI: 'Organisaatio', SV: 'Organisation', EN: 'Organization' },
    coursesselected: { FI: 'kurssia valittu', SV: 'kurser valda', EN: 'courses selected' },
    credits: { FI: 'opintopistettä', SV: 'studiepoäng', EN: 'credits' },
    export: {
      FI: 'Lähetä valitut suoritukset',
      SV: 'Sänd de valda prestationerna',
      EN: 'Send chosen credits',
    },
    table: {
      selectall: { FI: 'Valitse kaikki', SV: 'Välj alla', EN: 'Select all'},
      deselectall: { FI: 'Poista kaikki valinnat', SV: 'Radera alla val', EN: 'Deselect all'},
      select: { FI: 'Valitse', SV: 'Välj', EN: 'Select'},
      deselect: { FI: 'Poista valinta', SV: 'Radera val', EN: 'Deselect'},
      name: { FI: 'Nimi', SV: 'Namn', EN: 'Name' },
      credits: { FI: 'Laajuus', SV: 'Omfattning', EN: 'Credits' },
      evaluation: { FI: 'Arvosana', SV: 'Vitsord', EN: 'Evaluation' },
      regdate: { FI: 'Suorituspäivämäärä', SV: 'Datum för prestationen', EN: 'Registration date' },
    },
    type: {
      degree: { FI: 'Tutkinto', SV: 'Examen', EN: 'Degree' },
      module: { FI: 'Moduuli', SV: 'Modul', EN: 'Module' },
      course: { FI: 'Kurssi', SV: 'Kurs', EN: 'Course' },
    }
  },
  preview: {
    back: { FI: 'Takaisin', SV: 'Tillbaka', EN: 'Back' },
    loading: { FI: 'Tuotetaan esikatselua...', SV: 'Förhandsgranskning skapas...', EN: 'Generating preview...' },
    consent: { FI: 'Siirrä opinto- ja henkilötietoni toiseen EMREX-palvelua käyttävään maahan/korkeakouluun. Maa/korkeakoulu voi sijaita EU/ETA-alueen ulkopuolella.', SV: 'Överför mina studie- och personuppgifter till ett annat land/en annan högskola med hjälp av EMREX-tjänsten. Landet/högskolan kan vara beläget utanför EU/EES-området.', EN: 'Transfer my study data and personal details to other country/institution by using EMREX. Institution/country may locate outside of EU/EEA area.'},
    personalInfo: {
      title: { FI: 'Henkilötiedot', SV: 'Personuppgifter', EN: 'Personal information' },
      givenNames: { FI: 'Etunimet', SV: 'Förnamn', EN: 'Given names' },
      familyName: { FI: 'Sukunimi', SV: 'Efternamn', EN: 'Family name' },
      ssn: { FI: 'Henkilötunnus', SV: 'Personbeteckning', EN: 'Personal Identity Code' },
      oid: { FI: 'Kansallinen oppijanumero', SV: 'Nationellt studentnummer', EN: 'National Learner ID' },
      bday: { FI: 'Syntymäaika', SV: 'Födelsedatum', EN: 'Day of Birth' },
      gender: { FI: 'Sukupuoli', SV: 'Kön', EN: 'Gender' },
      male: { FI: 'Mies', SV: 'Man', EN: 'Male' },
      female: { FI: 'Nainen', SV: 'Kvinna', EN: 'Female' },
      'not applicable': { FI: 'Ei määritelty', SV: 'Inte fastställd', EN: 'Not applicable' },
      'not known': { FI: 'Ei tiedossa', SV: 'Okänt', EN: 'Not known' },
    },
    achievements: {
      title: { FI: 'Lähetettävät suoritukset', SV: 'Studieprestationer att sända', EN: 'Exportable achievements' },
      degree: { FI: 'tutkinto', SV: 'examen', EN: 'degree' },
      degrees: { FI: 'tutkintoa', SV: 'examina', EN: 'degrees' },
      module: { FI: 'moduuli', SV: 'modul', EN: 'module' },
      modules: { FI: 'moduulia', SV: 'moduler', EN: 'modules' },
      course: { FI: 'kurssi', SV: 'kurs', EN: 'course' },
      courses: { FI: 'kurssia', SV: 'kurser', EN: 'courses' },
      credits: { FI: 'opintopistettä', SV: 'studiepoäng', EN: 'credits' },
    },
    submit: { FI: 'Lähetä', SV: 'Sänd', EN: 'Submit' },
    expandElmo: { FI: 'Klikkaa tästä nähdäksesi kaikki lähetettävät tiedot ELMO-formaatissa', SV: 'Klicka här för att se alla uppgifter som sänds i ELMO-formatet', EN: 'Click here to view the whole exportable dataset in ELMO format' },
  }
};
