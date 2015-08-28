package fi.csc.emrex.ncp;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@RestController
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class
})
public class FiNcpApplication {

    @Autowired
    private HttpServletRequest context;

    @RequestMapping("/resource")
    public Map<String,Object> home() {

        System.out.println("Here we go again");

        Map<String,Object> model = new HashMap<>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }

    @RequestMapping(value="/norex", method= RequestMethod.POST)
    @ResponseBody
    public String greeting(@ModelAttribute CustomRequest request) {

        System.out.println("norex");

        context.getSession().setAttribute("sessionId", request.getSessionId());
        context.getSession().setAttribute("returnUrl", request.getReturnUrl());
        System.out.println("Return URL: " + context.getSession().getAttribute("returnUrl"));
        System.out.println("Session ID: " + context.getSession().getAttribute("sessionId"));

        // TODO redirect fiksummin
        String html ="<html><head>"
                + "<meta http-equiv=\"refresh\" content=\"0; url=http://localhost:8080/#/login\" />"
                + "</head><body/><html/>";
        return html;
    }

    @RequestMapping(value="/doLogin", method=RequestMethod.POST)
    @ResponseBody
    public String doLogin(@ModelAttribute User user) throws Exception {
        System.out.println("userName: " + user.getName());
        System.out.println("password: " + user.getPassword());

        // TODO redirect fiksummin
        String form = "<form action=\"" + context.getSession().getAttribute("returnUrl") +
                "\" method=\"POST\">\n" +
                "<input type=\"hidden\" name=\"sessionId\" value=\"" + context.getSession().getAttribute("sessionId") +
                "\">\n" +
                "  <input type=\"hidden\" name=\"elmo\" value=\""+ getElmo()  + "\"/><input type=\"submit\"/></form>\n";
        System.out.println(form);
        return form;
    }

    @RequestMapping(value="/elmo", method=RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> fetchElmoXml() throws Exception {

        System.out.println("elmo");

        Map<String,Object> model = new HashMap<>();
        model.put("returnUrl", context.getSession().getAttribute("returnUrl"));
        model.put("sessionId", context.getSession().getAttribute("sessionId"));
        model.put("elmoXml", getElmo());
        return model;
    }

    private String getElmo() throws Exception{
        return StringEscapeUtils.escapeHtml(
                new String(Files.readAllBytes(
                        Paths.get(new File("src/main/resources/Example-elmo-Finland.xml").getAbsolutePath()))));
    }

    @RequestMapping(value="/login", method= RequestMethod.GET, produces ="application/json;charset=UTF-8", headers="Accept=*")
    public @ResponseBody Map<String,Object> test() {

        System.out.println("Login");

        Map<String,Object> model = new HashMap<>();
        model.put("id", "zzz");
        model.put("content", "Oh well");
        return model;
    }

    public static void main(String[] args) {
        SpringApplication.run(FiNcpApplication.class, args);
    }
}
