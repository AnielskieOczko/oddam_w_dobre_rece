package org.jankowskirafal.oddamwdobrerece;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement // Enable declarative transaction management
@EnableJpaRepositories(basePackages = "org.jankowskirafal.oddamwdobrerece")
@EntityScan(basePackages = "org.jankowskirafal.oddamwdobrerece")
public class JpaConfig {

}
