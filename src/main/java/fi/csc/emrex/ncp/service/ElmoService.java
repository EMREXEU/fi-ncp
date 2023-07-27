package fi.csc.emrex.ncp.service;

import static fi.csc.emrex.ncp.service.ElmoXmlDefaults.DEFAULT_LEARNER_ID_TYPE;

import fi.csc.emrex.ncp.dto.IssuerDto;
import fi.csc.emrex.ncp.dto.LearnerDetailsDto;
import fi.csc.emrex.ncp.exception.NcpException;
import fi.csc.emrex.ncp.service.ElmoXmlDefaults.LOI;
import fi.csc.emrex.ncp.service.ElmoXmlDefaults.LOS;
import fi.csc.emrex.ncp.service.ElmoXmlDefaults.LOS.TYPE;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.Attachment;
import fi.csc.schemas.elmo.CountryCode;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.schemas.elmo.Elmo.Learner;
import fi.csc.schemas.elmo.Elmo.Report;
import fi.csc.schemas.elmo.Elmo.Report.Issuer;
import fi.csc.schemas.elmo.LearningOpportunitySpecification;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies.LearningOpportunityInstance;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Credit;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Level;
import fi.csc.schemas.elmo.TokenWithOptionalLang;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import mace.funet_fi.virta._2015._09._01.OpintosuorituksetTyyppi;
import mace.funet_fi.virta._2015._09._01.OpintosuoritusTyyppi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ElmoService {

  @Value("classpath:data/issuers.txt")
  Resource issuerResourceFile;

  // Line format: Korkeakoulu;TK-oppilaitoskoodi;Domain = schac
  public enum ISSUER_FILE_COLUMN {
    TITLE, CODE, DOMAIN
  }

  // Opintosuoritus.Myontaja is just a VIRTA code which needs to be mapped to
  // actual organization details
  // Key: Opintosuoritus.Myontaja
  private Map<String, IssuerDto> virtaIssuerCodeToIssuer = new HashMap<>();
  // Contains same data but mapped by SHIB_schacHomeOrganization as key
  // (IssuerDto.domain)
  private Map<String, IssuerDto> shibDomainToIssuer = new HashMap<>();

  @PostConstruct
  public void init() throws IOException {

    // Line format: Korkeakoulu;TK-oppilaitoskoodi;Domain = schac
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(issuerResourceFile.getInputStream()))) {
      reader.lines().forEach(line -> {
        if (!line.isEmpty()) {
          String[] args = line.split(";");
          IssuerDto issuer = new IssuerDto(args);
          virtaIssuerCodeToIssuer.put(issuer.getCode(), issuer);
          shibDomainToIssuer.put(issuer.getDomain(), issuer);
        }
      });
    }
  }

  public List<OpintosuoritusTyyppi> trimToSelectedCourses(List<OpintosuoritusTyyppi> opintosuoritukset,
      List<String> courseKeys) {

    OpintosuorituksetTyyppi opintosuorituksetTyyppi = new OpintosuorituksetTyyppi();
    // Initializes to empty array
    opintosuorituksetTyyppi.getOpintosuoritus();

    opintosuoritukset.forEach(course -> {
      if (courseKeys.contains(course.getAvain())) {
        opintosuorituksetTyyppi.getOpintosuoritus().add(course);
      }
    });

    return opintosuorituksetTyyppi.getOpintosuoritus();
  }

  public OpintosuorituksetResponse _trimToSelectedCourses(OpintosuorituksetResponse virtaXml, List<String> courseKeys) {

    List<OpintosuoritusTyyppi> opintosuoritukset = virtaXml.getOpintosuoritukset().getOpintosuoritus();
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

  public Elmo convertToElmoXml(List<OpintosuoritusTyyppi> filteredCourses,
      List<OpintosuoritusTyyppi> allCoursesFromSelectedIssuer, VirtaUserDto student, LearnerDetailsDto learnerDetails)
      throws NcpException {
    try {
      Elmo elmo = new Elmo();

      elmo.setLearner(createLearner(student, learnerDetails));
      elmo.setGeneratedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
      List<Elmo.Report> reports = elmo.getReport();

      reports.add(createReport(filteredCourses, allCoursesFromSelectedIssuer, learnerDetails));

      return elmo;
    } catch (DatatypeConfigurationException e) {
      throw new NcpException("Creating XMLGregorianCalendar failed.", e);
    }
  }

  public Elmo _convertToElmoXml(List<OpintosuoritusTyyppi> filteredCourses,
      List<OpintosuoritusTyyppi> allCoursesFromSelectedIssuer, VirtaUserDto student, LearnerDetailsDto learnerDetails)
      throws NcpException {

    try {
      Elmo elmo = new Elmo();
      elmo.setLearner(createLearner(student, learnerDetails));
      elmo.setGeneratedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
      List<Elmo.Report> reports = elmo.getReport();

      reports.add(createReport(filteredCourses, allCoursesFromSelectedIssuer, learnerDetails));

      return elmo;
    } catch (DatatypeConfigurationException e) {
      throw new NcpException("Creating XMLGregorianCalendar failed.", e);
    }
  }

  private Learner createLearner(VirtaUserDto student, LearnerDetailsDto details) throws NcpException {
    Elmo.Learner learner = new Elmo.Learner();

    // learner.setCitizenship(details.getCitizenship());

    Elmo.Learner.Identifier nationalIdentifier = new Elmo.Learner.Identifier();
    nationalIdentifier.setType(DEFAULT_LEARNER_ID_TYPE);
    nationalIdentifier.setValue(student.getSsn());
    learner.getIdentifier().add(nationalIdentifier);
    if (student.isOidSet()) {
      Elmo.Learner.Identifier nationalLearnerId = new Elmo.Learner.Identifier();
      nationalLearnerId.setType("nationalLearnerId");
      nationalLearnerId.setValue(student.getOid());
      learner.getIdentifier().add(nationalLearnerId);
    }

    learner.setBday(copyOf(details.getBday()));
    learner.setGender(details.getGender());
    learner.setGivenNames(details.getGivenNames());
    learner.setFamilyName(details.getFamilyName());

    return learner;
  }

  private Report createReport(List<OpintosuoritusTyyppi> selectedCourses,
      List<OpintosuoritusTyyppi> allCoursesFromSelectedIssuer, LearnerDetailsDto details)
      throws NcpException, DatatypeConfigurationException {

    // VIRTA has issuer on course level whereas ELMO has single issuer in report
    // level.
    // Result: ELMO report can only have single issuer and courses from this single
    // issuer!
    // VIRTA: OpintosuoritusTyyppi.myontaja
    // ELMO: report.issuer
    // ELMO: report.learningOpportunitySpecification
    Elmo.Report report = new Elmo.Report();

    // Only single issuer should exist for all courses
    IssuerDto issuerDto = issuerForCode(selectedCourses.stream().findFirst().get().getMyontaja());

    // Must create a copy of calendar as reference to VIRTA data seems to disappear.
    // report.setIssueDate(copyOf(opintosuoritsTyyppi.getSuoritusPvm()));
    report.setIssueDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
    report.setIssuer(createIssuer(issuerDto));

    for (OpintosuoritusTyyppi course : selectedCourses) {
      report.getLearningOpportunitySpecification()
          .add(createLearningOpportunitySpecification(course, allCoursesFromSelectedIssuer));
    }

    return report;
  }

  private LearningOpportunitySpecification createLearningOpportunitySpecification(OpintosuoritusTyyppi opintosuoritus,
      List<OpintosuoritusTyyppi> opintosuoritukset) throws NcpException {

    LearningOpportunitySpecification learningOpportunitySpecification = new LearningOpportunitySpecification();
    learningOpportunitySpecification.getIdentifier()
        .add(createLosIdentifier(LOS.ID_TYPE, opintosuoritus.getKoulutusmoduulitunniste()));
    learningOpportunitySpecification.setType(createLOSpecType(opintosuoritus));
    learningOpportunitySpecification.setSubjectArea(opintosuoritus.getKoulutuskoodi());
    learningOpportunitySpecification.setIscedCode(opintosuoritus.getKoulutuskoodi());
    learningOpportunitySpecification.setSpecifies(createSpecifies(opintosuoritus));
    createLocalizedTitles(opintosuoritus, learningOpportunitySpecification);

    if (opintosuoritus.getSisaltyvyys().toArray().length > 0) {
      opintosuoritus.getSisaltyvyys().forEach(course -> {
        OpintosuoritusTyyppi suoritus = opintosuoritukset.stream()
            .filter(c -> c.getAvain().equals(course.getSisaltyvaOpintosuoritusAvain())).findFirst().orElse(null);
        if (suoritus != null) {
          try {
            LearningOpportunitySpecification.HasPart hasPart = new LearningOpportunitySpecification.HasPart();
            hasPart.setLearningOpportunitySpecification(
                createLearningOpportunitySpecification(suoritus, opintosuoritukset));
            learningOpportunitySpecification.getHasPart().add(hasPart);
          } catch (NcpException e) {
            // TODO: handle exception
          }

        }

      });
    }
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
  private String createLOSpecType(OpintosuoritusTyyppi opintosuoritus) {

    String elmoType = TYPE.DEFAULT;

    if ("1".equalsIgnoreCase(opintosuoritus.getLaji())) {
      elmoType = TYPE.DEGREE;
    }
    if ("2".equalsIgnoreCase(opintosuoritus.getLaji()) && opintosuoritus.getSisaltyvyys().toArray().length > 0) {
      elmoType = TYPE.MODULE;
    }

    return elmoType;
  }

  private Specifies createSpecifies(OpintosuoritusTyyppi opintosuoritus) throws NcpException {
    Specifies specifies = new Specifies();
    specifies.setLearningOpportunityInstance(createLearningOpportunityInstance(opintosuoritus));
    return specifies;
  }

  private LearningOpportunityInstance createLearningOpportunityInstance(OpintosuoritusTyyppi opintosuoritus)
      throws NcpException {
    LearningOpportunityInstance learningOpportunityInstance = new LearningOpportunityInstance();
    learningOpportunityInstance.getIdentifier().add(createLoiIdentifier(LOI.ID_TYPE, opintosuoritus.getAvain()));
    learningOpportunityInstance.setDate(copyOf(opintosuoritus.getSuoritusPvm()));
    learningOpportunityInstance.setStatus(LOI.STATUS);
    learningOpportunityInstance.setResultLabel(opintosuoritus.getArvosana().getViisiportainen());
    learningOpportunityInstance.getCredit().add(createCredit(opintosuoritus));
    learningOpportunityInstance.getLevel().add(createLevel(opintosuoritus));
    learningOpportunityInstance.setLanguageOfInstruction(opintosuoritus.getKieli());
    return learningOpportunityInstance;
  }

  private LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Identifier createLoiIdentifier(
      String type, String value) {
    LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Identifier identifier = new LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Identifier();
    identifier.setType(type);
    identifier.setValue(value);
    return identifier;
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
  private Issuer createIssuer(IssuerDto issuerDto) {
    Issuer issuer = new Issuer();
    issuer.setCountry(issuerDto.getCountryCode());
    issuer.getIdentifier().add(createIdentifier(issuerDto.getIdentifierType(), issuerDto.getIdentifier()));
    issuer.getTitle().add(createLocalizedToken(issuerDto.getCountryCode(), issuerDto.getTitle()));
    issuer.setUrl(issuerDto.getUrl());
    return issuer;
  }

  public void addAttachment(Elmo elmoXml, String PDFDataURI) {
    Attachment attachment = new Attachment();
    attachment.setType("EMREX transcript");
    attachment.getContent().add(createTokenWithOptionalLang("en", PDFDataURI));
    attachment.getTitle().add(createTokenWithOptionalLang("en", "Emrex transcript"));

    elmoXml.getAttachment().add(attachment);
  }

  private TokenWithOptionalLang createTokenWithOptionalLang(String lang, String value) {
    TokenWithOptionalLang token = new TokenWithOptionalLang();
    token.setLang(lang);
    token.setValue(value);
    return token;
  }

  /**
   * @param issuerCode VIRTA XML: Opintosuoritus.Myontaja
   * @return cached Issuer details
   * @throws NcpException No issuer found for key
   */
  public IssuerDto issuerForCode(String issuerCode) throws NcpException {
    IssuerDto issuer = virtaIssuerCodeToIssuer.get(issuerCode);
    if (issuer == null) {
      throw new NcpException(String.format("Issuer not found for issuer code:%s", issuerCode));
    }
    return issuer;
  }

  /**
   * @param issuerDomain SHIBBOLETH: SHIB_schacHomeOrganization
   * @return cached Issuer details
   * @throws NcpException No issuer found for key
   */
  public IssuerDto issuerForDomain(String issuerDomain) throws NcpException {
    IssuerDto issuer = shibDomainToIssuer.get(issuerDomain);
    if (issuer == null) {
      throw new NcpException(String.format("Issuer not found for issuer code:%s", issuerDomain));
    }
    return issuer;
  }

  private Issuer.Identifier createIdentifier(String type, String value) {
    Elmo.Report.Issuer.Identifier identifier = new Issuer.Identifier();
    identifier.setType(type);
    identifier.setValue(value);
    return identifier;
  }

  private fi.csc.schemas.elmo.LearningOpportunitySpecification.Identifier createLosIdentifier(String type,
      String value) {
    fi.csc.schemas.elmo.LearningOpportunitySpecification.Identifier identifier = new fi.csc.schemas.elmo.LearningOpportunitySpecification.Identifier();
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
  private void createLocalizedTitles(OpintosuoritusTyyppi source, LearningOpportunitySpecification target) {
    // Init empty list
    target.getTitle();
    source.getNimi().forEach(name -> {
      TokenWithOptionalLang token = new TokenWithOptionalLang();
      token.setLang(name.getKieli());
      token.setValue(name.getValue());
      target.getTitle().add(token);
    });
  }


  /**
   * From some reason setting existing protected XMLGregorianCalendar
   * copyOf(XMLGregorianCalendar source) throws NcpException { entry to target XML
   * will be empty -> create copy instead
   *
   * @param source original XML entry which will not exist afterwards
   * @return copy of source or null if source null
   */
  protected XMLGregorianCalendar copyOf(XMLGregorianCalendar source) throws NcpException {
    try {
      XMLGregorianCalendar cal = null;
      if (source != null) {
        cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(source.toGregorianCalendar());
      }
      return cal;
    } catch (DatatypeConfigurationException e) {
      throw new NcpException("Creating XMLGregorianCalendar failed.", e);
    }
  }

  public Map<String, IssuerDto> getVirtaIssuerCodeToIssuer() {
    return virtaIssuerCodeToIssuer;
  }

}
