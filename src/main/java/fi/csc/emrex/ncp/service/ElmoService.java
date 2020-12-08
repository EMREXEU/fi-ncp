package fi.csc.emrex.ncp.service;

import static fi.csc.emrex.ncp.service.ElmoDefaults.DEFAULT_LEARNER_ID_TYPE;

import fi.csc.emrex.ncp.dto.IssuerDto;
import fi.csc.emrex.ncp.dto.LearnerDetailsDto;
import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.emrex.ncp.service.ElmoDefaults.LOI;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.CountryCode;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.schemas.elmo.Elmo.Learner;
import fi.csc.schemas.elmo.Elmo.Report;
import fi.csc.schemas.elmo.Elmo.Report.Issuer;
import fi.csc.schemas.elmo.LearningOpportunitySpecification;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies.LearningOpportunityInstance;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Credit;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Identifier;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Level;
import fi.csc.schemas.elmo.TokenWithOptionalLang;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import mace.funet_fi.virta._2015._09._01.OpintosuorituksetTyyppi;
import mace.funet_fi.virta._2015._09._01.OpintosuoritusTyyppi;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ElmoService {


  // Opintosuoritus.Myontaja is just a VIRTA code which needs to be mapped to actual organization details
  // Key: Opintosuoritus.Myontaja
  private Map<String, IssuerDto> virtaIssuerCodeToIssuer = new HashMap<>();

  @PostConstruct
  public void init() {
    // TODO: Update this map from config, include all codes.
    // TODO: pic/erasmus/schac	SCHAC - muodostettava palvelimella opintosuorituksen myöntäjästä
    virtaIssuerCodeToIssuer.put(
        "02536",
        new IssuerDto(
            CountryCode.FI,
            "TODO: identifier type from cached config",
            "TODO: identifier from cached config",
            "TODO: Title from cached config",
            "TODO: url from cached config"));
  }

  public OpintosuorituksetResponse trimToSelectedCourses(
      OpintosuorituksetResponse virtaXml,
      List<String> courseKeys) {

    List<OpintosuoritusTyyppi> opintosuoritukset = virtaXml.getOpintosuoritukset()
        .getOpintosuoritus();
    OpintosuorituksetTyyppi opintosuorituksetTyyppi = new OpintosuorituksetTyyppi();
    // Initializes to empty array
    opintosuorituksetTyyppi.getOpintosuoritus();
    opintosuoritukset.forEach(course -> {
      if (courseKeys.contains(course.getAvain())) {
        opintosuorituksetTyyppi.getOpintosuoritus().add(course);
      }
    });
    virtaXml.setOpintosuoritukset(opintosuorituksetTyyppi);
    return virtaXml;
  }

  public Elmo convertToElmoXml(
      OpintosuorituksetResponse virtaXml,
      VirtaUserDto student,
      LearnerDetailsDto learnerDetails) throws NpcException {

    // TODO: report.issuer
    try {
      Elmo elmo = new Elmo();
      elmo.setLearner(createLearner(student, learnerDetails));
      elmo.setGeneratedDate(
          DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
      List<Elmo.Report> reports = elmo.getReport();

      for (OpintosuoritusTyyppi opintosuoritus : virtaXml.getOpintosuoritukset()
          .getOpintosuoritus()) {
        reports.add(createReport(opintosuoritus, learnerDetails));
      }

      return elmo;
    } catch (DatatypeConfigurationException e) {
      throw new NpcException("Creating XMLGregorianCalendar failed.", e);
    }
  }

  private Learner createLearner(VirtaUserDto student, LearnerDetailsDto details) {
    Elmo.Learner learner = new Elmo.Learner();

    learner.setCitizenship(details.getCitizenship());

    Elmo.Learner.Identifier identifier = new Elmo.Learner.Identifier();
    identifier.setType(DEFAULT_LEARNER_ID_TYPE);
    identifier.setValue(student.getSsn());
    learner.getIdentifier().add(identifier);

    learner.setGivenNames(details.getGivenNames());
    learner.setFamilyName(details.getFamilyName());

    // Given name
    return learner;
  }

  private Report createReport(OpintosuoritusTyyppi opintosuoritus, LearnerDetailsDto details)
      throws NpcException, DatatypeConfigurationException {

    Elmo.Report report = new Elmo.Report();
    // Must create a copy of calendar as reference to VIRTA data seems to disappear.
    report.setIssueDate(copyOf(opintosuoritus.getSuoritusPvm()));
    report.setIssuer(createIssuer(opintosuoritus, details));
    report.getLearningOpportunitySpecification()
        .add(createLearningOpportunitySpecification(opintosuoritus));
    return report;
  }

  private LearningOpportunitySpecification createLearningOpportunitySpecification(
      OpintosuoritusTyyppi opintosuoritus) throws NpcException {
    LearningOpportunitySpecification learningOpportunitySpecification = new LearningOpportunitySpecification();

    learningOpportunitySpecification.setType(createLOSpecType(opintosuoritus.getLaji()));

    learningOpportunitySpecification.setSubjectArea(opintosuoritus.getKoulutuskoodi());
    learningOpportunitySpecification.setIscedCode(opintosuoritus.getKoulutuskoodi());
    learningOpportunitySpecification.setSpecifies(createSpecifies(opintosuoritus));
    createLocalizedTitles(opintosuoritus, learningOpportunitySpecification);
    return learningOpportunitySpecification;
  }


  /**
   * <pre>
   *
   * VIRTA (OpintosuoritusLajiKoodiTyyppi):
   * 		<xs:enumeration value="1"/> <!-- tutkinto -->
   * 		<xs:enumeration value="2"/> <!-- muu opintosuoritus -->
   * 		<xs:enumeration value="3"/> <!-- ei huomioitava -->
   * 		<xs:enumeration value="4"/> <!-- oppilaitoksen sisäinen -->
   *
   * ELMO:
   *    <xs:enumeration value="Degree Programme">
   *    <xs:enumeration value="Module">
   *    <xs:enumeration value="Course">
   *    <xs:enumeration value="Class">
   * </pre>
   *
   * @return ELMO type mapped from VIRTA type
   */
  private String createLOSpecType(String virtaType) {
    String DEFAULT_LOS_SPEC_TYPE = "Course";
    String elmoType = DEFAULT_LOS_SPEC_TYPE;

    if ("1".equalsIgnoreCase(virtaType)) {
      elmoType = "Degree Programme";
    }
    return elmoType;
  }

  private Specifies createSpecifies(OpintosuoritusTyyppi opintosuoritus) throws NpcException {

    LearningOpportunityInstance learningOpportunityInstance = new LearningOpportunityInstance();
    LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Identifier identifier = new Identifier();
    identifier.setType(LOI.ID_TYPE);
    identifier.setValue(opintosuoritus.getKoulutusmoduulitunniste());
    learningOpportunityInstance.getIdentifier().add(identifier);
    learningOpportunityInstance.setDate(copyOf(opintosuoritus.getSuoritusPvm()));

    learningOpportunityInstance.setStatus(LOI.STATUS);
    learningOpportunityInstance.setResultLabel(opintosuoritus.getArvosana().getViisiportainen());
    learningOpportunityInstance.getCredit().add(createCredit(opintosuoritus));
    learningOpportunityInstance.getLevel().add(createLevel(opintosuoritus));
    learningOpportunityInstance.setLanguageOfInstruction(opintosuoritus.getKieli());

    Specifies specifies = new Specifies();
    specifies.setLearningOpportunityInstance(learningOpportunityInstance);
    return specifies;
  }

  private Credit createCredit(OpintosuoritusTyyppi opintosuoritus) {
    Credit credit = new Credit();
    credit.setScheme(LOI.CREDIT_SCHEME);
    credit.setValue(opintosuoritus.getLaajuus().getOpintopiste());
    return credit;
  }

  private Level createLevel(OpintosuoritusTyyppi opintosuoritus) {
    Level level = new Level();
    level.setType(LOI.LEVEL_TYPE);
    return level;
  }

  /**
   * Always defaults to FI.
   */
  private Issuer createIssuer(OpintosuoritusTyyppi opintosuoritus, LearnerDetailsDto details)
      throws NpcException {

    IssuerDto issuerDto = issuerForCode(opintosuoritus.getMyontaja());

    Issuer issuer = new Issuer();
    issuer.setCountry(issuerDto.getCountryCode());
    issuer.getIdentifier().add(createIdentifier(
        issuerDto.getIdentifierType(),
        issuerDto.getIdentifier()));
    issuer.getTitle().add(createLocalizedToken(
        issuerDto.getCountryCode(),
        issuerDto.getTitle()));
    issuer.setUrl(issuerDto.getUrl());

    return issuer;
  }

  /**
   * @param issuerCode VIRTA XML: Opintosuoritus.Myontaja
   * @return cached Issuer details
   * @throws NpcException No issuer found for key
   */
  private IssuerDto issuerForCode(String issuerCode) throws NpcException {
    IssuerDto issuer = virtaIssuerCodeToIssuer.get(issuerCode);
    if (issuer == null) {
      throw new NpcException(String.format("Issuer not found for issuer code:%s", issuerCode));
    }
    return issuer;
  }

  private Issuer.Identifier createIdentifier(String type, String value) {
    Elmo.Report.Issuer.Identifier identifier = new Issuer.Identifier();
    identifier.setType(type);
    identifier.setValue(value);
    return identifier;
  }

  private TokenWithOptionalLang createLocalizedToken(CountryCode countryCode, String tokenText) {
    TokenWithOptionalLang token = new TokenWithOptionalLang();
    token.setLang(countryCode.toString());
    token.setValue(tokenText);
    return token;
  }

  /**
   * Mutator method from VIRTA XML to ELMO XML
   *
   * @param source read course title data
   * @param target write course title data
   */
  private void createLocalizedTitles(
      OpintosuoritusTyyppi source,
      LearningOpportunitySpecification target) {
    // Init empty list
    target.getTitle();
    source.getNimi().forEach(name -> {
      TokenWithOptionalLang token = new TokenWithOptionalLang();
      token.setLang(name.getKieli());
      token.setValue(name.getValue());
      target.getTitle().add(token);
    });
  }

  public void postElmo(String elmoString, NcpRequestDto ncpRequestDto) {
    // TODO
  }

  /**
   * From some reason setting existing   protected XMLGregorianCalendar copyOf(XMLGregorianCalendar
   * source) throws NpcException { entry to target XML will be empty -> create copy instead
   *
   * @param source original XML entry which will not exist afterwards
   * @return copy of source
   */
  protected XMLGregorianCalendar copyOf(XMLGregorianCalendar source) throws NpcException {
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(
          source.toGregorianCalendar());
    } catch (DatatypeConfigurationException e) {
      throw new NpcException("Creating XMLGregorianCalendar failed.", e);
    }
  }

}
