package fi.csc.emrex.ncp.service;

import fi.csc.emrex.ncp.dto.LearnerDetailsDto;
import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.CountryCode;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.schemas.elmo.Elmo.Learner;
import fi.csc.schemas.elmo.Elmo.Report;
import fi.csc.schemas.elmo.Elmo.Report.Issuer;
import fi.csc.schemas.elmo.LearningOpportunitySpecification;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies.LearningOpportunityInstance;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Identifier;
import fi.csc.schemas.elmo.TokenWithOptionalLang;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import lombok.extern.slf4j.Slf4j;
import mace.funet_fi.virta._2015._09._01.OpintosuorituksetTyyppi;
import mace.funet_fi.virta._2015._09._01.OpintosuoritusTyyppi;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ElmoService {

  private String DEFAULT_LEARNER_ID_TYPE = "nationalIdentifier";
  // Opintosuoritus.Myontaja is just a code which needs to be mapped to actual organization name
  private Map<String, String> issuerCodeToName = new HashMap<>();

  @PostConstruct
  public void init() {
    // TODO: Update this map from config, include all codes.
    issuerCodeToName.put("02536", "TODO: Oikea myöntäjän nimien konfiguraatio");
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
      throws NpcException {

    Elmo.Report report = new Elmo.Report();
    report.setIssueDate(opintosuoritus.getSuoritusPvm());
    report.setIssuer(createIssuer(opintosuoritus, details));

    List<LearningOpportunitySpecification> learningOpportunitySpecifications =
        report.getLearningOpportunitySpecification();
    learningOpportunitySpecifications.add(createLearningOpportunitySpecification(opintosuoritus));

    return report;
  }

  private LearningOpportunitySpecification createLearningOpportunitySpecification(
      OpintosuoritusTyyppi opintosuoritus) {
    LearningOpportunitySpecification learningOpportunitySpecification = new LearningOpportunitySpecification();

    // TODO: https://github.com/erasmus-without-paper/ewp-specs-api-courses#unique-identifiers
    String L_SPEC_TYPE = "Course";
    //learningOpportunitySpecification.setType(opintosuoritus.getKoulutusmoduulitunniste());
    learningOpportunitySpecification.setType(L_SPEC_TYPE);

    learningOpportunitySpecification.setSubjectArea(opintosuoritus.getKoulutuskoodi());
    learningOpportunitySpecification.setIscedCode(opintosuoritus.getKoulutuskoodi());
    learningOpportunitySpecification.setSpecifies(createSpecifies(opintosuoritus));
    createLocalizedTitles(opintosuoritus, learningOpportunitySpecification);
    return learningOpportunitySpecification;
  }

  private Specifies createSpecifies(OpintosuoritusTyyppi opintosuoritus) {
    Specifies specifies = new Specifies();

    LearningOpportunityInstance learningOpportunityInstance = new LearningOpportunityInstance();
    LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Identifier identifier = new Identifier();
    identifier.setType(opintosuoritus.getKoulutusmoduulitunniste());
    learningOpportunityInstance.getIdentifier().add(identifier);
    specifies.setLearningOpportunityInstance(learningOpportunityInstance);

    return specifies;
  }

  /**
   * Always defaults to FI.
   */
  private Issuer createIssuer(OpintosuoritusTyyppi opintosuoritus, LearnerDetailsDto details)
      throws NpcException {
    Issuer issuer = new Issuer();
    issuer.setCountry(CountryCode.FI);
    // TODO: pic/erasmus/schac	SCHAC - muodostettava palvelimella opintosuorituksen myöntäjästä
    issuer.getIdentifier().add(createIdentifier(
        "TODO:type",
        details.getSchacHomeOrganization()));

    issuer.getTitle().add(createLocalizedToken(
        CountryCode.FI,
        issuerCodeToTitle(opintosuoritus.getMyontaja())));

    // TODO
    issuer.setUrl("TODO");

    return issuer;
  }

  private String issuerCodeToTitle(String issuerCode) throws NpcException {
    // TODO: create and use mapping
    // Opintosuoritus.Myontaja is just a code which needs to be mapped to actual organization name

    String issuerName = issuerCodeToName.get(issuerCode);
    if (issuerName == null) {
      throw new NpcException(String.format("Issuer not found for issuer code:%s", issuerCode));
    }
    return issuerName;
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
}
