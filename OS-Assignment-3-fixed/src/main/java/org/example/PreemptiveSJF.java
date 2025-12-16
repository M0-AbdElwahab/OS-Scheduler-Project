package org.example;
import java.util.*;

public class PreemptiveSJF {
    private final List<Process> processes;
    private final int contextSwitch;
    private final List<String> executionOrder;

    public PreemptiveSJF(List<Process> processes, int contextSwitch) {
        this.processes = processes;
        this.contextSwitch = contextSwitch;
        this.executionOrder = new ArrayList<>();
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void run() {
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();
        Process lastProcess = null;

        while (completed < n) {
            Process currentProcess = null;
            int shortestTime = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0) {
                    if (p.getRemainingTime() < shortestTime) {
                        shortestTime = p.getRemainingTime();
                        currentProcess = p;
                    } else if (p.getRemainingTime() == shortestTime) {
                        if (currentProcess == null || p.getArrivalTime() < currentProcess.getArrivalTime()) {
                            currentProcess = p;
                        }
                    }
                }
            }

            if (currentProcess == null) {
                currentTime++;
                continue;
            }


            if (lastProcess != null && lastProcess != currentProcess) {
                currentTime += contextSwitch;
            }

            executionOrder.add(currentProcess.getName());
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
            currentTime++;
            lastProcess = currentProcess;

            if (currentProcess.getRemainingTime() == 0) {
                completed++;
                currentProcess.setCompletionTime(currentTime);
                currentProcess.setTurnaroundTime(currentTime - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
            }
        }
        
        printResults();
    }
    
    private void printResults() {
        System.out.println("\n===== Preemptive SJF Scheduling =====");
        
        System.out.print("Execution Order: ");
        String prev = "";
        for (String p : executionOrder) {
            if (!p.equals(prev)) {
                System.out.print(p + " ");
                prev = p;
            }
        }
        System.out.println();
        
        double totalWT = 0;
        double totalTAT = 0;
        
        System.out.println("\nProcess Details:");
        processes.sort(Comparator.comparing(Process::getName));
        
        for (Process p : processes) {
            System.out.println(p.getName() + " - Waiting Time: " + 
                p.getWaitingTime() + ", Turnaround Time: " + p.getTurnaroundTime());
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
        }
        
        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWT / processes.size());
        System.out.printf("Average Turnaround Time: %.2f\n", totalTAT / processes.size());
    }
}