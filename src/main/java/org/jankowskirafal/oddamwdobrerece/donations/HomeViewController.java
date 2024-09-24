package org.jankowskirafal.oddamwdobrerece.donations;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jankowskirafal.oddamwdobrerece.dtos.HomePageDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@Slf4j
@AllArgsConstructor
public class HomeViewController {

    private final DonationService donationServiceImpl;

    @GetMapping()
    public String displayHomePage(Model model) {
        log.info("Displaying home page");


        HomePageDto homePageDto = donationServiceImpl.getDataForHomePage();
        model.addAttribute("homePageDto", homePageDto);

        return "home";
    }

}
