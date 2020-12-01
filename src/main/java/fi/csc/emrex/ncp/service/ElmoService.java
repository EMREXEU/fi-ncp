package fi.csc.emrex.ncp.service;

import fi.csc.schemas.elmo.Elmo;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ElmoService {

  public OpintosuorituksetResponse trimToSelectedCourses(
      OpintosuorituksetResponse virtaXml,
      List<String> courseList) {
    // TODO
    return virtaXml;
  }

  public Elmo convertToElmoXml(OpintosuorituksetResponse virtaXml) {
    Elmo elmo = new Elmo();
    // TODO
    return elmo;

  }
}
