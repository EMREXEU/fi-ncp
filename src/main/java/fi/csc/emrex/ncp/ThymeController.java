/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp;

import static fi.csc.emrex.ncp.FiNcpApplication.getElmo;
import static fi.csc.emrex.ncp.FiNcpApplication.getElmoRemote;
import fi.csc.emrex.ncp.elmo.ElmoParser;

import java.time.LocalDate;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;

import fi.csc.emrex.ncp.virta.Gender;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Base64;
import java.util.List;

/**
 *
 * @author salum
 */
@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class
})
@Controller
@Slf4j
public class ThymeController {

    @Autowired
    private HttpServletRequest context;

    @Autowired
    private VirtaClient virtaClient;

    // function for local testing
    @RequestMapping(value = "/ncp/review", method = RequestMethod.GET)
    public String ncpReview(@RequestParam(value = "courses", required = false) String[] courses,
            Model model) throws Exception {
        return this.review(courses, model);
    }

    @RequestMapping(value = "/review", method = RequestMethod.GET)
    public String review(@RequestParam(value = "courses", required = false) String[] courses,
            Model model) throws Exception {
        System.out.println("/review");

        model.addAttribute("sessionId", context.getSession().getAttribute("sessionId"));
        model.addAttribute("returnUrl", context.getSession().getAttribute("returnUrl"));
        ElmoParser parser = (ElmoParser) context.getSession().getAttribute("elmo");
        String xmlString;

        if (courses != null && courses.length > 0) {
            List<String> courseList = Arrays.asList(courses);
            xmlString = parser.getCourseData(courseList);
        } else {
            xmlString = parser.getCourseData();
        }
        final String encodedXml = Base64.getEncoder().encodeToString(xmlString.getBytes());
        model.addAttribute("elmo", encodedXml);
        model.addAttribute("buttonText", "Approve courses");
        return "review";
    }

    @RequestMapping(value = "/ncp/abort", method = RequestMethod.GET)
    public String smpabort(Model model) {
        return abort(model);

    }

    @RequestMapping(value = "/abort", method = RequestMethod.GET)
    public String abort(Model model) {
        model.addAttribute("sessionId", context.getSession().getAttribute("sessionId"));
        model.addAttribute("returnUrl", context.getSession().getAttribute("returnUrl"));
        model.addAttribute("elmo", "");
        model.addAttribute("buttonText", "Stop course transfer");
        return "review";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String ncp1(@ModelAttribute CustomRequest request) {
        return this.greeting(request);
    }

    @RequestMapping(value = "/ncp/", method = RequestMethod.POST)
    public String greeting(@ModelAttribute CustomRequest request) {

        System.out.println("/ncp/");

        context.getSession().setAttribute("sessionId", request.getSessionId());
        context.getSession().setAttribute("returnUrl", request.getReturnUrl());
        System.out.println("Return URL: " + context.getSession().getAttribute("returnUrl"));
        System.out.println("Session ID: " + context.getSession().getAttribute("sessionId"));
        try {

            String user = "";
            String elmoXML = getXMLFromVirta(user);

            ElmoParser parser = new ElmoParser(elmoXML);
            context.getSession().setAttribute("elmo", parser);
            return "norex";

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "norex";
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        return "test";
    }

    private String getXMLFromVirta(String user) throws Exception {

        final String xml = virtaClient.fetchStudies(createVirtaUser());
        log.info("Elmo XML: {}", xml);

        //PLACEHOLDER FOR VIRTA REQUEST
        return getElmoRemote();
    }


    private VirtaUser createVirtaUser() {
        // TODO tänne oikeat hakuehdot
        return new VirtaUser("Kaisa", "Keränen", Gender.FEMALE, LocalDate.of(1966, 7, 18));
    }

}
