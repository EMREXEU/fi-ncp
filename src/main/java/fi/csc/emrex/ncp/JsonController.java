/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fi.csc.emrex.ncp;

import static fi.csc.emrex.ncp.FiNcpApplication.getElmo;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author salum
 */
@RestController
public class JsonController {
    @Autowired
    private HttpServletRequest context;
        
    @RequestMapping(value="/login", method= RequestMethod.GET, produces ="application/json;charset=UTF-8", headers="Accept=*")
    public @ResponseBody Map<String,Object> test() {

        System.out.println("Login");
System.out.println("Return URL:"+context.getSession().getAttribute("returnUrl"));
        Map<String,Object> model = new HashMap<>();
        model.put("id", "zzz");
        model.put("content", "Oh well");
        return model;
    }
    @RequestMapping(value="/elmo", method=RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> fetchElmoXml() throws Exception {

        System.out.println("elmo");
System.out.println("Return URL:"+context.getSession().getAttribute("returnUrl"));
        Map<String,Object> model = new HashMap<>();
        model.put("returnUrl", context.getSession().getAttribute("returnUrl"));
        model.put("sessionId", context.getSession().getAttribute("sessionId"));
        model.put("elmoXml", getElmo());
        return model;
    }
    @RequestMapping("/resource")
    public Map<String,Object> home() {

        System.out.println("Here we go again");
System.out.println("Return URL:"+context.getSession().getAttribute("returnUrl"));
        Map<String,Object> model = new HashMap<>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }


}
