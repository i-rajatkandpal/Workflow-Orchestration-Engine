package com.rajat.workflow.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrintTaskTest {

    @Test
    void testTaskCreation(){
        PrintTask task = new PrintTask("task1", "Test task", "Hello world");

        assertEquals("task1" , task.getId());
        assertEquals("Test task", task.getName());
        assertEquals(TaskStatus.PENDING, task.getStatus());

    }

    @Test
    void testTaskExecution(){
        PrintTask task  = new PrintTask("task1","Test task", "Hello world");

        TaskResult result = task.execute();

        assertEquals(TaskStatus.SUCCESS, result.getStatus());
        assertEquals(TaskStatus.SUCCESS, task.getStatus());
        assertEquals("task1" , result.getTaskId());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testTaskStatusTransition() {
        PrintTask task = new PrintTask("task1", "Test Task", "Hello World");

        assertEquals(TaskStatus.PENDING, task.getStatus());

        task.setStatus(TaskStatus.RUNNING);
        assertEquals(TaskStatus.RUNNING, task.getStatus());

        task.setStatus(TaskStatus.SUCCESS);
        assertEquals(TaskStatus.SUCCESS, task.getStatus());
    }
}
