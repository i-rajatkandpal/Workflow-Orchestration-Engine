package com.rajat.workflow.domain;

public class MathTask implements Task {

    public enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    private final String id;
    private final String name;
    private final double operand1;
    private final double operand2;
    private final Operation operation;
    private TaskStatus status;

    public MathTask(String id, String name, double operand1, double operand2, Operation operation) {
        this.id = id;
        this.name = name;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operation = operation;
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
            double result = switch (operation) {
                case ADD -> operand1 + operand2;
                case SUBTRACT -> operand1 - operand2;
                case MULTIPLY -> operand1 * operand2;
                case DIVIDE -> {
                    if (operand2 == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    yield operand1 / operand2;
                }
            };

            System.out.println("[" + id + "] " + operand1 + " " +
                    getOperationSymbol() + " " + operand2 + " = " + result);

            setStatus(TaskStatus.SUCCESS);
            long executionTime = System.currentTimeMillis() - startTime;
            return new TaskResult(id, TaskStatus.SUCCESS, result, null, executionTime);

        } catch (ArithmeticException e) {
            System.out.println("[" + id + "] Math error: " + e.getMessage());
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

    private String getOperationSymbol() {
        return switch (operation) {
            case ADD -> "+";
            case SUBTRACT -> "-";
            case MULTIPLY -> "*";
            case DIVIDE -> "/";
        };
    }
}