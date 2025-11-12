package com.rajat.workflow.graph;

import com.rajat.workflow.domain.PrintTask;
import com.rajat.workflow.domain.Task;
import com.rajat.workflow.exception.CyclicDependencyException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DAGTest {

    @Test
    void testAddTask() {
        DAG dag = new DAG();
        Task task = new PrintTask("task1", "Test", "Hello");

        dag.addTask(task);

        assertEquals(task, dag.getTask("task1"));
        assertTrue(dag.getAllTasksId().contains("task1"));

    }

    @Test
    void testAddDependency(){
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A" , "TaskA", "A"));
        dag.addTask(new PrintTask("B" , "TaskB", "B"));

        dag.addDependencies("A","B");
        assertTrue(dag.getDependencies("A").contains("B"));

    }

    @Test
    void testLinearDependencies(){
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A","TaskA","A"));
        dag.addTask(new PrintTask("B","TaskB","B"));
        dag.addTask(new PrintTask("C","TaskC","C"));

        dag.addDependencies("A","B");
        dag.addDependencies("B","C");

        assertFalse(dag.hasCycle());
    }


    @Test
    void testSimpleCycle(){
        DAG dag = new DAG();

        dag.addTask(new PrintTask("A","TaskA","A"));
        dag.addTask(new PrintTask("B","TaskB","B"));


        dag.addDependencies("A","B");
        dag.addDependencies("B","A");


        assertTrue(dag.hasCycle());
    }

    @Test
//    a-b,c
//    b-d
//    c-d

    void testDiamondDependencyNoCycle(){
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A","TaskA","A"));
        dag.addTask(new PrintTask("B","TaskB","B"));
        dag.addTask(new PrintTask("C","TaskC","C"));
        dag.addTask(new PrintTask("D","TaskD","D"));

        dag.addDependencies("A","B");
        dag.addDependencies("A","C");
        dag.addDependencies("B","D");
        dag.addDependencies("C","D");


        assertFalse(dag.hasCycle());
    }

    @Test
    void testInvalidTaskDependency() {
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A", "Task A", "A"));
        assertThrows(IllegalArgumentException.class, () -> {
            dag.addDependencies("A", "NonExistent");
        });
    }



    //topological sort tests
    @Test
    void testExecutionOrderLinear() {
        // A -> B -> C
        // Expected order: C, B, A
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A", "Task A", "A"));
        dag.addTask(new PrintTask("B", "Task B", "B"));
        dag.addTask(new PrintTask("C", "Task C", "C"));

        dag.addDependencies("A", "B");
        dag.addDependencies("B", "C");

        List<String> order = dag.getExecutionOrder();

        assertEquals(3, order.size());
        // C must come before B, B must come before A
        assertTrue(order.indexOf("C") < order.indexOf("B"));
        assertTrue(order.indexOf("B") < order.indexOf("A"));
    }

    @Test
    void testExecutionOrderDiamond() {
        // A -> B, C
        // B -> D
        // C -> D
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A", "Task A", "A"));
        dag.addTask(new PrintTask("B", "Task B", "B"));
        dag.addTask(new PrintTask("C", "Task C", "C"));
        dag.addTask(new PrintTask("D", "Task D", "D"));

        dag.addDependencies("A", "B");
        dag.addDependencies("A", "C");
        dag.addDependencies("B", "D");
        dag.addDependencies("C", "D");

        List<String> order = dag.getExecutionOrder();

        assertEquals(4, order.size());
        // D must come before B and C
        assertTrue(order.indexOf("D") < order.indexOf("B"));
        assertTrue(order.indexOf("D") < order.indexOf("C"));
        // B and C must come before A
        assertTrue(order.indexOf("B") < order.indexOf("A"));
        assertTrue(order.indexOf("C") < order.indexOf("A"));
    }

    @Test
    void testExecutionOrderThrowsOnCycle() {
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A", "Task A", "A"));
        dag.addTask(new PrintTask("B", "Task B", "B"));

        dag.addDependencies("A", "B");
        dag.addDependencies("B", "A");

        assertThrows(CyclicDependencyException.class, () -> {
            dag.getExecutionOrder();
        });
    }

    @Test
    void testNullTaskThrowsException() {
        DAG dag = new DAG();
        assertThrows(IllegalArgumentException.class, () -> {
            dag.addTask(null);
        });
    }

    @Test
    void testDuplicateTaskIdThrowsException() {
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A", "Task A", "A"));

        assertThrows(IllegalArgumentException.class, () -> {
            dag.addTask(new PrintTask("A", "Task A Again", "A"));
        });
    }

    @Test
    void testSelfDependencyThrowsException() {
        DAG dag = new DAG();
        dag.addTask(new PrintTask("A", "Task A", "A"));

        assertThrows(IllegalArgumentException.class, () -> {
            dag.addDependencies("A", "A");
        });
    }

    @Test
    void testEmptyTaskIdThrowsException() {
        DAG dag = new DAG();
        assertThrows(IllegalArgumentException.class, () -> {
            dag.addTask(new PrintTask("", "Task", "message"));
        });
    }
}
