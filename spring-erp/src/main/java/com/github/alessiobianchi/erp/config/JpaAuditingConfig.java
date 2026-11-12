package com.github.alessiobianchi.erp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// Separate from ErpSpringApplication so @WebMvcTest slices (no JPA) don't try to start auditing.
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
