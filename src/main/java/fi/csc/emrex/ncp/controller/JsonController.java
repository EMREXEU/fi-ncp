/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.elmo.ElmoParser;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
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
public class JsonController {

  @Autowired
  private HttpServletRequest context;

  @Autowired
  private VirtaClient virtaClient;

  @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json;charset=UTF-8", headers = "Accept=*")
  public @ResponseBody
  Map<String, Object> test() {

    log.info("Login");
    Map<String, Object> model = new HashMap<>();
    model.put("id", "zzz");
    model.put("content", "Oh well");
    return model;
  }

  @RequestMapping(value = "/elmo", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> fetchElmoXml() throws Exception {

    log.info("elmo");
    Map<String, Object> model = new HashMap<>();
    model.put("returnUrl", context.getSession().getAttribute("returnUrl"));
    model.put("sessionId", context.getSession().getAttribute("sessionId"));
    // TODO oikeat hakuehdot
    VirtaUserDto virtaUserDto = new VirtaUserDto("17488477125", null);
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
      @RequestParam(value = "courses", required = false) String[] courses) throws Exception {
    log.info("/api/elmo");
    if (courses != null) {
      for (int i = 0; i < courses.length; i++) {
        System.out.print(courses[i] + ", ");

      }
      log.info("");
    }
    try {

      ElmoParser parser = (ElmoParser) context.getSession().getAttribute("elmo");
      String xmlString;
      if (courses != null && courses.length > 0) {
        log.info("courses count: " + courses.length);
        List<String> courseList = Arrays.asList(courses);
        xmlString = parser.getCourseData(courseList);
      } else {
        log.info("null courses");
        xmlString = parser.getCourseData();
      }

      JSONObject json = XML.toJSONObject(xmlString);
      //log.info(json.toString());
      return json.toString();
    } catch (Exception e) {

      StackTraceElement elements[] = e.getStackTrace();
      Map<String, Object> error = new HashMap<String, Object>();
      Map<String, Object> log = new HashMap<String, Object>();
      error.put("message", e.getMessage());
      for (int i = 0, n = elements.length; i < n; i++) {
        log.put(elements[i].getFileName() + " " + elements[i].getLineNumber(),
            elements[i].getMethodName());
      }
      error.put("stack", log);
      return new JSONObject(error).toString();
    }
  }

  @RequestMapping("/resource")
  public Map<String, Object> home() {

    log.info("Here we go again");
    Map<String, Object> model = new HashMap<>();
    model.put("id", UUID.randomUUID().toString());
    model.put("content", "Hello World");
    return model;
  }

}
