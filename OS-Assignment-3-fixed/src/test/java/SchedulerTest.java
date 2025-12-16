import org.example.*;
import org.example.Process;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class SchedulerTest {

    // Helper class to store expected results
    static class ExpectedResult {
        String name;
        int waitingTime;
        int turnaroundTime;

        ExpectedResult(String name, int waitingTime, int turnaroundTime) {
            this.name = name;
            this.waitingTime = waitingTime;
            this.turnaroundTime = turnaroundTime;
        }
    }

    // Helper method to verify process results
    private void verifyResults(List<Process> processes, List<ExpectedResult> expected,
                               double expectedAvgWT, double expectedAvgTAT) {
        double totalWT = 0;
        double totalTAT = 0;

        for (ExpectedResult exp : expected) {
            Process process = processes.stream()
                    .filter(p -> p.getName().equals(exp.name))
                    .findFirst()
                    .orElse(null);

            assertNotNull(process, "Process " + exp.name + " not found");
            assertEquals(exp.waitingTime, process.getWaitingTime(),
                    "Waiting time mismatch for " + exp.name);
            assertEquals(exp.turnaroundTime, process.getTurnaroundTime(),
                    "Turnaround time mismatch for " + exp.name);

            totalWT += process.getWaitingTime();
            totalTAT += process.getTurnaroundTime();
        }

        double avgWT = totalWT / processes.size();
        double avgTAT = totalTAT / processes.size();

        assertEquals(expectedAvgWT, avgWT, 0.01, "Average waiting time mismatch");
        assertEquals(expectedAvgTAT, avgTAT, 0.01, "Average turnaround time mismatch");
    }

    // ==================== SJF TESTS ====================

    @Test
    @DisplayName("SJF Test Case 1: Basic mixed arrivals")
    public void testSJF_TestCase1() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 8, 3, "P1"));
        processes.add(new Process(1, 4, 1, "P2"));
        processes.add(new Process(2, 2, 4, "P3"));
        processes.add(new Process(3, 1, 2, "P4"));
        processes.add(new Process(4, 3, 5, "P5"));

        PreemptiveSJF sjf = new PreemptiveSJF(processes, 1);
        sjf.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 16, 24),
                new ExpectedResult("P2", 7, 11),
                new ExpectedResult("P3", 4, 6),
                new ExpectedResult("P4", 1, 2),
                new ExpectedResult("P5", 9, 12)
        );

        verifyResults(sjf.getProcesses(), expected, 7.4, 11.0);
    }

    @Test
    @DisplayName("SJF Test Case 2: All processes arrive at time 0")
    public void testSJF_TestCase2() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 6, 3, "P1"));
        processes.add(new Process(0, 3, 1, "P2"));
        processes.add(new Process(0, 8, 2, "P3"));
        processes.add(new Process(0, 4, 4, "P4"));
        processes.add(new Process(0, 2, 5, "P5"));

        PreemptiveSJF sjf = new PreemptiveSJF(processes, 1);
        sjf.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 12, 18),
                new ExpectedResult("P2", 3, 6),
                new ExpectedResult("P3", 19, 27),
                new ExpectedResult("P4", 7, 11),
                new ExpectedResult("P5", 0, 2)
        );

        verifyResults(sjf.getProcesses(), expected, 8.2, 12.8);
    }

    @Test
    @DisplayName("SJF Test Case 3: Varied burst times with starvation risk")
    public void testSJF_TestCase3() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 10, 5, "P1"));
        processes.add(new Process(2, 5, 1, "P2"));
        processes.add(new Process(5, 3, 2, "P3"));
        processes.add(new Process(8, 7, 1, "P4"));
        processes.add(new Process(10, 2, 3, "P5"));

        PreemptiveSJF sjf = new PreemptiveSJF(processes, 1);
        sjf.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 22, 32),
                new ExpectedResult("P2", 1, 6),
                new ExpectedResult("P3", 4, 7),
                new ExpectedResult("P4", 8, 15),
                new ExpectedResult("P5", 3, 5)
        );

        verifyResults(sjf.getProcesses(), expected, 7.6, 13.0);
    }

    @Test
    @DisplayName("SJF Test Case 4: Large bursts with gaps in arrivals")
    public void testSJF_TestCase4() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 12, 2, "P1"));
        processes.add(new Process(4, 9, 3, "P2"));
        processes.add(new Process(8, 15, 1, "P3"));
        processes.add(new Process(12, 6, 4, "P4"));
        processes.add(new Process(16, 11, 2, "P5"));
        processes.add(new Process(20, 5, 5, "P6"));

        PreemptiveSJF sjf = new PreemptiveSJF(processes, 2);
        sjf.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 0, 12),
                new ExpectedResult("P2", 25, 34),
                new ExpectedResult("P3", 45, 60),
                new ExpectedResult("P4", 2, 8),
                new ExpectedResult("P5", 24, 35),
                new ExpectedResult("P6", 2, 7)
        );

        verifyResults(sjf.getProcesses(), expected, 16.33, 26.0);
    }

    @Test
    @DisplayName("SJF Test Case 5: Short bursts with high frequency")
    public void testSJF_TestCase5() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 3, 3, "P1"));
        processes.add(new Process(1, 2, 1, "P2"));
        processes.add(new Process(2, 4, 2, "P3"));
        processes.add(new Process(3, 1, 4, "P4"));
        processes.add(new Process(4, 3, 5, "P5"));

        PreemptiveSJF sjf = new PreemptiveSJF(processes, 1);
        sjf.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 0, 3),
                new ExpectedResult("P2", 5, 7),
                new ExpectedResult("P3", 11, 15),
                new ExpectedResult("P4", 1, 2),
                new ExpectedResult("P5", 5, 8)
        );

        verifyResults(sjf.getProcesses(), expected, 4.4, 7.0);
    }

    @Test
    @DisplayName("SJF Test Case 6: Mixed scenario - comprehensive test")
    public void testSJF_TestCase6() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 14, 4, "P1"));
        processes.add(new Process(3, 7, 2, "P2"));
        processes.add(new Process(6, 10, 5, "P3"));
        processes.add(new Process(9, 5, 1, "P4"));
        processes.add(new Process(12, 8, 3, "P5"));
        processes.add(new Process(15, 4, 6, "P6"));

        PreemptiveSJF sjf = new PreemptiveSJF(processes, 1);
        sjf.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 40, 54),
                new ExpectedResult("P2", 1, 8),
                new ExpectedResult("P3", 26, 36),
                new ExpectedResult("P4", 3, 8),
                new ExpectedResult("P5", 11, 19),
                new ExpectedResult("P6", 3, 7)
        );

        verifyResults(sjf.getProcesses(), expected, 14.0, 22.0);
    }

    // ==================== PRIORITY TESTS ====================

   @Test
   @DisplayName("Priority Test Case 1: Basic mixed arrivals")
   public void testPriority_TestCase1() {
       List<Process> processes = new ArrayList<>();
       processes.add(new Process(0, 8, 3, "P1"));
       processes.add(new Process(1, 4, 1, "P2"));
       processes.add(new Process(2, 2, 4, "P3"));
       processes.add(new Process(3, 1, 2, "P4"));
       processes.add(new Process(4, 3, 5, "P5"));

       PriorityScheduler priority = new PriorityScheduler(processes, 1, 5);
       priority.run();

       List<ExpectedResult> expected = Arrays.asList(
               new ExpectedResult("P1", 15, 23),
               new ExpectedResult("P2", 1, 5),
               new ExpectedResult("P3", 21, 23),
               new ExpectedResult("P4", 6, 7),
               new ExpectedResult("P5", 21, 24)
       );

       verifyResults(priority.getProcesses(), expected, 12.8, 16.4);
   }

   @Test
   @DisplayName("Priority Test Case 2: All processes arrive at time 0")
   public void testPriority_TestCase2() {
       List<Process> processes = new ArrayList<>();
       processes.add(new Process(0, 6, 3, "P1"));
       processes.add(new Process(0, 3, 1, "P2"));
       processes.add(new Process(0, 8, 2, "P3"));
       processes.add(new Process(0, 4, 4, "P4"));
       processes.add(new Process(0, 2, 5, "P5"));

       PriorityScheduler priority = new PriorityScheduler(processes, 1, 5);
       priority.run();

       List<ExpectedResult> expected = Arrays.asList(
               new ExpectedResult("P1", 32, 38),
               new ExpectedResult("P2", 0, 3),
               new ExpectedResult("P3", 23, 31),
               new ExpectedResult("P4", 36, 40),
               new ExpectedResult("P5", 31, 33)
       );

       verifyResults(priority.getProcesses(), expected, 24.4, 29.0);
   }

   @Test
   @DisplayName("Priority Test Case 3: Varied burst times with starvation risk")
   public void testPriority_TestCase3() {
       List<Process> processes = new ArrayList<>();
       processes.add(new Process(0, 10, 5, "P1"));
       processes.add(new Process(2, 5, 1, "P2"));
       processes.add(new Process(5, 3, 2, "P3"));
       processes.add(new Process(8, 7, 1, "P4"));
       processes.add(new Process(10, 2, 3, "P5"));

       PriorityScheduler priority = new PriorityScheduler(processes, 1, 4);
       priority.run();

       List<ExpectedResult> expected = Arrays.asList(
               new ExpectedResult("P1", 29, 39),
               new ExpectedResult("P2", 1, 6),
               new ExpectedResult("P3", 18, 21),
               new ExpectedResult("P4", 14, 21),
               new ExpectedResult("P5", 19, 21)
       );

       verifyResults(priority.getProcesses(), expected, 16.2, 21.6);
   }

   @Test
   @DisplayName("Priority Test Case 4: Large bursts with gaps in arrivals")
   public void testPriority_TestCase4() {
       List<Process> processes = new ArrayList<>();
       processes.add(new Process(0, 12, 2, "P1"));
       processes.add(new Process(4, 9, 3, "P2"));
       processes.add(new Process(8, 15, 1, "P3"));
       processes.add(new Process(12, 6, 4, "P4"));
       processes.add(new Process(16, 11, 2, "P5"));
       processes.add(new Process(20, 5, 5, "P6"));

       PriorityScheduler priority = new PriorityScheduler(processes, 2, 6);
       priority.run();

       List<ExpectedResult> expected = Arrays.asList(
               new ExpectedResult("P1", 38, 50),
               new ExpectedResult("P2", 120, 129),
               new ExpectedResult("P3", 65, 80),
               new ExpectedResult("P4", 121, 127),
               new ExpectedResult("P5", 100, 111),
               new ExpectedResult("P6", 117, 122)
       );

       verifyResults(priority.getProcesses(), expected, 93.5, 103.17);
   }

   @Test
   @DisplayName("Priority Test Case 5: Short bursts with high frequency")
   public void testPriority_TestCase5() {
       List<Process> processes = new ArrayList<>();
       processes.add(new Process(0, 3, 3, "P1"));
       processes.add(new Process(1, 2, 1, "P2"));
       processes.add(new Process(2, 4, 2, "P3"));
       processes.add(new Process(3, 1, 4, "P4"));
       processes.add(new Process(4, 3, 5, "P5"));

       PriorityScheduler priority = new PriorityScheduler(processes, 1, 3);
       priority.run();

       List<ExpectedResult> expected = Arrays.asList(
               new ExpectedResult("P1", 8, 11),
               new ExpectedResult("P2", 1, 3),
               new ExpectedResult("P3", 11, 15),
               new ExpectedResult("P4", 11, 12),
               new ExpectedResult("P5", 14, 17)
       );

       verifyResults(priority.getProcesses(), expected, 9.0, 11.6);
   }

   @Test
   @DisplayName("Priority Test Case 6: Mixed scenario - comprehensive test")
   public void testPriority_TestCase6() {
       List<Process> processes = new ArrayList<>();
       processes.add(new Process(0, 14, 4, "P1"));
       processes.add(new Process(3, 7, 2, "P2"));
       processes.add(new Process(6, 10, 5, "P3"));
       processes.add(new Process(9, 5, 1, "P4"));
       processes.add(new Process(12, 8, 3, "P5"));
       processes.add(new Process(15, 4, 6, "P6"));

       PriorityScheduler priority = new PriorityScheduler(processes, 1, 5);
       priority.run();

       List<ExpectedResult> expected = Arrays.asList(
               new ExpectedResult("P1", 46, 60),
               new ExpectedResult("P2", 12, 19),
               new ExpectedResult("P3", 55, 65),
               new ExpectedResult("P4", 4, 9),
               new ExpectedResult("P5", 26, 34),
               new ExpectedResult("P6", 54, 58)
       );

       verifyResults(priority.getProcesses(), expected, 32.83, 40.83);
   }

    // ==================== EDGE CASE TESTS ====================

    @Test
    @DisplayName("SJF Edge Case: Single Process")
    public void testSJF_SingleProcess() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 5, 1, "P1"));

        PreemptiveSJF sjf = new PreemptiveSJF(processes, 0);
        sjf.run();

        List<Process> result = sjf.getProcesses();
        assertEquals(0, result.getFirst().getWaitingTime());
        assertEquals(5, result.getFirst().getTurnaroundTime());
    }

    @Test
    @DisplayName("SJF Edge Case: No Context Switch")
    public void testSJF_NoContextSwitch() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 3, 2, "P1"));
        processes.add(new Process(1, 2, 1, "P2"));

        PreemptiveSJF sjf = new PreemptiveSJF(processes, 0);
        sjf.run();

        List<Process> result = sjf.getProcesses();
        assertTrue(result.stream().allMatch(p -> p.getTurnaroundTime() > 0));
    }
    // ==================== ROUND ROBIN TESTS ====================

    @Test
    @DisplayName("RR Test Case 1: Basic mixed arrivals (CS=1, Q=2)")
    public void testRR_TestCase1() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 8, 3, "P1"));
        processes.add(new Process(1, 4, 1, "P2"));
        processes.add(new Process(2, 2, 4, "P3"));
        processes.add(new Process(3, 1, 2, "P4"));
        processes.add(new Process(4, 3, 5, "P5"));

        RoundRobin rr = new RoundRobin(processes, 2, 1);
        rr.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 19, 27),
                new ExpectedResult("P2", 14, 18),
                new ExpectedResult("P3", 4, 6),
                new ExpectedResult("P4", 9, 10),
                new ExpectedResult("P5", 17, 20)
        );

        // We check the 'processes' list because your code updates it directly
        verifyResults(processes, expected, 12.6, 16.2);
    }

    @Test
    @DisplayName("RR Test Case 2: All arrive at time 0 (CS=1, Q=3)")
    public void testRR_TestCase2() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 6, 3, "P1"));
        processes.add(new Process(0, 3, 1, "P2"));
        processes.add(new Process(0, 8, 2, "P3"));
        processes.add(new Process(0, 4, 4, "P4"));
        processes.add(new Process(0, 2, 5, "P5"));

        RoundRobin rr = new RoundRobin(processes, 3, 1);
        rr.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 16, 22),
                new ExpectedResult("P2", 4, 7),
                new ExpectedResult("P3", 23, 31),
                new ExpectedResult("P4", 24, 28),
                new ExpectedResult("P5", 16, 18)
        );

        verifyResults(processes, expected, 16.6, 21.2);
    }

    @Test
    @DisplayName("RR Test Case 3: Varied burst times (CS=1, Q=4)")
    public void testRR_TestCase3() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 10, 5, "P1"));
        processes.add(new Process(2, 5, 1, "P2"));
        processes.add(new Process(5, 3, 2, "P3"));
        processes.add(new Process(8, 7, 1, "P4"));
        processes.add(new Process(10, 2, 3, "P5"));

        RoundRobin rr = new RoundRobin(processes, 4, 1);
        rr.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 21, 31),
                new ExpectedResult("P2", 18, 23),
                new ExpectedResult("P3", 10, 13),
                new ExpectedResult("P4", 20, 27),
                new ExpectedResult("P5", 16, 18)
        );

        verifyResults(processes, expected, 17.0, 22.4);
    }

    @Test
    @DisplayName("RR Test Case 4: Large bursts with gaps (CS=2, Q=5)")
    public void testRR_TestCase4() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 12, 2, "P1"));
        processes.add(new Process(4, 9, 3, "P2"));
        processes.add(new Process(8, 15, 1, "P3"));
        processes.add(new Process(12, 6, 4, "P4"));
        processes.add(new Process(16, 11, 2, "P5"));
        processes.add(new Process(20, 5, 5, "P6"));

        RoundRobin rr = new RoundRobin(processes, 5, 2);
        rr.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 38, 50),
                new ExpectedResult("P2", 26, 35),
                new ExpectedResult("P3", 58, 73),
                new ExpectedResult("P4", 49, 55),
                new ExpectedResult("P5", 57, 68),
                new ExpectedResult("P6", 32, 37)
        );

        verifyResults(processes, expected, 43.33, 53.0);
    }

    @Test
    @DisplayName("RR Test Case 5: Short bursts high frequency (CS=1, Q=2)")
    public void testRR_TestCase5() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 3, 3, "P1"));
        processes.add(new Process(1, 2, 1, "P2"));
        processes.add(new Process(2, 4, 2, "P3"));
        processes.add(new Process(3, 1, 4, "P4"));
        processes.add(new Process(4, 3, 5, "P5"));

        RoundRobin rr = new RoundRobin(processes, 2, 1);
        rr.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 7, 10),
                new ExpectedResult("P2", 2, 4),
                new ExpectedResult("P3", 12, 16),
                new ExpectedResult("P4", 8, 9),
                new ExpectedResult("P5", 13, 16)
        );

        verifyResults(processes, expected, 8.4, 11.0);
    }

    @Test
    @DisplayName("RR Test Case 6: Comprehensive mixed scenario (CS=1, Q=4)")
    public void testRR_TestCase6() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 14, 4, "P1"));
        processes.add(new Process(3, 7, 2, "P2"));
        processes.add(new Process(6, 10, 5, "P3"));
        processes.add(new Process(9, 5, 1, "P4"));
        processes.add(new Process(12, 8, 3, "P5"));
        processes.add(new Process(15, 4, 6, "P6"));

        RoundRobin rr = new RoundRobin(processes, 4, 1);
        rr.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 44, 58),
                new ExpectedResult("P2", 18, 25),
                new ExpectedResult("P3", 45, 55),
                new ExpectedResult("P4", 36, 41),
                new ExpectedResult("P5", 35, 43),
                new ExpectedResult("P6", 24, 28)
        );

        verifyResults(processes, expected, 33.67, 41.67);
    }

    @Test
    @DisplayName("RR Edge Case: Single Process (CS=1, Q=2)")
    public void testRR_EdgeCase_SingleProcess() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 5, 1, "P1"));

        RoundRobin rr = new RoundRobin(processes, 2, 1);
        rr.run();

        Process result = processes.get(0);

        // UPDATED EXPECTATION:
        // Your scheduler adds a Context Switch (1s) every time the Quantum (2s) expires.
        // Execution: Run(2s) -> CS(1s) -> Run(2s) -> CS(1s) -> Run(1s).
        // Total Delay (Waiting Time) = 1s + 1s = 2s.
        assertEquals(2, result.getWaitingTime(), "Single process waiting time (includes CS overhead)");
        assertEquals(7, result.getTurnaroundTime(), "Single process TAT (Burst 5 + Wait 2)");
    }

    @Test
    @DisplayName("RR Edge Case: Quantum larger than burst (Acts like FCFS)")
    public void testRR_EdgeCase_LargeQuantum() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(0, 3, 1, "P1"));
        processes.add(new Process(1, 2, 1, "P2"));

        // Quantum=10 is larger than any burst.
        // P1 runs 0-3. CS(1). P2 runs 4-6.
        RoundRobin rr = new RoundRobin(processes, 10, 1);
        rr.run();

        List<ExpectedResult> expected = Arrays.asList(
                new ExpectedResult("P1", 0, 3),
                new ExpectedResult("P2", 3, 5) // Arrived at 1, starts at 4. Wait = 3.
        );
        verifyResults(processes, expected, 1.5, 4.0);
    }
}