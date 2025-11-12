package com.rajat.workflow.domain;

import java.util.function.Supplier;

public class ConditionalTask implements Task {

    private final String id;
    private final String name;
    private final Supplier<Boolean> condition;
    private final String message;
    private TaskStatus status;

    public ConditionalTask(String id, String name, Supplier<Boolean> condition, String message) {
        this.id = id;
        this.name = name;
        this.condition = condition;
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
    public TaskResult execute() {
        long startTime = System.currentTimeMillis();
        setStatus(TaskStatus.RUNNING);

        try {
            boolean conditionMet = condition.get();

            if (conditionMet) {
                System.out.println("[" + id + "] Condition met: " + message);
                setStatus(TaskStatus.SUCCESS);
                long executionTime = System.currentTimeMillis() - startTime;
                return new TaskResult(id, TaskStatus.SUCCESS, true, null, executionTime);
            } else {
                System.out.println("[" + id + "] Condition not met, skipping");
                setStatus(TaskStatus.SKIPPED);
                long executionTime = System.currentTimeMillis() - startTime;
                return new TaskResult(id, TaskStatus.SKIPPED, false,
                        "Condition not met", executionTime);
            }

        } catch (Exception e) {
            System.out.println("[" + id + "] Error evaluating condition: " + e.getMessage());
            setStatus(TaskStatus.FAILED);
            long executionTime = System.currentTimeMillis() - startTime;
            return new TaskResult(id, TaskStatus.FAILED, null, e.getMessage(), executionTime);
        }
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