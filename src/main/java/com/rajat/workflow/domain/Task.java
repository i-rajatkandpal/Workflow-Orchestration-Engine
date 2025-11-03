package com.rajat.workflow.domain;

public interface Task {
    String getId();
    String getName();
    TaskResult execute();
    TaskStatus getStatus();
    void setStatus(TaskStatus status);
}
