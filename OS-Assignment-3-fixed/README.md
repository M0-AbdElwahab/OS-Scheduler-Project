# CPU Scheduler Simulator

## Overview
Implementation of various CPU scheduling algorithms for Operating Systems coursework.

## Schedulers Implemented

1. **Preemptive Shortest Job First (SJF)**
2. **Priority Scheduling with Aging**
3. **Round Robin**
4. **AG Scheduler** (Three-phase hybrid: FCFS → Priority → SJF)

## Project Structure

```
OS-Assignment-3-fixed/
├── pom.xml
├── README.md
├── run.bat
├── lib/
│   └── gson-2.10.1.jar
├── src/
│   ├── main/java/org/example/
│   │   ├── Process.java
│   │   ├── PreemptiveSJF.java
│   │   ├── PriorityScheduler.java
│   │   ├── RoundRobin.java
│   │   ├── AGScheduler.java
│   │   └── TestRunner.java
│   └── test/java/
│       └── SchedulerTest.java
├── test_cases/
│   ├── AG/
│   │   └── AG_test*.json (6 files)
│   └── Other_Schedulers/
│       └── test_*.json (6 files)
└── .gitignore
```

## Requirements

- Java JDK 21+
- Gson 2.10.1 (included in lib/)

## Building and Running

### Quick Start (Windows)
```bash
# Compile all files
javac -cp "lib/gson-2.10.1.jar" -d target/classes src/main/java/org/example/*.java

# Run all test cases
java -cp "lib/gson-2.10.1.jar;target/classes" org.example.TestRunner
```

### Using Maven (Alternative)
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="org.example.TestRunner"
```

## Output Format

Each scheduler outputs:
- Execution order
- Per-process waiting time and turnaround time  
- Average waiting time and turnaround time
- Quantum history (AG scheduler only)

