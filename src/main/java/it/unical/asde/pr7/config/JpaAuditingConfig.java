package it.unical.asde.pr7.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    class AuditorAwareImpl implements AuditorAware {
        @Override
        public Optional getCurrentAuditor() {
            return Optional.empty();
        }
    }
}
