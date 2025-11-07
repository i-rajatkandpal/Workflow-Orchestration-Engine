package com.rajat.workflow;

import com.rajat.workflow.domain.*;
import com.rajat.workflow.graph.DAG;
import com.rajat.workflow.engine.WorkflowResult;
import com.rajat.workflow.engine.WorkflowEngine;


import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("--------------Workflow engine demo----------------");
        runSuccessfulWorkflow();
        System.out.println("--------------------------------------------------");
        runFailingWorkflow();
    }
    public static void runSuccessfulWorkflow(){
        System.out.println("Demo Successful workflow");
        DAG workflow = new DAG();
        workflow.addTask(new PrintTask("download","download data","Downloading..."));
        workflow.addTask(new PrintTask("validate", "validate data", "Validating..."));
        workflow.addTask(new SleepTask("process", "process data",500));
        workflow.addTask(new PrintTask("save","save result","Saving..."));

        workflow.addDependencies("validate" , "download");
        workflow.addDependencies("process", "validate");
        workflow.addDependencies("save" , "process");

        WorkflowEngine engine = new WorkflowEngine();
        WorkflowResult result = engine.execute(workflow);

        printWorkflowSummary(result);
    }
    public static void runFailingWorkflow(){
        System.out.println("Demo: Workflow with failure");
        DAG workflow = new DAG();

        workflow.addTask(new PrintTask("taskA","Task A", "Running A"));
        workflow.addTask(new FailTask("taskB","Task B"));
        workflow.addTask(new PrintTask("taskC","Task C","Running C"));
        workflow.addTask(new PrintTask("taskD","Task D","Running D"));

        workflow.addDependencies("taskC","taskB");
        workflow.addDependencies("taskD","taskC");

        WorkflowEngine engine = new WorkflowEngine();
        WorkflowResult result = engine.execute(workflow);

        printWorkflowSummary(result);
    }

    public static void printWorkflowSummary(WorkflowResult result){
        System.out.println("--------------Workflow summary------------");
        System.out.println("Status: " + result.getStatus());
        System.out.println("Total Tasks:  " + result.getTotalTasks());
        System.out.println("Successful Tasks:  " + result.getSuccessfulTasks());
        System.out.println("Failed Tasks: " + result.getFailedTasks());
        System.out.println("Skipped Tasks: " + result.getSkippedTasks());
        System.out.println("Execution Tasks: " + result.getExecutionTimeMs() + "ms");

    }
}