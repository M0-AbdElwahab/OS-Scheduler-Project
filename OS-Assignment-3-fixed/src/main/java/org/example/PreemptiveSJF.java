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
    // Get processes list for testing
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
                        // If same remaining time, choose the one that arrived first
                        if (currentProcess == null || p.getArrivalTime() < currentProcess.getArrivalTime()) {
                            currentProcess = p;
                        }
                    }
                }
            }

            if (currentProcess == null) {
                // No process available, CPU is idle
                currentTime++;
                continue;
            }

            // Add context switch time if switching to a different process
            if (lastProcess != null && lastProcess != currentProcess) {
                currentTime += contextSwitch;
            }

            // Execute the current process for 1 time unit
            executionOrder.add(currentProcess.getName());
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
            currentTime++;
            lastProcess = currentProcess;

            // If process is completed
            if (currentProcess.getRemainingTime() == 0) {
                completed++;
                currentProcess.setCompletionTime(currentTime);
                currentProcess.setTurnaroundTime(currentTime - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
            }
        }
    }
}