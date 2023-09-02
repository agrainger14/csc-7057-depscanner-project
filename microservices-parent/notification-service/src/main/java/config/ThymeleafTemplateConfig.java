package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

/**
 * Configuration class for Thymeleaf templates used in the application.
 */
@Configuration
public class ThymeleafTemplateConfig {

    /**
     * Creates and configures a SpringTemplateEngine for processing Thymeleaf templates.
     *
     * @return The configured SpringTemplateEngine instance.
     */
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.addTemplateResolver(emailTemplateResolver());
        return springTemplateEngine;
    }

    /**
     * Configures a ClassLoaderTemplateResolver for resolving email related ThymeLeaf templates.
     *
     * @return The configured ClassLoaderTemplateResolver instance.
     */
    @Bean
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix("/templates/layouts/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
        emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        emailTemplateResolver.setCacheable(false);
        return emailTemplateResolver;
    }
}
