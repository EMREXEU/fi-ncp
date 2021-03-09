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
    loading: { FI: 'Tuotetaan esikatselua...', SV: '', EN: 'Generating preview...' },
  }
};
