package com.bala.app.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/batch-processor")
public class BatchController {

    @Autowired
    @Qualifier("batchJob")
    private Job batchJob;

    @Autowired
    @Qualifier("databaseMigrationChunkJob")
    private Job databaseMigrationChunkJob;

    @Autowired
    private JobLauncher jobLauncher;

    @PostMapping("/start")
    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public BatchStatus batchStart() throws Exception {
        Map<String, JobParameter> jobParameterMap = new HashMap<>();
        jobParameterMap.put("currentTime", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters=new JobParameters(jobParameterMap);
        JobExecution jobExecution = jobLauncher.run(batchJob, jobParameters);

        return jobExecution.getStatus();

    }

    @PostMapping("/databaseMigration/start")
    public BatchStatus databaseMigrationStart() throws Exception {
        Map<String, JobParameter> jobParameterMap = new HashMap<>();
        jobParameterMap.put("currentTime", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters=new JobParameters(jobParameterMap);
        JobExecution jobExecution = jobLauncher.run(databaseMigrationChunkJob, jobParameters);

        return jobExecution.getStatus();

    }
}
