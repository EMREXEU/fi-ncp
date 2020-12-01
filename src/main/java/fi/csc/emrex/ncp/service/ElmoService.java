package fi.csc.emrex.ncp.service;

import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.schemas.elmo.Elmo;
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
    return elmo;

  }

  public void postElmo(String elmoString, NcpRequestDto ncpRequestDto) {
    // TODO
  }
}
