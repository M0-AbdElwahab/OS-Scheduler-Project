# CPU Scheduler Simulator

## Overview
Implementation of various CPU scheduling algorithms.

## Schedulers Implemented

1. **Preemptive Shortest Job First (SJF)**
2. **Priority Scheduling with Aging**
3. **Round Robin**
4. **AG Scheduler**

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
│       └── JSONTestRunner.java
├── test_cases/
│   ├── AG/
│   │   └── AG_test*.json (6 files)
│   └── Other_Schedulers/
│       └── test_*.json (6 files)
└── .gitignore
```

## Requirements

- Java JDK 21+
- Gson 2.10.1

## Running Tests

```bash
# Compile
javac -cp "lib\gson-2.10.1.jar;target\classes" -d target\test-classes src\test\java\JSONTestRunner.java

# Run tests
java -cp "lib\gson-2.10.1.jar;target\classes;target\test-classes" org.example.JSONTestRunner
```

## Building and Running

### Quick Start (Windows)
```bash
# Run everything
run.bat

# Or manually:
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

