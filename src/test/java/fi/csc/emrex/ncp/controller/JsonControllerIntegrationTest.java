package fi.csc.emrex.ncp.controller;

import static fi.csc.emrex.ncp.controller.ThymeControllerIntegrationTest.getShibbolethAuthenticationAttributes;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class JsonControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testFetchElmoXml() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders
        .get("/elmo").sessionAttrs(getShibbolethAuthenticationAttributes()))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
