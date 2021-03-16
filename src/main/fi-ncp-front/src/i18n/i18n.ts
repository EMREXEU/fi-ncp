'use strict';

export const i18n = {
  header: {
    title: {
      FI: 'Suomen kansallinen yhteyspiste',
      SV: '',
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
    loading: { FI: 'Haetaan suorituksia...', SV: '', EN: 'Fetching courses...' },
    issuers: {
      prompt: {
        FI:
          'Sinulla on opintosuorituksia useammalta organisaatiolta, ole hyvä ja valitse organisaatio jonka suorituksia tahdot lähettää.',
        SV: '',
        EN:
          'You have transcripts from multiple organizations, please select the organization whose transcript you wish to export.',
      },
      select: {
        FI: 'Valitse organisaatio jonka suorituksia tahdot lähettää',
        SV: '',
        EN: 'Select the organization whose transcript to export',
      },
    },
    organization: { FI: 'Organisaatio', SV: '', EN: 'Organization' },
    coursesselected: { FI: 'kurssia valittu', SV: '', EN: 'courses selected' },
    credits: { FI: 'opintopistettä', SV: '', EN: 'credits' },
    export: {
      FI: 'Lähetä valitut suoritukset',
      SV: '',
      EN: 'Send chosen credits',
    },
    table: {
      selectall: { FI: 'Valitse kaikki', SV: '', EN: 'Select all'},
      deselectall: { FI: 'Poista kaikki valinnat', SV: '', EN: 'Deselect all'},
      select: { FI: 'Valitse', SV: '', EN: 'Select'},
      deselect: { FI: 'Poista valinta', SV: '', EN: 'Deselect'},
      name: { FI: 'Nimi', SV: '', EN: 'Name' },
      credits: { FI: 'Laajuus', SV: '', EN: 'Credits' },
      evaluation: { FI: 'Arvosana', SV: '', EN: 'Evaluation' },
      regdate: { FI: 'Suorituspäivämäärä', SV: '', EN: 'Registration date' },
    },
    type: {
      degree: { FI: 'Tutkinto', SV: '', EN: 'Degree' },
      module: { FI: 'Moduuli', SV: '', EN: 'Module' },
      course: { FI: 'Kurssi', SV: '', EN: 'Course' },
    }
  },
  preview: {
    back: { FI: 'Takaisin', SV: '', EN: 'Back' },
    loading: { FI: 'Tuotetaan esikatselua...', SV: '', EN: 'Generating preview...' },
    personalInfo: {
      title: { FI: 'Henkilötiedot', SV: '', EN: 'Personal information' },
      givenNames: { FI: 'Etunimet', SV: '', EN: 'Given names' },
      familyName: { FI: 'Sukunimi', SV: '', EN: 'Family name' },
      ssn: { FI: 'Henkilötunnus', SV: '', EN: 'Personal Identity Code' },
      oid: { FI: 'Kansallinen oppijanumero', SV: '', EN: 'National Learner ID' },
      bday: { FI: 'Syntymäaika', SV: '', EN: 'Day of Birth' },
      gender: { FI: 'Sukupuoli', SV: '', EN: 'Gender' },
      male: { FI: 'Mies', SV: '', EN: 'Male' },
      female: { FI: 'Nainen', SV: '', EN: 'Female' },
      'not applicable': { FI: 'Ei määritelty', SV: '', EN: 'Not applicable' },
      'not known': { FI: 'Ei tiedossa', SV: '', EN: 'Not known' },
    },
    achievements: {
      title: { FI: 'Lähetettävät suoritukset', SV: '', EN: 'Exportable achievements' },
      degree: { FI: 'tutkinto', SV: '', EN: 'degree' },
      degrees: { FI: 'tutkintoa', SV: '', EN: 'degrees' },
      module: { FI: 'moduuli', SV: '', EN: 'module' },
      modules: { FI: 'moduulia', SV: '', EN: 'modules' },
      course: { FI: 'kurssi', SV: '', EN: 'course' },
      courses: { FI: 'kurssia', SV: '', EN: 'courses' },
      credits: { FI: 'opintopistettä', SV: '', EN: 'credits' },
    },
    submit: { FI: 'Lähetä', SV: '', EN: 'Submit' },
    expandElmo: { FI: 'Klikkaa tästä nähdäksesi kaikki lähetettävät tiedot Elmo formaatissa', SV: '', EN: 'Click here to view the whole exportable dataset in Elmo format' },
  }
};
