package fi.csc.emrex.ncp;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AjpNioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AJPConnector {
  @Value("${ajp.secret}")
  private String SECRET;

  @Bean
  public TomcatServletWebServerFactory servletContainer() {
    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
    Connector ajpConnector = new Connector("org.apache.coyote.ajp.AjpNioProtocol");
    AjpNioProtocol protocol = (AjpNioProtocol) ajpConnector.getProtocolHandler();
    protocol.setSecret(SECRET);
    ajpConnector.setPort(9090);
    ajpConnector.setSecure(true);
    ajpConnector.setProperty("allowedRequestAttributesPattern", ".*");
    ajpConnector.setURIEncoding("utf-8");
    tomcat.addAdditionalTomcatConnectors(ajpConnector);
    return tomcat;
  }
}
