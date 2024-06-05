package fi.csc.emrex.ncp.controller.error;

import fi.csc.emrex.ncp.controller.utils.NcpSessionAttributes;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/error")
public class GlobalErrorController {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorController.class.getName());

    private final GlobalErrorHandler globalErrorHandler;

    public GlobalErrorController(GlobalErrorHandler globalErrorHandler) {
        this.globalErrorHandler = globalErrorHandler;
    }

    @PostMapping
    public void error(@RequestBody String frontendLog, HttpServletRequest context) {
        HttpSession session = context.getSession();

        OpiskelijanKaikkiTiedotResponse virtaXml = (OpiskelijanKaikkiTiedotResponse) session
                .getAttribute(NcpSessionAttributes.VIRTA_XML);

        if (virtaXml == null) {
            log.info("Virta xml is null, this could happen if frontend has failed before loading course data");
        }
        globalErrorHandler.handle(virtaXml, frontendLog);
    }
}
