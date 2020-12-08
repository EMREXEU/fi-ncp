package fi.csc.emrex.ncp.service;

import fi.csc.schemas.elmo.CountryCode;

public class ElmoDefaults {

  public static final String DEFAULT_LEARNER_ID_TYPE = "nationalIdentifier";
    public static final CountryCode VIRTA_ISSUER_COUNTRY_CODE = CountryCode.FI;

  public static class LOI {

    // TODO: what is proper type?
    public static final String ID_TYPE = "opintosuoritus_avain";
    public static final String CREDIT_SCHEME = "ECTS";
    public static final String LEVEL_TYPE = "EQF";
    public static final String STATUS = "passed";

  }
}
