package com.bala.app.config;

import com.bala.app.chunk.JPAProcessor;
import com.bala.app.entity.postgres.Student;
import com.bala.app.tasklet.Tasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Autowired
    @Qualifier("postgresDataSource")
    private DataSource postgresDataSource;

    @Autowired
    @Qualifier("sqlDataSource")
    private DataSource sqlDataSource;

    @Autowired
    @Qualifier("postgresqlEntityManagerFactory")
    private EntityManagerFactory postgresqlEntityManagerFactory;

    @Autowired
    @Qualifier("mysqlEntityManagerFactory")
    private EntityManagerFactory mysqlEntityManagerFactory;

    @Autowired
    private JpaTransactionManager jpaTransactionManager;

    @Autowired
    private JPAProcessor jpaProcessor;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private Tasklet tasklet;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob(){
        return jobBuilderFactory
                .get("spring-batch")
                .incrementer(new RunIdIncrementer())
                .start(taskletStep())
                .build();
    }

    @Bean
    public Job databaseMigrationChunkJob(){
        return jobBuilderFactory
                .get("database-migration")
                .incrementer(new RunIdIncrementer())
                .start(databaseMigration())
                .build();
    }

    public Step taskletStep() {
        return stepBuilderFactory
                .get("step")
                .tasklet(tasklet).build();
    }

    public Step databaseMigration(){
        return stepBuilderFactory
                .get("database-migration")
                .<Student, com.bala.app.entity.sql.Student>chunk(3)
                .reader(jpaCursorItemReader())
                .processor(jpaProcessor)
                .writer(jpaItemWriter())
                .transactionManager(jpaTransactionManager)
                .build();
    }

    @Bean
    public JpaCursorItemReader<Student> jpaCursorItemReader() {
        JpaCursorItemReader<Student> jpaCursorItemReader =
                new JpaCursorItemReader<Student>();

        jpaCursorItemReader.setEntityManagerFactory(postgresqlEntityManagerFactory);

        jpaCursorItemReader.setQueryString("From Student");

        jpaCursorItemReader.setMaxItemCount(15);

        return jpaCursorItemReader;
    }

    public JpaItemWriter<com.bala.app.entity.sql.Student> jpaItemWriter() {
        JpaItemWriter<com.bala.app.entity.sql.Student> jpaItemWriter =
                new JpaItemWriter<com.bala.app.entity.sql.Student>();

        jpaItemWriter.setEntityManagerFactory(mysqlEntityManagerFactory);

        return jpaItemWriter;
    }

}
