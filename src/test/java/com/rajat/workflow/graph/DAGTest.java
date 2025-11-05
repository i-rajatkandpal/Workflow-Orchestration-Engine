package com.rajat.workflow.graph;

import com.rajat.workflow.domain.PrintTask;
import com.rajat.workflow.domain.Task;
import org.junit.jupiter.api.Test;

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
}
