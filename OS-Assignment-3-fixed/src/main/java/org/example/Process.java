package org.example;

public class Process {
    private int arrivalTime;
    private int burstTime;
    private int remainingTime;
    private int waitingTime;
    private int priorityTime;
    private int turnaroundTime;
    private int completionTime;
    private String name;
    private int quantum; // For AG Scheduling

    public Process(int arriveTime, int burstTime, int priorityTime, String name) {
        this.name = name;
        this.arrivalTime = arriveTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priorityTime = priorityTime;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.completionTime = 0;
        this.quantum = 0;
    }

    // Copy constructor for AG scheduling
    public Process(Process other) {
        this.name = other.name;
        this.arrivalTime = other.arrivalTime;
        this.burstTime = other.burstTime;
        this.remainingTime = other.remainingTime;
        this.priorityTime = other.priorityTime;
        this.waitingTime = other.waitingTime;
        this.turnaroundTime = other.turnaroundTime;
        this.completionTime = other.completionTime;
        this.quantum = other.quantum;
    }

    // Getters
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingTime() { return remainingTime; }
    public int getPriorityTime() { return priorityTime; }
    public int getWaitingTime() { return waitingTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public int getCompletionTime() { return completionTime; }
    public String getName() { return name; }
    public int getQuantum() { return quantum; }

    // Setters
    public void setArrivalTime(int arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setBurstTime(int burstTime) { this.burstTime = burstTime; }
    public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }
    public void setPriorityTime(int priorityTime) { this.priorityTime = priorityTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }
    public void setCompletionTime(int completionTime) { this.completionTime = completionTime; }
    public void setName(String name) { this.name = name; }
    public void setQuantum(int quantum) { this.quantum = quantum; }

    // Aliases for Round Robin compatibility
    public int getRemainingBurstTime() {
        return remainingTime;
    }

    public void setRemainingBurstTime(int remainingBurstTime) {
        this.remainingTime = remainingBurstTime;
    }

    // Completion helper
    public boolean isCompleted() {
        return remainingTime <= 0;
    }

    @Override
    public String toString() {
        return String.format(
            "Process{name='%s', arrival=%d, burst=%d, remaining=%d, priority=%d}",
            name, arrivalTime, burstTime, remainingTime, priorityTime
        );
    }
}
