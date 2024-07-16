package org.jankowskirafal.oddam_w_dobre_rece;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class ControllerTest {

    @GetMapping
    public String serveHome() {
        return "index";
    }
}
