package org.example;

import java.util.*;

public class PriorityScheduler {

    private final List<Process> processes;
    private final int contextSwitch;
    private final int agingInterval;
    private final Map<Process, Integer> waitingTimeForAging = new HashMap<>();
    private final List<String> executionOrder = new ArrayList<>();

    public PriorityScheduler(List<Process> processes, int contextSwitch, int agingInterval) {
        this.processes = processes;
        this.contextSwitch = contextSwitch;
        this.agingInterval = agingInterval;
        for (Process p : processes) {
            p.setRemainingTime(p.getBurstTime());
            waitingTimeForAging.put(p, 0);
        }
    }

    public void run() {
        int currentTime = 0;
        int completedCount = 0;
        Process lastProcess = null;

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        while (completedCount < processes.size()) {
            Process current = selectProcess(currentTime);

            if (current == null) {
                currentTime++;
                updateAgingTimers(currentTime, null);
                continue;
            }

            if (lastProcess != null && lastProcess != current) {
                for (int i = 0; i < contextSwitch; i++) {
                    currentTime++;
                    updateAgingTimers(currentTime, null);
                }
            }

            executionOrder.add(current.getName());
            waitingTimeForAging.put(current, 0);

            current.setRemainingTime(current.getRemainingTime() - 1);
            currentTime++;

            updateAgingTimers(currentTime, current);

            if (current.getRemainingTime() == 0) {
                completedCount++;
                current.setCompletionTime(currentTime);
                current.setTurnaroundTime(currentTime - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
            }

            lastProcess = current;
        }
        
        printResults();
    }
    
    private void printResults() {
        System.out.println("\n===== Priority Scheduling (with Aging) =====");
        
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

    /**
     * Updates the waiting time counter for aging.
     * All incomplete processes that have arrived and are NOT running get +1.
     */
    private void updateAgingTimers(int currentTime, Process runningProcess) {
        for (Process p : processes) {
            if (p.getArrivalTime() <= currentTime &&
                    p.getRemainingTime() > 0 &&
                    p != runningProcess) {

                int currentWait = waitingTimeForAging.getOrDefault(p, 0);
                waitingTimeForAging.put(p, currentWait + 1);
            }
        }
    }

    /**
     * Selects the process with the lowest Effective Priority.
     * Effective Priority = Base Priority - (Waiting Time / Aging Interval)
     * Tie Breakers:
     * 1. Arrival Time (Earlier wins)
     * 2. List Order (Stability)
     */
    private Process selectProcess(int currentTime) {
        Process best = null;
        double bestEffectivePriority = Double.MAX_VALUE;

        for (Process p : processes) {
            if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0) {

                int ageFactor = waitingTimeForAging.getOrDefault(p, 0) / agingInterval;
                int effectivePriority = p.getPriorityTime() - ageFactor;

                if (effectivePriority < bestEffectivePriority) {
                    bestEffectivePriority = effectivePriority;
                    best = p;
                } else if (effectivePriority == bestEffectivePriority) {
                    if (best == null || p.getArrivalTime() < best.getArrivalTime()) {
                        best = p;
                    }
                }
            }
        }
        return best;
    }

    public List<Process> getProcesses() {
        return processes;
    }
}