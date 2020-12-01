package fi.csc.emrex.ncp.service;

import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.schemas.elmo.CountryCode;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.schemas.elmo.Elmo.Report.Issuer;
import fi.csc.schemas.elmo.LearningOpportunitySpecification;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies.LearningOpportunityInstance;
import fi.csc.schemas.elmo.LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Identifier;
import fi.csc.schemas.elmo.TokenWithOptionalLang;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mace.funet_fi.virta._2015._09._01.OpintosuorituksetTyyppi;
import mace.funet_fi.virta._2015._09._01.OpintosuoritusTyyppi;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ElmoService {

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

  public Elmo convertToElmoXml(OpintosuorituksetResponse virtaXml) {
    Elmo elmo = new Elmo();
    // TODO

    Elmo.Learner learner = new Elmo.Learner();
    Elmo.Learner.Identifier identifier = new Elmo.Learner.Identifier();
    identifier.setType("nationalIdentifier");
    identifier.setValue("TODO");
    learner.getIdentifier().add(identifier);
    elmo.setLearner(learner);

    List<Elmo.Report> reports = elmo.getReport();
    virtaXml.getOpintosuoritukset().getOpintosuoritus().forEach(opintosuoritus -> {
      Elmo.Report report = new Elmo.Report();
      List<LearningOpportunitySpecification> learningOpportunitySpecifications
          = report.getLearningOpportunitySpecification();

      LearningOpportunitySpecification learningOpportunitySpecification = new LearningOpportunitySpecification();
      learningOpportunitySpecification.setType(opintosuoritus.getKoulutusmoduulitunniste());
      learningOpportunitySpecification.setSubjectArea(opintosuoritus.getKoulutuskoodi());
      learningOpportunitySpecification.setIscedCode(opintosuoritus.getKoulutuskoodi());
      learningOpportunitySpecification.setSpecifies(createSpecifies(opintosuoritus));

      report.setIssueDate(opintosuoritus.getSuoritusPvm());
      report.setIssuer(createIssuer());

      createLocalizedTitles(opintosuoritus, learningOpportunitySpecification);

      learningOpportunitySpecifications.add(learningOpportunitySpecification);
      reports.add(report);
    });

    return elmo;

  }

  private Specifies createSpecifies(OpintosuoritusTyyppi opintosuoritus) {
    Specifies specifies = new Specifies();
    LearningOpportunityInstance learningOpportunityInstance = new LearningOpportunityInstance();
    LearningOpportunitySpecification.Specifies.LearningOpportunityInstance.Identifier identifier = new Identifier();
    if (opintosuoritus.getSisaltyvyys().size() > 0) {
      // TODO: assuming use first found ok?
      identifier.setType(opintosuoritus.getSisaltyvyys().get(0).getSisaltyvaOpintosuoritusAvain());
    }
    learningOpportunityInstance.getIdentifier().add(identifier);
    specifies.setLearningOpportunityInstance(learningOpportunityInstance);
    return specifies;
  }

  /**
   * Always defaults to FI.
   */
  private Issuer createIssuer() {
    Issuer issuer = new Issuer();
    issuer.setCountry(CountryCode.FI);
    return issuer;
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
