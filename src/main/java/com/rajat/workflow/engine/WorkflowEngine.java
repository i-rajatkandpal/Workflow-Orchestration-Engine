package com.rajat.workflow.engine;

import com.rajat.workflow.domain.Task;
import com.rajat.workflow.domain.TaskStatus;
import com.rajat.workflow.domain.TaskResult;
import com.rajat.workflow.graph.DAG;

import java.util.*;

public class WorkflowEngine {

    public WorkflowResult execute(DAG workflow){
        if (workflow == null) {
            throw new IllegalArgumentException("Workflow cannot be null");
        }

        if (workflow.getAllTasksId().isEmpty()) {
            return new WorkflowResult(
                    WorkflowStatus.SUCCESS,
                    new HashMap<>(),
                    0
            );
        }

        long startTime = System.currentTimeMillis();

        List<String> executeOrder = workflow.getExecutionOrder();

        Map<String, TaskResult> results = new HashMap<>();
        Set<String> failedTasks = new HashSet<>();

        Map<String, Set<String>> dependents = buildDependentsMap(workflow);

        for(String taskId : executeOrder){
            Task task = workflow.getTask(taskId);

            // if any dependencies failed
            if(hasFailedDependency(taskId, workflow , failedTasks)){
                TaskResult skippedResult = createSkippedResult(taskId);
                results.put(taskId, skippedResult);
                task.setStatus(TaskStatus.SKIPPED);
                failedTasks.add(taskId); // treating it as failed for downstream

                System.out.println("[" + taskId + "] SKIPPED (dependency failed)");
                continue;

            }

            // execute the task
            System.out.println("[" + taskId + "] Executing...");
            TaskResult result = task.execute();
            results.put(taskId,result);

            if(result.getStatus() == TaskStatus.FAILED){
                failedTasks.add(taskId);
                System.out.println("[" + taskId + "] FAILED: " + result.getErrorMessage());
            }
            else System.out.println("[" + taskId + "] SUCCESS");

        }

        WorkflowStatus
                overallStatus  = calculateOverallStatus(results);

        long executionTime = System.currentTimeMillis() - startTime;
        return new WorkflowResult(overallStatus, results, executionTime);
    }

    private Map<String, Set<String>> buildDependentsMap(DAG workflow){
        Map<String, Set<String>> dependents = new HashMap<>();

        for(String taskId : workflow.getAllTasksId()){
            dependents.put(taskId, new HashSet<>());
        }

        for(String taskId : workflow.getAllTasksId()){
            for(String dependency : workflow.getDependencies(taskId)){
                dependents.get(dependency).add(taskId);
            }
        }
        return dependents;
    }

    private boolean hasFailedDependency(String taskId, DAG workflow, Set<String> failedTasks){
        List<String> dependencies = workflow.getDependencies(taskId);
        for(String dependency : dependencies){
            if(failedTasks.contains(dependency)) return true;
        }
        return false;
    }

    private TaskResult createSkippedResult(String taskId){
        return new TaskResult(taskId, TaskStatus.SKIPPED,null, "Skipped due to failed dependency" , 0);
    }
    private WorkflowStatus calculateOverallStatus(Map<String, TaskResult> results){
        if (results.isEmpty()) {
            return WorkflowStatus.SUCCESS;  // Empty workflow
        }

        int success = 0, failed = 0 ,skipped = 0;
        for (TaskResult result : results.values()) {
            TaskStatus status = result.getStatus();
            if (status == TaskStatus.SUCCESS) success++;
            else if (status == TaskStatus.FAILED) failed++;
            else if (status == TaskStatus.SKIPPED) skipped++;
        }

        if (failed == 0 && skipped == 0) {
            return WorkflowStatus.SUCCESS;
        } else if (success == 0) {
            return WorkflowStatus.FAILED;
        } else {
            return WorkflowStatus.PARTIAL_SUCCESS;
        }
    }
}
