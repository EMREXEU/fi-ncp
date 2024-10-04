import {Opintosuoritus} from "./courses/course";

export default class Utils {
  static resolveCourseLabel(opintosuoritus: Opintosuoritus): string {
    return opintosuoritus.nimi ? opintosuoritus.nimi[0].value : '';
  }

}
