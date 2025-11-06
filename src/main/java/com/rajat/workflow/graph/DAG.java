package com.rajat.workflow.graph;

import com.rajat.workflow.domain.Task;
import com.rajat.workflow.exception.CyclicDependencyException;

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

    public List<String> getExecutionOrder(){
        if(hasCycle()) {
            throw new CyclicDependencyException("Cannot create execution order: workflow contains cycles");
        }

        Map<String, Integer> inDegree = new HashMap<>();
        // initially putting all the as 0.
        for(String taskId : tasks.keySet()){
            inDegree.put(taskId,0);
        }


        // map with all dependencies is traversed then each one dependency are traverse and values of those
        // dependencies are increase by 1 because it tells that someone is dependent on it. so increase by one
        for(String taskId : tasks.keySet()){
            for(String dependency : dependencies.get(taskId)){
                inDegree.put(dependency,inDegree.get(dependency) + 1);
            }
        }

        //making a queue and add all those tasks who have in-degree = 0

        Queue<String> queue = new LinkedList<>();
        for(String taskId : inDegree.keySet()){
            if(inDegree.get(taskId) == 0 ){
                queue.add(taskId);
            }
        }

        List<String> executionOrder = new ArrayList<>();
        while(!queue.isEmpty()){
            String taskId = queue.poll();
            executionOrder.add(taskId);

            for(String dependent : tasks.keySet()){
                if(dependencies.get(dependent).contains(dependent)){
                    inDegree.put(dependent,inDegree.get(dependent) - 1);
                    if(inDegree.get(dependent) == 0){
                        queue.add(dependent);
                    }
                }
            }
        }
        return executionOrder;



        // Q.1 ask claude will getAllTasksId and tasks.keySet() do the same thing.
    }
}
