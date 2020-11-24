/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.controller;


import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author salum
 */
@RestController
@Slf4j
public class JsonController extends NcpControllerBase {

  // TODO: remove
  private final static String SSN = "010280-123A";

  @Autowired
  private HttpServletRequest context;
  @Autowired
  private VirtaClient virtaClient;


  @RequestMapping(value = "/elmo", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> fetchElmoXml() throws NpcException {

    log.info("elmo");
    HttpSession session = context.getSession();

    Map<String, Object> model = new HashMap<>();
    model.put(
        NcpSessionAttributes.RETURN_URL,
        session.getAttribute(NcpSessionAttributes.RETURN_URL));
    model.put(
        NcpSessionAttributes.SESSION_ID,
        session.getAttribute(NcpSessionAttributes.SESSION_ID));

    // TODO oikeat hakuehdot
    VirtaUserDto virtaUserDto = new VirtaUserDto(
        //"17488477125",
        null,
        SSN);
    model.put("elmoXml", virtaClient.fetchStudies(virtaUserDto));
    return model;
  }

  @RequestMapping(value = "/ncp/api/elmo", method = RequestMethod.GET)
  @ResponseBody
  public String npcGetElmoJSON(@RequestParam(value = "courses", required = false) String[] courses)
      throws Exception {
    log.info("/ncp/api/elmo");
    if (courses != null) {
      log.info("courses.length=" + courses.length);
      for (int i = 0; i < courses.length; i++) {
        System.out.print(courses[i] + ", ");

      }
      log.info("");
    }
    return this.getElmoJSON(courses);
  }

  @RequestMapping(value = "/api/elmo", method = RequestMethod.GET)
  @ResponseBody
  public String getElmoJSON(
      @RequestParam(value = "courses", required = false) String[] courses) throws NpcException {

    log.info("/api/elmo");
    HttpSession session = context.getSession();

    OpintosuorituksetResponse virtaXml = (OpintosuorituksetResponse) session
        .getAttribute(NcpSessionAttributes.VIRTA_XML);
    String xmlString;
    if (courses != null && courses.length > 0) {
      log.info("courses count: " + courses.length);
      List<String> courseList = Arrays.asList(courses);
      // TODO: convert to ELMO
      xmlString = XmlUtil.toString(virtaXml);
    } else {
      // TODO: selected courses, convert to ELMO
      log.info("null courses");
      xmlString = XmlUtil.toString(virtaXml);
    }

    JSONObject json = XML.toJSONObject(xmlString);
    //log.info(json.toString());
    return json.toString();

  }

  // TODO: is this really needed?
  @Deprecated
  @RequestMapping("/resource")
  public Map<String, Object> home() {

    log.info("Here we go again");
    Map<String, Object> model = new HashMap<>();
    model.put("id", UUID.randomUUID().toString());
    model.put("content", "Hello World");
    return model;
  }

  @Deprecated
  @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json;charset=UTF-8", headers = "Accept=*")
  public @ResponseBody
  Map<String, Object> test() {

    log.info("Login");
    Map<String, Object> model = new HashMap<>();
    model.put("id", "zzz");
    model.put("content", "Oh well");
    return model;
  }
}
