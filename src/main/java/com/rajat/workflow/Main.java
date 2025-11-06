package com.rajat.workflow;

import com.rajat.workflow.domain.*;
import com.rajat.workflow.graph.DAG;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Workflow Engine Demo ===\n");

        DAG workflow = new DAG();

        // Adding the tasks
        workflow.addTask(new PrintTask("download", "Download Data", "Downloading..."));
        workflow.addTask(new PrintTask("validate", "Validate Data", "Validating..."));
        workflow.addTask(new SleepTask("process", "Process Data", 1000));
        workflow.addTask(new PrintTask("save", "Save Results", "Saving..."));

        // Define the dependencies
        workflow.addDependencies("validate", "download");  // validate depends on download
        workflow.addDependencies("process", "validate");   // process depends on validate
        workflow.addDependencies("save", "process");       // save depends on process

        // Getting the execution order
        System.out.println("Checking for cycles...");
        if (workflow.hasCycle()) {
            System.out.println("ERROR: Workflow contains a cycle!");
            return;
        }
        System.out.println("No cycles detected.\n");

        System.out.println("Calculating execution order...");
        List<String> order = workflow.getExecutionOrder();
        System.out.println("Execution order: " + order + "\n");

        System.out.println("Executing tasks in order:");
        for (String taskId : order) {
            Task task = workflow.getTask(taskId);
            TaskResult result = task.execute();
            System.out.println(taskId + " completed in " + result.getExecutionTimeMs() + "ms");
        }

        System.out.println("\n=== Workflow Complete ===");
    }
}