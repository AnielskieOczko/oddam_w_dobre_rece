package org.jankowskirafal.oddam_w_dobre_rece;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Transactional
public class AppConfig implements WebMvcConfigurer {
}
