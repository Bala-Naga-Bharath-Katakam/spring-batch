package com.bala.app.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class Tasklet implements org.springframework.batch.core.step.tasklet.Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("Executing first tasklet step...");
        System.out.println("SEC = " + chunkContext.getStepContext().getStepExecutionContext());
        return RepeatStatus.FINISHED;
    }
}
