package com.rajat.workflow.graph;

import com.rajat.workflow.domain.Task;
import com.rajat.workflow.expection.CyclicDependencyException;

import java.util.*;

public class DAG {
    private final Map<String, Task> tasks;
    private final Map<String , List<String>> dependencies;

    public DAG() {
        this.tasks = new HashMap<>();
        this.dependencies = new HashMap<>();
    }

    public void addTask(Task task){
        tasks.put(task.getId(), task);
        dependencies.putIfAbsent(task.getId(), new ArrayList<>());
    }

    public void addDependencies(String taskId, String dependsOnTaskId){
        if(!tasks.containsKey(taskId)){
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        if(!tasks.containsKey(dependsOnTaskId)){
            throw new IllegalArgumentException("Dependency task not found " + dependsOnTaskId);
        }

        dependencies.get(taskId).add(dependsOnTaskId);
    }

    public boolean hasCycle(){
        Set<String> white = new HashSet<>(tasks.keySet());
        Set<String> grey = new HashSet<>();
        Set<String> black = new HashSet<>();

        for(String taskId : tasks.keySet()){
            if(white.contains(taskId)){
                if(hasCycleDFS(taskId, white, grey, black)){
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasCycleDFS(String taskId, Set<String> white, Set<String> grey, Set<String> black) {
        white.remove(taskId);
        grey.add(taskId);

        for (String dep : dependencies.getOrDefault(taskId, new ArrayList<>())) {
            if (black.contains(dep)) continue;
            if (grey.contains(dep)) return true;
            if (hasCycleDFS(dep, white, grey, black)) return true;
        }

        grey.remove(taskId);
        black.add(taskId);
        return false;
    }


    public Task getTask(String taskId){
        return tasks.get(taskId);
    }

    public List<String> getDependencies(String taskId){
        return new ArrayList<>(dependencies.get(taskId));
    }

    public Set<String> getAllTasksId(){
        return new HashSet<>(tasks.keySet());
    }
}
