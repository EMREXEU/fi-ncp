import {Opintosuoritus} from "./courses/course";
import {ArvosanaAsteikkoMuuTyyppi} from "./preview/preview";

export default class Utils {
  static resolveCourseLabel(opintosuoritus: Opintosuoritus): string {
    return opintosuoritus.nimi ? opintosuoritus.nimi[0].value : '';
  }
  static resolveEvaluation(opintosuoritus: Opintosuoritus) {
    return opintosuoritus.arvosana.viisiportainen ||
    opintosuoritus.arvosana.toinenKotimainen ||
    opintosuoritus.arvosana.hyvaksytty ||
    opintosuoritus.arvosana.naytetyo ||
    opintosuoritus.arvosana.tutkielma ||
    opintosuoritus.arvosana.eiKaytossa ||
    this.resolveMuuEvaluation(opintosuoritus.arvosana.muu);
  }

  static resolveMuuEvaluation(muu: ArvosanaAsteikkoMuuTyyppi) {
    if (!muu) {
      return null;
    }
    let koodi = muu.koodi;
    let a;
    if(koodi != null) {
      a = muu.asteikko.asteikkoArvosana.filter(asteikkoArvosana => asteikkoArvosana.avain === koodi)
    }
    if (a && a.length === 1) {
      return a[0].nimi;
    }
    return null;
  }
}
