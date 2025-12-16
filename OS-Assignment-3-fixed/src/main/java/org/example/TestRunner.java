package org.example;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TestRunner {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public static void main(String[] args) {
        String agPath = "test_cases\\AG";
        String otherPath = "test_cases\\Other_Schedulers";
        
        System.out.println("========================================");
        System.out.println("       AG SCHEDULER TEST CASES");
        System.out.println("========================================\n");
        
        runAGTests(agPath);
        
        System.out.println("\n========================================");
        System.out.println("    OTHER SCHEDULERS TEST CASES");
        System.out.println("========================================\n");
        
        runOtherSchedulersTests(otherPath);
    }
    
    private static void runAGTests(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        
        if (files == null || files.length == 0) {
            System.out.println("No AG test files found!");
            return;
        }
        
        Arrays.sort(files);
        
        for (File file : files) {
            try {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("Running: " + file.getName());
                System.out.println("══════════════════════════════════════");
                
                String content = new String(Files.readAllBytes(file.toPath()));
                JsonObject testCase = gson.fromJson(content, JsonObject.class);
                
                JsonArray processesJson = testCase.getAsJsonObject("input").getAsJsonArray("processes");
                List<Process> processes = new ArrayList<>();
                
                for (JsonElement elem : processesJson) {
                    JsonObject p = elem.getAsJsonObject();
                    Process proc = new Process(
                        p.get("arrival").getAsInt(),
                        p.get("burst").getAsInt(),
                        p.get("priority").getAsInt(),
                        p.get("name").getAsString()
                    );
                    proc.setQuantum(p.get("quantum").getAsInt());
                    processes.add(proc);
                }
                
                AGScheduler scheduler = new AGScheduler(processes, 0);
                scheduler.run();
                
                System.out.println("\nExpected Output:");
                JsonObject expected = testCase.getAsJsonObject("expectedOutput");
                System.out.println("Execution Order: " + expected.getAsJsonArray("executionOrder"));
                System.out.println("Avg WT: " + expected.get("averageWaitingTime").getAsDouble());
                System.out.println("Avg TAT: " + expected.get("averageTurnaroundTime").getAsDouble());
                
            } catch (Exception e) {
                System.err.println("Error running test " + file.getName() + ": " + e.getMessage());
            }
        }
    }
    
    private static void runOtherSchedulersTests(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        
        if (files == null || files.length == 0) {
            System.out.println("No test files found!");
            return;
        }
        
        Arrays.sort(files);
        
        for (File file : files) {
            try {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("Running: " + file.getName());
                System.out.println("══════════════════════════════════════");
                
                String content = new String(Files.readAllBytes(file.toPath()));
                JsonObject testCase = gson.fromJson(content, JsonObject.class);
                
                JsonObject input = testCase.getAsJsonObject("input");
                int contextSwitch = input.get("contextSwitch").getAsInt();
                int rrQuantum = input.get("rrQuantum").getAsInt();
                int agingInterval = input.get("agingInterval").getAsInt();
                
                JsonArray processesJson = input.getAsJsonArray("processes");
                
                System.out.println("\n----- Preemptive SJF -----");
                List<Process> sjfProcesses = parseProcesses(processesJson);
                new PreemptiveSJF(sjfProcesses, contextSwitch).run();
                
                System.out.println("\n----- Round Robin -----");
                List<Process> rrProcesses = parseProcesses(processesJson);
                new RoundRobin(rrProcesses, rrQuantum, contextSwitch).run();
                
                System.out.println("\n----- Priority with Aging -----");
                List<Process> priorityProcesses = parseProcesses(processesJson);
                new PriorityScheduler(priorityProcesses, contextSwitch, agingInterval).run();
                
            } catch (Exception e) {
                System.err.println("Error running test " + file.getName() + ": " + e.getMessage());
            }
        }
    }
    
    private static List<Process> parseProcesses(JsonArray processesJson) {
        List<Process> processes = new ArrayList<>();
        for (JsonElement elem : processesJson) {
            JsonObject p = elem.getAsJsonObject();
            Process proc = new Process(
                p.get("arrival").getAsInt(),
                p.get("burst").getAsInt(),
                p.get("priority").getAsInt(),
                p.get("name").getAsString()
            );
            processes.add(proc);
        }
        return processes;
    }
}
