package org.jankowskirafal.oddam_w_dobre_rece.donations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@Slf4j
public class DonationViewController {

    @GetMapping("/home")
    public String displayHomePage() {
        log.info("Displaying home page");
        return "index";
    }

}
