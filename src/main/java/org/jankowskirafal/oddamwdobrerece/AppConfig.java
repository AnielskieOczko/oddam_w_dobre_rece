package org.jankowskirafal.oddamwdobrerece;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jankowskirafal.oddamwdobrerece.users.AuthorityConverter;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
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


    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages"); // Base name of your properties files
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;

    }
}