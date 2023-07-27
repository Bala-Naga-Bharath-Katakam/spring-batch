package com.bala.app.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.sqldatasource")
    public DataSource sqlDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.postgresdatasource")
    public DataSource postgresDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public EntityManagerFactory postgresqlEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean lem =
                new LocalContainerEntityManagerFactoryBean();

        lem.setDataSource(postgresDataSource());
        lem.setPackagesToScan("com.bala.app.entity.postgres");
        lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        lem.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        lem.afterPropertiesSet();

        return lem.getObject();
    }

    @Bean
    public EntityManagerFactory mysqlEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean lem =
                new LocalContainerEntityManagerFactoryBean();

        lem.setDataSource(sqlDataSource());
        lem.setPackagesToScan("com.bala.app.entity.sql");
        lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        lem.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        lem.afterPropertiesSet();

        return lem.getObject();
    }

    @Bean
    @Primary
    public JpaTransactionManager jpaTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new
                JpaTransactionManager();

        jpaTransactionManager.setDataSource(sqlDataSource());
        jpaTransactionManager.setEntityManagerFactory(mysqlEntityManagerFactory());

        return jpaTransactionManager;
    }
}
