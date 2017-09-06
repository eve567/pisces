package net.ufrog.pisces.service.configurations;

import net.ufrog.common.data.SpringDataConfigurations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Configuration
@EnableJpaRepositories(basePackages = "net.ufrog.pisces.domain.repositories")
@EnableTransactionManagement
public class PersistentConfiguration {

    private static final String DATASOURCE_PRIMARY  = "primary";

    @Primary
    @Bean(initMethod = "init", destroyMethod = "close")
    @SuppressWarnings("ContextJavaBeanUnresolvedMethodsInspection")
    public DataSource datasource() throws SQLException {
        return SpringDataConfigurations.druidDataSource(DATASOURCE_PRIMARY);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        return SpringDataConfigurations.hibernateJpaEntityManagerFactory(DATASOURCE_PRIMARY, dataSource, "net.ufrog.pisces.domain.models");
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return SpringDataConfigurations.jpaTransactionManager(entityManagerFactory);
    }
}
