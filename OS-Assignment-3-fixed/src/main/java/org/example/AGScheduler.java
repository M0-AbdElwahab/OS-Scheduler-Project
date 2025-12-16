package org.example;

import java.util.*;

public class AGScheduler {
    private List<Process> processes;
    private Queue<Process> readyQueue;
    private List<String> executionOrder;
    private Map<String, List<Integer>> quantumHistoryMap;
    private int contextSwitch;

    public AGScheduler(List<Process> processes, int contextSwitch) {
        this.processes = processes;
        this.contextSwitch = contextSwitch;
        this.readyQueue = new LinkedList<>();
        this.executionOrder = new ArrayList<>();
        this.quantumHistoryMap = new HashMap<>();
        
        for (Process p : processes) {
            quantumHistoryMap.put(p.getName(), new ArrayList<>());
            quantumHistoryMap.get(p.getName()).add(p.getQuantum());
        }
    }

    public void run() {
        int currentTime = 0;
        int completed = 0;
        Process currentProcess = null;
        Process lastProcess = null;
        int currentQuantumStartTime = 0;

        List<Process> arrivalList = new ArrayList<>(processes);
        arrivalList.sort(Comparator.comparingInt(Process::getArrivalTime));
        int arrivalIndex = 0;

        while (completed < processes.size()) {
            while (arrivalIndex < arrivalList.size() && arrivalList.get(arrivalIndex).getArrivalTime() <= currentTime) {
                readyQueue.add(arrivalList.get(arrivalIndex));
                arrivalIndex++;
            }

            if (currentProcess == null) {
                if (!readyQueue.isEmpty()) {
                    currentProcess = readyQueue.poll();
                    

                    if (lastProcess != null && lastProcess != currentProcess) {
                        currentTime += contextSwitch;

                         while (arrivalIndex < arrivalList.size() && arrivalList.get(arrivalIndex).getArrivalTime() <= currentTime) {
                            readyQueue.add(arrivalList.get(arrivalIndex));
                            arrivalIndex++;
                        }
                    }
                    
                    currentQuantumStartTime = currentTime;
                } else {
                    currentTime++;
                    continue;
                }
            }

            executionOrder.add(currentProcess.getName());
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
            currentTime++;

            if (currentProcess.getRemainingTime() == 0) {
                completed++;
                currentProcess.setCompletionTime(currentTime);
                currentProcess.setTurnaroundTime(currentTime - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                
                quantumHistoryMap.get(currentProcess.getName()).add(0);
                
                lastProcess = currentProcess;
                currentProcess = null;
                continue;
            }

            int quantumUsed = currentTime - currentQuantumStartTime;
            int q = currentProcess.getQuantum();
            int t1 = (int) Math.ceil(0.25 * q);
            int t2 = (int) Math.ceil(0.50 * q);

            boolean switched = false;

            if (quantumUsed == t1) {
                Process bestPriorityProc = getBestPriorityProcess(readyQueue);
                if (bestPriorityProc != null && bestPriorityProc.getPriorityTime() < currentProcess.getPriorityTime()) {
                    int remainingQ = q - quantumUsed;
                    int addedQ = (int) Math.ceil(remainingQ / 2.0);
                    int newQ = q + addedQ;
                    
                    currentProcess.setQuantum(newQ);
                    quantumHistoryMap.get(currentProcess.getName()).add(newQ);
                    
                    readyQueue.add(currentProcess);
                    readyQueue.remove(bestPriorityProc);
                    
                    lastProcess = currentProcess;
                    currentProcess = bestPriorityProc;
                    currentQuantumStartTime = currentTime;
                    switched = true;
                }
            }

            else if (quantumUsed >= t2 && quantumUsed < q && !switched) {
                 Process shortestJob = getShortestJobProcess(readyQueue);
                 if (shortestJob != null && shortestJob.getRemainingTime() < currentProcess.getRemainingTime()) {
                     int remainingQ = q - quantumUsed;
                     int newQ = q + remainingQ;
                     
                     currentProcess.setQuantum(newQ);
                     quantumHistoryMap.get(currentProcess.getName()).add(newQ);
                     
                     readyQueue.add(currentProcess);
                     readyQueue.remove(shortestJob);
                     
                     lastProcess = currentProcess;
                     currentProcess = shortestJob;
                     currentQuantumStartTime = currentTime;
                     switched = true;
                 }
            }
            
            if (!switched && quantumUsed >= q) {
                int newQ = q + 2;
                currentProcess.setQuantum(newQ);
                quantumHistoryMap.get(currentProcess.getName()).add(newQ);
                
                readyQueue.add(currentProcess);
                lastProcess = currentProcess;
                currentProcess = null;
            }
        }
        
        printResults();
    }

    private Process getBestPriorityProcess(Queue<Process> queue) {
        Process best = null;
        for (Process p : queue) {
            if (best == null || p.getPriorityTime() < best.getPriorityTime()) {
                best = p;
            }
        }
        return best;
    }

    private Process getShortestJobProcess(Queue<Process> queue) {
        Process best = null;
        for (Process p : queue) {
            if (best == null || p.getRemainingTime() < best.getRemainingTime()) {
                best = p;
            }
        }
        return best;
    }

    private void printResults() {
        System.out.println("\n===== AG Scheduling =====");
        
        System.out.println("Quantum History:");
        for (Map.Entry<String, List<Integer>> entry : quantumHistoryMap.entrySet()) {
            System.out.print(entry.getKey() + ": ");
            System.out.println(entry.getValue());
        }
        System.out.println("--------------------------------------------------");

        System.out.print("Execution Order: ");
        String prev = "";
        for (String p : executionOrder) {
            if (!p.equals(prev)) {
                System.out.print(p + " ");
                prev = p;
            }
        }
        System.out.println();

        double totalWT = 0, totalTAT = 0;
        System.out.println("\nProcess Details:");
        processes.sort(Comparator.comparing(Process::getName));
        for (Process p : processes) {
            System.out.println(p.getName() + " - Waiting Time: " + p.getWaitingTime() +
                    ", Turnaround Time: " + p.getTurnaroundTime());
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWT / processes.size());
        System.out.printf("Average Turnaround Time: %.2f\n", totalTAT / processes.size());
    }
    
    public List<Process> getProcesses() {
        return processes;
    }
}