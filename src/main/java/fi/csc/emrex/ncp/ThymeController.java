/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fi.csc.emrex.ncp;

import static fi.csc.emrex.ncp.FiNcpApplication.getElmo;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Base64;

/**
 *
 * @author salum
 */

@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class
})
@Controller
public class ThymeController {
    
    @Autowired
    private HttpServletRequest context;
    
    @RequestMapping("/thyme")
    String thyme(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model){
        System.out.println("thyme");
        System.out.println("Return URL:"+context.getSession().getAttribute("returnUrl"));
        model.addAttribute("name", name);
        return "thyme";
    }
        
    @RequestMapping(value="/norex", method= RequestMethod.POST)
    public String greeting(@ModelAttribute CustomRequest request) {

        System.out.println("norex");

        context.getSession().setAttribute("sessionId", request.getSessionId());
        context.getSession().setAttribute("returnUrl", request.getReturnUrl());
        System.out.println("Return URL: " + context.getSession().getAttribute("returnUrl"));
        System.out.println("Session ID: " + context.getSession().getAttribute("sessionId"));
        try{
        if ( hakaLogin()){
            String user="";
            String elmoXML = getXMLFromVirta(user);
            context.getSession().setAttribute("elmo", elmoXML);
            return "norex";
        }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return "norex";
    }
    
    @RequestMapping(value="/doLogin", method=RequestMethod.POST)
    public String doLogin(@ModelAttribute User user, Model model) throws Exception {
        System.out.println("userName: " + user.getName());
        System.out.println("password: " + user.getPassword());
        System.out.println("Return URL:"+context.getSession().getAttribute("returnUrl"));
        model.addAttribute("sessionId", context.getSession().getAttribute("sessionId"));
        model.addAttribute("returnUrl", context.getSession().getAttribute("returnUrl"));

      
        //model.addAttribute("elmo", encodedXml);
        
        return "doLogin";
        
    }
    @RequestMapping(value="/test", method=RequestMethod.GET)
    public String test(){
        return "test";
    }

    private boolean hakaLogin() {
        //TODO
        return true;
  
    }

    private String getXMLFromVirta(String user) throws Exception{
          //final String encodedXml = Base64.getEncoder().encodeToString(getElmo().getBytes());
          return getElmo();
    }
    

}
