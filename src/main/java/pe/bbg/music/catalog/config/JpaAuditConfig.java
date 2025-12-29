package pe.bbg.music.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import pe.bbg.music.catalog.security.SecurityUtils;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(SecurityUtils.getCurrentUser());
    }
}
