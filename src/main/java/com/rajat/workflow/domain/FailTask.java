package com.rajat.workflow.domain;

public class FailTask implements Task{
    //id , name , status
    private final String id;
    private final String name;
    private TaskStatus status;

    public FailTask(String id, String name) {
        this.id = id;
        this.name = name;
        this.status = TaskStatus.PENDING;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TaskResult execute() {
        long startTime = System.currentTimeMillis();
        setStatus(TaskStatus.RUNNING);

        System.out.println("[" + id + "] Intentionally failing");
        setStatus(TaskStatus.FAILED);
        long executionTime = System.currentTimeMillis() - startTime;
        return new TaskResult(id, TaskStatus.FAILED, null, "Task designed to fail", executionTime);
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
