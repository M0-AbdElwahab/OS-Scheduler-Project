package org.example;

import java.util.*;

// Round Robin CPU Scheduling Algorithm
public class RoundRobinScheduler {
    private int quantum;
    private int contextSwitchTime;
    private List<Process> processes;
    private List<String> executionOrder;
    private Map<String, Process> processMap;
    
    public RoundRobinScheduler(int quantum, int contextSwitchTime) {
        this.quantum = quantum;
        this.contextSwitchTime = contextSwitchTime;
        this.processes = new ArrayList<>();
        this.executionOrder = new ArrayList<>();
        this.processMap = new HashMap<>();
    }
    
    // Add process to scheduler
    public void addProcess(Process process) {
        processes.add(process);
        processMap.put(process.getName(), process);
    }
    
    // Execute Round Robin scheduling
    public void schedule() {
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        
        // Initialize processes
        for (Process p : processes) {
            p.setRemainingBurstTime(p.getBurstTime());
        }
        
        Queue<Process> readyQueue = new LinkedList<>();
        int currentTime = 0;
        Process currentProcess = null;
        int quantumRemaining = 0;
        int processIndex = 0;
        
        Map<String, Integer> arrivalTimes = new HashMap<>();
        for (Process p : processes) {
            arrivalTimes.put(p.getName(), p.getArrivalTime());
        }
        
        Map<String, Integer> serviceTime = new HashMap<>();
        for (Process p : processes) {
            serviceTime.put(p.getName(), 0);
        }
        
        boolean firstExecution = true;
        
        // Main scheduling loop
        while (processIndex < processes.size() || !readyQueue.isEmpty() || currentProcess != null) {
            // Add newly arrived processes
            while (processIndex < processes.size() && processes.get(processIndex).getArrivalTime() <= currentTime) {
                readyQueue.offer(processes.get(processIndex));
                processIndex++;
            }
            
            // Select process from ready queue
            if (currentProcess == null && !readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
                quantumRemaining = quantum;
                executionOrder.add(currentProcess.getName());
                
                if (!firstExecution) {
                    currentTime += contextSwitchTime;
                }
                firstExecution = false;
            }
            
            // Execute current process
            if (currentProcess != null) {
                currentProcess.setRemainingBurstTime(currentProcess.getRemainingBurstTime() - 1);
                quantumRemaining -= 1;
                serviceTime.put(currentProcess.getName(), serviceTime.get(currentProcess.getName()) + 1);
                currentTime += 1;
                
                // Check for new arrivals
                while (processIndex < processes.size() && processes.get(processIndex).getArrivalTime() <= currentTime) {
                    readyQueue.offer(processes.get(processIndex));
                    processIndex++;
                }
                
                // Process completed or quantum expired
                if (currentProcess.isCompleted()) {
                    currentProcess.setCompletionTime(currentTime);
                    currentProcess = null;
                    quantumRemaining = 0;
                } else if (quantumRemaining == 0) {
                    readyQueue.offer(currentProcess);
                    currentProcess = null;
                }
            } else {
                // Advance time to next arrival
                if (processIndex < processes.size()) {
                    currentTime = processes.get(processIndex).getArrivalTime();
                } else {
                    break;
                }
            }
        }
        
        calculateMetrics(serviceTime, arrivalTimes);
    }
    
    // Calculate waiting and turnaround times
    private void calculateMetrics(Map<String, Integer> serviceTime, Map<String, Integer> arrivalTimes) {
        for (Process p : processes) {
            p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        }
    }
    
    public double getAverageWaitingTime() {
        if (processes.isEmpty()) return 0.0;
        int total = processes.stream().mapToInt(Process::getWaitingTime).sum();
        return (double) total / processes.size();
    }
    
    public double getAverageTurnaroundTime() {
        if (processes.isEmpty()) return 0.0;
        int total = processes.stream().mapToInt(Process::getTurnaroundTime).sum();
        return (double) total / processes.size();
    }
    
    public List<String> getExecutionOrder() {
        return new ArrayList<>(executionOrder);
    }
    
    public List<ProcessResult> getProcessResults() {
        List<ProcessResult> results = new ArrayList<>();
        for (Process p : processes) {
            results.add(new ProcessResult(p.getName(), p.getWaitingTime(), p.getTurnaroundTime()));
        }
        return results;
    }
    
    // Process result data class
    public static class ProcessResult {
        private String name;
        private int waitingTime;
        private int turnaroundTime;
        
        public ProcessResult(String name, int waitingTime, int turnaroundTime) {
            this.name = name;
            this.waitingTime = waitingTime;
            this.turnaroundTime = turnaroundTime;
        }
        
        public String getName() {
            return name;
        }
        
        public int getWaitingTime() {
            return waitingTime;
        }
        
        public int getTurnaroundTime() {
            return turnaroundTime;
        }
        
        @Override
        public String toString() {
            return String.format("ProcessResult{name='%s', waitingTime=%d, turnaroundTime=%d}", 
                name, waitingTime, turnaroundTime);
        }
    }
}

// Wrapper class for RoundRobinScheduler to match Main's expected interface
class RoundRobin {
    private RoundRobinScheduler scheduler;
    private List<Process> processes;
    
    public RoundRobin(List<Process> processes, int quantum, int contextSwitch) {
        this.processes = processes;
        this.scheduler = new RoundRobinScheduler(quantum, contextSwitch);
        
        // Add all processes to scheduler
        for (Process p : processes) {
            scheduler.addProcess(p);
        }
    }
    
    public void run() {
        // Run the scheduling algorithm
        scheduler.schedule();
        
        // Print results in same format as other schedulers
        printResults();
    }
    
    private void printResults() {
        System.out.println("\n===== Round Robin Scheduling =====");
        
        // Execution order
        System.out.print("Execution Order: ");
        List<String> order = scheduler.getExecutionOrder();
        String prev = "";
        for (String proc : order) {
            if (!proc.equals(prev)) {
                System.out.print(proc + " ");
                prev = proc;
            }
        }
        System.out.println();
        
        // Individual process statistics
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;
        
        System.out.println("\nProcess Details:");
        for (RoundRobinScheduler.ProcessResult result : scheduler.getProcessResults()) {
            System.out.println(result.getName() + " - Waiting Time: " + result.getWaitingTime() +
                    ", Turnaround Time: " + result.getTurnaroundTime());
            totalWaitingTime += result.getWaitingTime();
            totalTurnaroundTime += result.getTurnaroundTime();
        }
        
        // Averages
        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWaitingTime / processes.size());
        System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaroundTime / processes.size());
    }
}
