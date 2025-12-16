package org.example;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Test Case 1: Basic test from assignment
        System.out.println("===== TEST CASE 1 =====\n");

        int numberOfProcesses = 4;
        int RRQuantum = 2;
        int CSTime = 0;

        Process p1 = new Process(0, 17, 4, "P1");
        p1.setQuantum(4);
        Process p2 = new Process(3, 6, 9, "P2");
        p2.setQuantum(3);
        Process p3 = new Process(4, 10, 3, "P3");
        p3.setQuantum(5);
        Process p4 = new Process(29, 4, 8, "P4");
        p4.setQuantum(2);

        List<Process> processes = new ArrayList<>();
        processes.add(p1);
        processes.add(p2);
        processes.add(p3);
        processes.add(p4);

        // Run Preemptive SJF
        new PreemptiveSJF(processes, CSTime).run();

        System.out.println("\n" + "=".repeat(60) + "\n");

//        // Run Round Robin
//        new RoundRobin(processes, RRQuantum, CSTime).run();
//
//        System.out.println("\n" + "=".repeat(60) + "\n");
//
//        // Run Priority Scheduling
//        new PriorityScheduler(processes, CSTime).run();
//
//        System.out.println("\n" + "=".repeat(60) + "\n");
//
//        // Run AG Scheduling
//        new AGScheduler(processes, CSTime).run();


        // Test Case 2: Custom test
        System.out.println("\n\n\n===== TEST CASE 2 =====\n");

        numberOfProcesses = 4;
        RRQuantum = 7;
        CSTime = 10;

        Process p1_2 = new Process(0, 17, 4, "P1");
        p1_2.setQuantum(7);
        Process p2_2 = new Process(2, 6, 7, "P2");
        p2_2.setQuantum(9);
        Process p3_2 = new Process(5, 11, 3, "P3");
        p3_2.setQuantum(4);
        Process p4_2 = new Process(15, 4, 6, "P4");
        p4_2.setQuantum(6);

        List<Process> processes2 = new ArrayList<>();
        processes2.add(p1_2);
        processes2.add(p2_2);
        processes2.add(p3_2);
        processes2.add(p4_2);

        // Run Preemptive SJF
        new PreemptiveSJF(processes2, CSTime).run();

        System.out.println("\n" + "=".repeat(60) + "\n");

//        // Run Round Robin
//        new RoundRobin(processes2, RRQuantum, CSTime).run();
//
//        System.out.println("\n" + "=".repeat(60) + "\n");
//
//        // Run Priority Scheduling
//        new PriorityScheduler(processes2, CSTime).run();
//
//        System.out.println("\n" + "=".repeat(60) + "\n");
//
//        // Run AG Scheduling
//        new AGScheduler(processes2, CSTime).run();
    }
}