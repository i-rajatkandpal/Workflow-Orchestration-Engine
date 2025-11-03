package com.rajat.workflow.domain;

public class TaskResult {
    private final String taskId;
    private final TaskStatus status;
    private final Object data;
    private final String errorMessage;
    private final long executionTimeMs;

    public TaskResult(String taskId, TaskStatus status, Object data, String errorMessage, long executionTimeMs) {
        this.taskId = taskId;
        this.status = status;
        this.data = data;
        this.errorMessage = errorMessage;
        this.executionTimeMs = executionTimeMs;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
}
