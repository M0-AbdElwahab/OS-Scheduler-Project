package org.example;

import java.util.*;

public class RoundRobin {
    private int quantum;
    private int contextSwitchTime;
    private List<Process> processes;
    private List<String> executionOrder;
    
    public RoundRobin(List<Process> processes, int quantum, int contextSwitch) {
        this.processes = processes;
        this.quantum = quantum;
        this.contextSwitchTime = contextSwitch;
        this.executionOrder = new ArrayList<>();
    }
    
    public List<Process> getProcesses() {
        return processes;
    }
    
    public void run() {
        schedule();
        printResults();
    }
    
    private void schedule() {
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        
        for (Process p : processes) {
            p.setRemainingBurstTime(p.getBurstTime());
        }
        
        Queue<Process> readyQueue = new LinkedList<>();
        int currentTime = 0;
        Process currentProcess = null;
        int quantumRemaining = 0;
        int processIndex = 0;
        boolean firstExecution = true;
        
        while (processIndex < processes.size() || !readyQueue.isEmpty() || currentProcess != null) {
            
            while (processIndex < processes.size() && 
                   processes.get(processIndex).getArrivalTime() <= currentTime) {
                readyQueue.offer(processes.get(processIndex));
                processIndex++;
            }
            
            if (currentProcess == null && !readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
                quantumRemaining = quantum;
                executionOrder.add(currentProcess.getName());
                
                if (!firstExecution) {
                    currentTime += contextSwitchTime;
                    
                    while (processIndex < processes.size() && 
                           processes.get(processIndex).getArrivalTime() <= currentTime) {
                        readyQueue.offer(processes.get(processIndex));
                        processIndex++;
                    }
                }
                firstExecution = false;
            }
            
            if (currentProcess != null) {
                currentProcess.setRemainingBurstTime(currentProcess.getRemainingBurstTime() - 1);
                quantumRemaining--;
                currentTime++;
                
                while (processIndex < processes.size() && 
                       processes.get(processIndex).getArrivalTime() <= currentTime) {
                    readyQueue.offer(processes.get(processIndex));
                    processIndex++;
                }
                
                if (currentProcess.isCompleted()) {
                    currentProcess.setCompletionTime(currentTime);
                    currentProcess.setTurnaroundTime(currentTime - currentProcess.getArrivalTime());
                    currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                    currentProcess = null;
                    quantumRemaining = 0;
                }
                else if (quantumRemaining == 0) {
                    readyQueue.offer(currentProcess);
                    currentProcess = null;
                }
            } 
            else {
                if (processIndex < processes.size()) {
                    currentTime = processes.get(processIndex).getArrivalTime();
                } else {
                    break;
                }
            }
        }
    }
    
    private void printResults() {
        System.out.println("\n===== Round Robin Scheduling =====");

        System.out.print("Execution Order: ");
        String prev = "";
        for (String proc : executionOrder) {
            if (!proc.equals(prev)) {
                System.out.print(proc + " ");
                prev = proc;
            }
        }
        System.out.println();

        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        System.out.println("\nProcess Details:");
        for (Process p : processes) {
            System.out.println(p.getName() + " - Waiting Time: " + p.getWaitingTime() +
                    ", Turnaround Time: " + p.getTurnaroundTime());
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWaitingTime / processes.size());
        System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaroundTime / processes.size());
    }
}