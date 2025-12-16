package org.example;

import java.util.*;

public class PriorityScheduler {

    private final List<Process> processes;
    private final int contextSwitch;
    private final int agingInterval;
    private final Map<Process, Integer> waitingTimeForAging = new HashMap<>();

    public PriorityScheduler(List<Process> processes, int contextSwitch, int agingInterval) {
        this.processes = processes;
        this.contextSwitch = contextSwitch;
        this.agingInterval = agingInterval;
        for (Process p : processes) {
            // Initialize state
            p.setRemainingTime(p.getBurstTime());
            waitingTimeForAging.put(p, 0);
        }
    }

    public void run() {
        int currentTime = 0;
        int completedCount = 0;
        Process lastProcess = null;

        // Sort by arrival time primarily to ensure correct initial order
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        while (completedCount < processes.size()) {
            Process current = selectProcess(currentTime);

            // 1. CPU Idle (No process arrived yet)
            if (current == null) {
                currentTime++;
                // Even if idle, waiting processes must age
                updateAgingTimers(currentTime, null);
                continue;
            }

            // 2. Context Switch
            // We only switch if there was a previous process and it's different from current
            if (lastProcess != null && lastProcess != current) {
                for (int i = 0; i < contextSwitch; i++) {
                    currentTime++;
                    // During context switch, NO ONE is running, so everyone ages
                    updateAgingTimers(currentTime, null);
                }
            }

            // 3. Execute Process (1 unit)
            // Reset aging for the running process immediately
            waitingTimeForAging.put(current, 0);

            current.setRemainingTime(current.getRemainingTime() - 1);
            currentTime++;

            // 4. Update Aging for OTHERS
            updateAgingTimers(currentTime, current);

            // 5. Check Completion
            if (current.getRemainingTime() == 0) {
                completedCount++;
                current.setCompletionTime(currentTime);
                current.setTurnaroundTime(currentTime - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
            }

            lastProcess = current;
        }
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

                // Calculate Effective Priority
                // Integer division floors the result (e.g., 9/5 = 1)
                int ageFactor = waitingTimeForAging.getOrDefault(p, 0) / agingInterval;
                int effectivePriority = p.getPriorityTime() - ageFactor;

                if (effectivePriority < bestEffectivePriority) {
                    bestEffectivePriority = effectivePriority;
                    best = p;
                } else if (effectivePriority == bestEffectivePriority) {
                    // TIE BREAKER:
                    // Use strict less than (<) for Arrival Time.
                    // This guarantees that if Arrival Times are equal, we keep the
                    // 'best' we already found (which appeared earlier in the list).
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