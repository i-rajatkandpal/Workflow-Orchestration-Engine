package com.rajat.workflow.engine;

import com.rajat.workflow.domain.*;
import com.rajat.workflow.graph.DAG;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkflowEngineTest {

    @Test
    void testAllTasksSucceed() {
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A", "A", "A"));
        dag.addTask(new PrintTask("B", "B", "B"));
        dag.addTask(new PrintTask("C", "C", "C"));

        dag.addDependencies("B", "A");
        dag.addDependencies("C", "B");

        WorkflowEngine engine = new WorkflowEngine();
        WorkflowResult result = engine.execute(dag);

        assertEquals(WorkflowStatus.SUCCESS, result.getStatus());
        assertEquals(3, result.getSuccessfulTasks());
        assertEquals(0, result.getFailedTasks());
        assertEquals(0, result.getSkippedTasks());
    }

    @Test
    void testTaskFailureCascades() {
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A", "A", "A"));
        dag.addTask(new FailTask("B", "B"));  // Fails
        dag.addTask(new PrintTask("C", "C", "C"));

        dag.addDependencies("C", "B");  // C depends on B

        WorkflowEngine engine = new WorkflowEngine();
        WorkflowResult result = engine.execute(dag);

        assertEquals(WorkflowStatus.PARTIAL_SUCCESS, result.getStatus());
        assertEquals(1, result.getSuccessfulTasks());  // A succeeds
        assertEquals(1, result.getFailedTasks());      // B fails
        assertEquals(1, result.getSkippedTasks());     // C skipped

        assertEquals(TaskStatus.SUCCESS, result.getTaskResult("A").getStatus());
        assertEquals(TaskStatus.FAILED, result.getTaskResult("B").getStatus());
        assertEquals(TaskStatus.SKIPPED, result.getTaskResult("C").getStatus());
    }

    @Test
    void testIndependentTasksRunDespiteFailure() {
        DAG dag = new DAG();
        dag.addTask(new FailTask("A", "A"));
        dag.addTask(new PrintTask("B", "B", "B"));  // Independent of A

        WorkflowEngine engine = new WorkflowEngine();
        WorkflowResult result = engine.execute(dag);

        assertEquals(WorkflowStatus.PARTIAL_SUCCESS, result.getStatus());
        assertEquals(1, result.getSuccessfulTasks());  // B succeeds
        assertEquals(1, result.getFailedTasks());      // A fails
    }
    @Test
    void testEmptyWorkflow() {
        DAG dag = new DAG();
        WorkflowEngine engine = new WorkflowEngine();
        WorkflowResult result = engine.execute(dag);

        assertEquals(WorkflowStatus.SUCCESS, result.getStatus());
        assertEquals(0, result.getTotalTasks());
    }

    @Test
    void testAllTasksFail() {
        DAG dag = new DAG();
        dag.addTask(new FailTask("A", "A"));
        dag.addTask(new FailTask("B", "B"));

        WorkflowEngine engine = new WorkflowEngine();
        WorkflowResult result = engine.execute(dag);

        assertEquals(WorkflowStatus.FAILED, result.getStatus());
        assertEquals(0, result.getSuccessfulTasks());
        assertEquals(2, result.getFailedTasks());
    }

    @Test
    void testDisconnectedComponents() {
        // Component 1 - A -> B (both succeed)
        // Component 2: C -> D (D fail)
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A", "A", "A"));
        dag.addTask(new PrintTask("B", "B", "B"));
        dag.addTask(new PrintTask("C", "C", "C"));
        dag.addTask(new FailTask("D", "D"));

        dag.addDependencies("B", "A");
        dag.addDependencies("D", "C");

        WorkflowEngine engine = new WorkflowEngine();
        WorkflowResult result = engine.execute(dag);

        assertEquals(WorkflowStatus.PARTIAL_SUCCESS, result.getStatus());
        assertEquals(3, result.getSuccessfulTasks());
        assertEquals(1, result.getFailedTasks());
    }

    @Test
    void testMathTaskDivisionByZero() {
        DAG dag = new DAG();
        dag.addTask(new MathTask("div", "Divide", 10, 0, MathTask.Operation.DIVIDE));

        WorkflowEngine engine = new WorkflowEngine();
        WorkflowResult result = engine.execute(dag);

        assertEquals(WorkflowStatus.FAILED, result.getStatus());
        assertEquals(TaskStatus.FAILED, result.getTaskResult("div").getStatus());
        assertTrue(result.getTaskResult("div").getErrorMessage().contains("zero"));
    }

    @Test
    void testConditionalTaskSkipsWhenConditionFalse() {
        DAG dag = new DAG();
        dag.addTask(new ConditionalTask("cond", "Check", () -> false, "Should skip"));

        WorkflowEngine engine = new WorkflowEngine();
        WorkflowResult result = engine.execute(dag);

        assertEquals(TaskStatus.SKIPPED, result.getTaskResult("cond").getStatus());
    }
}