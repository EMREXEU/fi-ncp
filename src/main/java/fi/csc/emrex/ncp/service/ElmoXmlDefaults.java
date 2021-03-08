package fi.csc.emrex.ncp.service;

import fi.csc.schemas.elmo.CountryCode;

/**
 * Single place for ELMO XML constants.
 */
public class ElmoXmlDefaults {

  public static final String DEFAULT_LEARNER_ID_TYPE = "nationalIdentifier";
  public static final CountryCode VIRTA_ISSUER_COUNTRY_CODE = CountryCode.FI;

  public static class LOS {

    public static final String ID_TYPE = "koulutusmoduulitunniste";

    public static class TYPE {

      public static final String DEGREE = "Degree Programme";
      public static final String MODULE = "Module";
      public static final String COURSE = "Course";
      public static final String DEFAULT = COURSE;
    }
  }

  public static class LOI {

    public static final String ID_TYPE = "opintosuoritus_avain";
    public static final String CREDIT_SCHEME = "ECTS";
    public static final String LEVEL_TYPE = "EQF";
    public static final String STATUS = "passed";

  }
}
