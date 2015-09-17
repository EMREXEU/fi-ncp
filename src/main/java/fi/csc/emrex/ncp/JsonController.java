/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp;

import static fi.csc.emrex.ncp.FiNcpApplication.getElmo;
import fi.csc.emrex.ncp.elmo.ElmoParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author salum
 */
@RestController
public class JsonController {

    @Autowired
    private HttpServletRequest context;

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json;charset=UTF-8", headers = "Accept=*")
    public @ResponseBody
    Map<String, Object> test() {

        System.out.println("Login");
        Map<String, Object> model = new HashMap<>();
        model.put("id", "zzz");
        model.put("content", "Oh well");
        return model;
    }

    @RequestMapping(value = "/elmo", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> fetchElmoXml() throws Exception {

        System.out.println("elmo");
        Map<String, Object> model = new HashMap<>();
        model.put("returnUrl", context.getSession().getAttribute("returnUrl"));
        model.put("sessionId", context.getSession().getAttribute("sessionId"));
        model.put("elmoXml", getElmo());
        return model;
    }

    @RequestMapping(value = "/ncp/api/elmo", method = RequestMethod.GET)
    @ResponseBody
    public String npcGetElmoJSON(@RequestParam(value = "courses", required = false) String[] courses) throws Exception {
            return this.getElmoJSON(courses);
    }

    @RequestMapping(value = "/api/elmo", method = RequestMethod.GET)
    @ResponseBody
    public String getElmoJSON(
            @RequestParam(value = "courses", required = false) String[] courses) throws Exception {
        System.out.println("/api/elmo");
        try {

            String elmo = (String) context.getSession().getAttribute("elmo");
            ElmoParser parser = new ElmoParser(elmo);
            String xmlString;
            if (courses == null || courses.length < 1) {
                System.out.println("null courses");

                xmlString = parser.getCourseData();

            } else {

                List<String> courseList = Arrays.asList(courses);
                System.out.println("courses count: " + courseList.size());
                xmlString = parser.getCourseData(courseList);
                //  System.out.println(xmlString);
            }

            JSONObject json = XML.toJSONObject(xmlString);
            //System.out.println(json.toString());
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

        System.out.println("Here we go again");
        Map<String, Object> model = new HashMap<>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }

}
