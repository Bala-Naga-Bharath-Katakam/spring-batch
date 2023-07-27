package com.bala.app.config;

import com.bala.app.entity.StudentCsv;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

@Configuration
public class CSVFileBatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("sqlDataSource")
    private DataSource sqlDataSource;

    @Bean
    public Job csvBatchProcessor(){
        return jobBuilderFactory
                .get("csv-batch-processor")
                .start(csvBatchProcessing())
                .build();
    }

    public Step csvBatchProcessing() {
        return stepBuilderFactory
                .get("csv-batch-processing")
                .<StudentCsv,StudentCsv>chunk(3)
                .reader(fileReader())
                .writer(jdbcBatchItemWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<StudentCsv> fileReader(){
        FlatFileItemReader<StudentCsv> flatFileItemReader=new FlatFileItemReader<>();

        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/students.csv"));

        DefaultLineMapper<StudentCsv> defaultLineMapper = new DefaultLineMapper<>();

        BeanWrapperFieldSetMapper<StudentCsv> beanWrapperFieldSetMapper=new BeanWrapperFieldSetMapper<StudentCsv>();
        beanWrapperFieldSetMapper.setTargetType(StudentCsv.class);

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();

        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setNames("ID","First Name","Last Name","Email");

        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);


        flatFileItemReader.setLineMapper(defaultLineMapper);
        flatFileItemReader.setLinesToSkip(1);

        return flatFileItemReader;
    }

    @Bean
    public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter =
                new JdbcBatchItemWriter<StudentCsv>();

        jdbcBatchItemWriter.setDataSource(sqlDataSource);
        jdbcBatchItemWriter.setSql(
                "insert into studentcsv(id, first_name, last_name, email) "
                        + "values (:id, :firstName, :lastName, :email)");

        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(
                new BeanPropertyItemSqlParameterSourceProvider<StudentCsv>());

        return jdbcBatchItemWriter;
    }
}
