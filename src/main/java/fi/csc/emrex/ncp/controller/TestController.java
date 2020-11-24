/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.emrex.ncp.execption.NpcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/test")
public class TestController extends NcpControllerBase {

  @Autowired
  private ThymeController thymeController;

  /**
   * Debug endpoint to emulate session parameters by giving them in request parameter.
   */
  @Deprecated
  @RequestMapping(value = "/ncp_mock_shibboleth", method = RequestMethod.POST)
  public String getCoursesMockShibboleth(
      @ModelAttribute NcpRequestDto request,
      @RequestParam("unique-id") String personId,
      @RequestParam("SHIB_funetEduPersonLearnerId") String learnerId) throws NpcException {
    return thymeController.getCourses(request, personId, learnerId);
  }
}
