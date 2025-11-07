package com.rajat.workflow.engine;

import com.rajat.workflow.domain.TaskStatus;
import com.rajat.workflow.domain.TaskResult;

import java.util.*;

public class WorkflowResult {
    private final WorkflowStatus status;
    private final Map<String, TaskResult> taskResults;
    private final long executionTimeMs;
    private final int totalTasks;
    private final int successfulTasks;
    private final int skippedTasks;
    private final int failedTasks;

    public WorkflowResult(WorkflowStatus status, Map<String, TaskResult> taskResults, long executionTimeMs){
        this.status = status;
        this.taskResults = new HashMap<>(taskResults);
        this.executionTimeMs = executionTimeMs;
        this.totalTasks = taskResults.size();

        int success = 0, failed = 0 , skipped = 0;
        for(TaskResult result : taskResults.values()){
            if(result.getStatus() == TaskStatus.SUCCESS) success++;
            else if(result.getStatus() == TaskStatus.FAILED) failed++;
            else if(result.getStatus() == TaskStatus.SKIPPED) skipped++;
        }
        this.successfulTasks = success;
        this.failedTasks = failed;
        this.skippedTasks = skipped;

    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public Map<String, TaskResult> getTaskResults() {
        return new HashMap<>(taskResults);
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getSuccessfulTasks() {
        return successfulTasks;
    }

    public int getSkippedTasks() {
        return skippedTasks;
    }

    public int getFailedTasks() {
        return failedTasks;
    }

    public TaskResult getTaskResult(String taskId){
        return taskResults.get(taskId);
    }
}
