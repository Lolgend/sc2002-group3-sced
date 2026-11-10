## Stage 2 Reproducible Runs

The base game uses a fixed seed (`DEFAULT_SEED = 42L`). Start a new game for each replay and follow the same sequence of Manager choices across all 18 turns. Each turn has a Waiter prompt and a Chef prompt; the Cashier acts automatically.

The six arrival turns remain 1, 4, 7, 10, 13 and 16. C2 arrives as a VIP on turn 4, and C5 arrives as a Critic on turn 13. The other four customers are Regular customers.

The Critic chooses the cheapest menu item. If multiple items have the same lowest price, it chooses the first one in menu order.

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

Expected result:
- Outcome: VICTORY
- Paid Customers: 5 / 4 required [PASS]
- Total Revenue: $62.10 / $45.00 required [PASS]
- Avg Satisfaction: 73.8 / 60.0 required [PASS]
- Unhappy Departures: 0

### Deterministic Defeat Run

Selects `0` (`Wait`) for both Waiter and Chef on every turn. No customers are served or pay, so the game ends in defeat after all 18 turns.

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

Expected result:
- Outcome: DEFEAT
- Paid Customers: 0 / 4 required [FAIL]
- Total Revenue: $0.00 / $45.00 required [FAIL]
- Avg Satisfaction: N/A / 60.0 required [FAIL]
- Unhappy Departures: 1