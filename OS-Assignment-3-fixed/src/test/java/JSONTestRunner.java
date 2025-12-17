package org.example;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JSONTestRunner {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("Running tests...");
        runAGTests();
        runOtherSchedulersTests();
        System.out.println("\nPassed: " + passedTests + ", Failed: " + failedTests);
        if (failedTests > 0) {
            System.exit(1);
        }
    }

    private static void runAGTests() {
        File folder = new File("test_cases/AG");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null || files.length == 0) {
            failedTests++;
            return;
        }

        Arrays.sort(files);

        for (File file : files) {
            try {
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

                JsonObject expected = testCase.getAsJsonObject("expectedOutput");
                double expectedAvgWT = expected.get("averageWaitingTime").getAsDouble();
                double expectedAvgTAT = expected.get("averageTurnaroundTime").getAsDouble();

                double actualAvgWT = processes.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
                double actualAvgTAT = processes.stream().mapToInt(Process::getTurnaroundTime).average().orElse(0);

                double wtTolerance = 0.5;
                double tatTolerance = 0.5;

                boolean wtPass = Math.abs(expectedAvgWT - actualAvgWT) <= wtTolerance;
                boolean tatPass = Math.abs(expectedAvgTAT - actualAvgTAT) <= tatTolerance;

                if (wtPass && tatPass) {
                    System.out.println("PASS: " + file.getName());
                    passedTests++;
                } else {
                    System.out.println("FAIL: " + file.getName());
                    if (!wtPass) {
                        System.out.println("  WT: Expected " + expectedAvgWT + ", Got " + actualAvgWT);
                    }
                    if (!tatPass) {
                        System.out.println("  TAT: Expected " + expectedAvgTAT + ", Got " + actualAvgTAT);
                    }
                    failedTests++;
                }

            } catch (Exception e) {
                System.out.println("ERROR: " + file.getName());
                failedTests++;
            }
        }
    }

    private static void runOtherSchedulersTests() {
        File folder = new File("test_cases/Other_Schedulers");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null || files.length == 0) {
            failedTests++;
            return;
        }

        Arrays.sort(files);

        for (File file : files) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                JsonObject testCase = gson.fromJson(content, JsonObject.class);

                JsonArray processesJson = testCase.getAsJsonObject("input").getAsJsonArray("processes");
                int contextSwitch = testCase.getAsJsonObject("input").get("contextSwitch").getAsInt();
                int roundRobinQuantum = testCase.getAsJsonObject("input").get("rrQuantum").getAsInt();
                int agingInterval = testCase.getAsJsonObject("input").get("agingInterval").getAsInt();

                testSJF(file.getName(), processesJson, contextSwitch, testCase.getAsJsonObject("expectedOutput"));
                testRoundRobin(file.getName(), processesJson, contextSwitch, roundRobinQuantum, testCase.getAsJsonObject("expectedOutput"));
                testPriority(file.getName(), processesJson, contextSwitch, agingInterval, testCase.getAsJsonObject("expectedOutput"));

            } catch (Exception e) {
                failedTests += 3;
            }
        }
    }

    private static void testSJF(String fileName, JsonArray processesJson, int contextSwitch, JsonObject expectedOutput) {
        List<Process> processes = new ArrayList<>();
        for (JsonElement elem : processesJson) {
            JsonObject p = elem.getAsJsonObject();
            processes.add(new Process(
                p.get("arrival").getAsInt(),
                p.get("burst").getAsInt(),
                p.get("priority").getAsInt(),
                p.get("name").getAsString()
            ));
        }

        PreemptiveSJF sjf = new PreemptiveSJF(processes, contextSwitch);
        sjf.run();

        JsonObject expected = expectedOutput.getAsJsonObject("SJF");
        double expectedAvgWT = expected.get("averageWaitingTime").getAsDouble();
        double expectedAvgTAT = expected.get("averageTurnaroundTime").getAsDouble();

        double actualAvgWT = processes.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
        double actualAvgTAT = processes.stream().mapToInt(Process::getTurnaroundTime).average().orElse(0);

        double tolerance = 0.1;

        boolean wtPass = Math.abs(expectedAvgWT - actualAvgWT) <= tolerance;
        boolean tatPass = Math.abs(expectedAvgTAT - actualAvgTAT) <= tolerance;

        if (wtPass && tatPass) {
            System.out.println("PASS: " + fileName + " [SJF]");
            passedTests++;
        } else {
            System.out.println("FAIL: " + fileName + " [SJF]");
            if (!wtPass) {
                System.out.println("  WT: Expected " + expectedAvgWT + ", Got " + actualAvgWT);
            }
            if (!tatPass) {
                System.out.println("  TAT: Expected " + expectedAvgTAT + ", Got " + actualAvgTAT);
            }
            failedTests++;
        }
    }

    private static void testRoundRobin(String fileName, JsonArray processesJson, int contextSwitch, int quantum, JsonObject expectedOutput) {
        List<Process> processes = new ArrayList<>();
        for (JsonElement elem : processesJson) {
            JsonObject p = elem.getAsJsonObject();
            processes.add(new Process(
                p.get("arrival").getAsInt(),
                p.get("burst").getAsInt(),
                p.get("priority").getAsInt(),
                p.get("name").getAsString()
            ));
        }

        RoundRobin rr = new RoundRobin(processes, quantum, contextSwitch);
        rr.run();

        JsonObject expected = expectedOutput.getAsJsonObject("RR");
        double expectedAvgWT = expected.get("averageWaitingTime").getAsDouble();
        double expectedAvgTAT = expected.get("averageTurnaroundTime").getAsDouble();

        double actualAvgWT = processes.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
        double actualAvgTAT = processes.stream().mapToInt(Process::getTurnaroundTime).average().orElse(0);

        double tolerance = 0.1;

        boolean wtPass = Math.abs(expectedAvgWT - actualAvgWT) <= tolerance;
        boolean tatPass = Math.abs(expectedAvgTAT - actualAvgTAT) <= tolerance;

        if (wtPass && tatPass) {
            System.out.println("PASS: " + fileName + " [RR]");
            passedTests++;
        } else {
            System.out.println("FAIL: " + fileName + " [RR]");
            if (!wtPass) {
                System.out.println("  WT: Expected " + expectedAvgWT + ", Got " + actualAvgWT);
            }
            if (!tatPass) {
                System.out.println("  TAT: Expected " + expectedAvgTAT + ", Got " + actualAvgTAT);
            }
            failedTests++;
        }
    }

    private static void testPriority(String fileName, JsonArray processesJson, int contextSwitch, int agingInterval, JsonObject expectedOutput) {
        List<Process> processes = new ArrayList<>();
        for (JsonElement elem : processesJson) {
            JsonObject p = elem.getAsJsonObject();
            processes.add(new Process(
                p.get("arrival").getAsInt(),
                p.get("burst").getAsInt(),
                p.get("priority").getAsInt(),
                p.get("name").getAsString()
            ));
        }

        PriorityScheduler priority = new PriorityScheduler(processes, contextSwitch, agingInterval);
        priority.run();

        JsonObject expected = expectedOutput.getAsJsonObject("Priority");
        double expectedAvgWT = expected.get("averageWaitingTime").getAsDouble();
        double expectedAvgTAT = expected.get("averageTurnaroundTime").getAsDouble();

        double actualAvgWT = processes.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
        double actualAvgTAT = processes.stream().mapToInt(Process::getTurnaroundTime).average().orElse(0);

        double tolerance = 0.1;

        boolean wtPass = Math.abs(expectedAvgWT - actualAvgWT) <= tolerance;
        boolean tatPass = Math.abs(expectedAvgTAT - actualAvgTAT) <= tolerance;

        if (wtPass && tatPass) {
            System.out.println("PASS: " + fileName + " [Priority]");
            passedTests++;
        } else {
            System.out.println("FAIL: " + fileName + " [Priority]");
            if (!wtPass) {
                System.out.println("  WT: Expected " + expectedAvgWT + ", Got " + actualAvgWT);
            }
            if (!tatPass) {
                System.out.println("  TAT: Expected " + expectedAvgTAT + ", Got " + actualAvgTAT);
            }
            failedTests++;
        }
    }
}
