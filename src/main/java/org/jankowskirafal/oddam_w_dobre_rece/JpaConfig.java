package org.jankowskirafal.oddam_w_dobre_rece;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement // Enable declarative transaction management
@EnableJpaRepositories(basePackages = "org.jankowskirafal.oddam_w_dobre_rece")
@EntityScan(basePackages = "org.jankowskirafal.oddam_w_dobre_rece")
public class JpaConfig {

}
