package com.rajat.workflow.domain;

public class SleepTask implements Task{
    private final String id;
    private final String name;
    private final long sleepMillis;
    private TaskStatus status;

    public SleepTask(String id, String name, long sleepMillis) {
        this.id = id;
        this.name = name;
        this.sleepMillis = sleepMillis;
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
            System.out.println("[" + id + "] sleeping for " + sleepMillis + "ms" );
            Thread.sleep(sleepMillis);
            setStatus(TaskStatus.SUCCESS);
            long executionTime = System.currentTimeMillis() - startTime;
            return new TaskResult(id, TaskStatus.SUCCESS,sleepMillis,null,executionTime);
        }
        catch(InterruptedException e){
            setStatus(TaskStatus.FAILED);
            long executionTime = System.currentTimeMillis() - startTime;
            return new TaskResult(id, TaskStatus.FAILED, null, e.getMessage(), executionTime);
        }
    }


    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }
}
