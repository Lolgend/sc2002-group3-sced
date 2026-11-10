# Restaurant Rush (Stage 1)

A turn-based command-line restaurant management game in Java (JDK 17+).

## Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher (`javac` and `java` available in `PATH`).
- **Dependencies**: None (runs completely offline using the standard Java SE library).

---

## How to Compile and Run

### 1. Compile the Source Code

From the repository root directory:

```powershell
javac -d out -sourcepath src/main/java src/main/java/restaurant/Main.java
```


### 2. Run the Game (Interactive Mode)

```bash
java -cp out restaurant.Main
```

---

## Reproducible Test Runs

For turn-by-turn inputs to reproduce deterministic victory and defeat outcomes for stage-1, see the [Stage 1 Test Notes](test_notes/stage-1.md).

