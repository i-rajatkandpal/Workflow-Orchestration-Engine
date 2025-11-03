package com.rajat.workflow.domain;

public class PrintTask implements Task{
    private final String id;
    private final String name;
    private final String message;
    private TaskStatus status;

    public PrintTask(String id, String name, String message) {
        this.id = id;
        this.name = name;
        this.message = message;
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
    public TaskResult execute(){
        long startTime = System.currentTimeMillis();
        setStatus(TaskStatus.RUNNING);

        try{
            System.out.println("[" + id + "] " + message);
            setStatus(TaskStatus.SUCCESS);
            long executionTime = System.currentTimeMillis() - startTime;
            return new TaskResult(id,TaskStatus.SUCCESS, message, null, executionTime);
        }
        catch (Exception e){
            setStatus(TaskStatus.FAILED);
            long executionTime = System.currentTimeMillis() - startTime;
            return new TaskResult(id, TaskStatus.FAILED, null, e.getMessage(), executionTime);
        }
    }

    @Override
    public TaskStatus getStatus(){
        return status;
    }

    @Override
    public void setStatus(TaskStatus status){
        this.status = status;
    }
}
