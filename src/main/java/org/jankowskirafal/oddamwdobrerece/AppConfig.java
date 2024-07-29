package org.jankowskirafal.oddamwdobrerece;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jankowskirafal.oddamwdobrerece.users.AuthorityConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Transactional
@AllArgsConstructor
public class AppConfig implements WebMvcConfigurer {

    private AuthorityConverter authorityConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(authorityConverter);
    }
}
