## Stage 1 Reproducible Runs

The base game uses a fixed seed (`DEFAULT_SEED = 42L`), so the same sequence of Manager choices across all 18 turns (Waiter and Chef prompts each turn; Cashier acts automatically) always produces the exact same outcome.

### Deterministic Victory Run

Prioritizes serving ready orders, taking orders from seated customers, seating waiting customers, and cooking active orders:

```text
Turn 1 Waiter: 1
Turn 1 Chef: 0
Turn 2 Waiter: 1
Turn 2 Chef: 1
Turn 3 Waiter: 0
Turn 3 Chef: 1
Turn 4 Waiter: 2
Turn 4 Chef: 0
Turn 5 Waiter: 1
Turn 5 Chef: 0
Turn 6 Waiter: 1
Turn 6 Chef: 1
Turn 7 Waiter: 2
Turn 7 Chef: 0
Turn 8 Waiter: 1
Turn 8 Chef: 0
Turn 9 Waiter: 1
Turn 9 Chef: 1
Turn 10 Waiter: 2
Turn 10 Chef: 0
Turn 11 Waiter: 1
Turn 11 Chef: 0
Turn 12 Waiter: 1
Turn 12 Chef: 1
Turn 13 Waiter: 1
Turn 13 Chef: 1
Turn 14 Waiter: 2
Turn 14 Chef: 0
Turn 15 Waiter: 1
Turn 15 Chef: 1
Turn 16 Waiter: 1
Turn 16 Chef: 0
Turn 17 Waiter: 1
Turn 17 Chef: 0
Turn 18 Waiter: 1
Turn 18 Chef: 1
```

### Deterministic Defeat Run

Selects `0` (`Wait`) for both Waiter and Chef on every turn so customers eventually lose all satisfaction and abandon the restaurant:

```text
Turn 1 Waiter: 0
Turn 1 Chef: 0
Turn 2 Waiter: 0
Turn 2 Chef: 0
Turn 3 Waiter: 0
Turn 3 Chef: 0
Turn 4 Waiter: 0
Turn 4 Chef: 0
Turn 5 Waiter: 0
Turn 5 Chef: 0
Turn 6 Waiter: 0
Turn 6 Chef: 0
Turn 7 Waiter: 0
Turn 7 Chef: 0
Turn 8 Waiter: 0
Turn 8 Chef: 0
Turn 9 Waiter: 0
Turn 9 Chef: 0
Turn 10 Waiter: 0
Turn 10 Chef: 0
Turn 11 Waiter: 0
Turn 11 Chef: 0
Turn 12 Waiter: 0
Turn 12 Chef: 0
Turn 13 Waiter: 0
Turn 13 Chef: 0
Turn 14 Waiter: 0
Turn 14 Chef: 0
Turn 15 Waiter: 0
Turn 15 Chef: 0
Turn 16 Waiter: 0
Turn 16 Chef: 0
Turn 17 Waiter: 0
Turn 17 Chef: 0
Turn 18 Waiter: 0
Turn 18 Chef: 0
```
