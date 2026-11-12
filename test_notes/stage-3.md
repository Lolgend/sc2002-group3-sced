## Stage 3 Reproducible Runs

The base game uses a fixed seed (`DEFAULT_SEED = 42L`). Start a new game for each replay and follow the same sequence of Manager choices across all 18 turns.

Each turn has these prompts, in order:

1. **Happy Hour**: shown only while Happy Hour has not been used. `0` keeps it off, `1` activates it.
2. **Host**: `0` waits, other options seat a queued customer.
3. **Waiter**: `0` waits, other options seat, take an order or serve.
4. **Chef**: `0` waits, other options cook an order.

The Cashier acts automatically.

The six arrival turns remain 1, 4, 7, 10, 13 and 16. C2 arrives as a VIP on turn 4, and C5 arrives as a Critic on turn 13. The other four customers are Regular customers.

The base menu now contains three dishes and three combo meals. Regular and VIP customers choose uniformly at random from all six items using the seeded random generator. The Critic still chooses the cheapest item, which is Salad.

### Deterministic Victory Run

The Host seats each new arrival, the Waiter takes the order in the same turn, and the Chef starts cooking immediately. Happy Hour is activated on turn 13, so the Critic's Salad is taken at 20% off ($7.20).

```text
Turn 1 Happy Hour: 0
Turn 1 Host: 1
Turn 1 Waiter: 1
Turn 1 Chef: 1
Turn 2 Happy Hour: 0
Turn 2 Host: 0
Turn 2 Waiter: 0
Turn 2 Chef: 1
Turn 3 Happy Hour: 0
Turn 3 Host: 0
Turn 3 Waiter: 1
Turn 3 Chef: 0
Turn 4 Happy Hour: 0
Turn 4 Host: 1
Turn 4 Waiter: 1
Turn 4 Chef: 1
Turn 5 Happy Hour: 0
Turn 5 Host: 0
Turn 5 Waiter: 0
Turn 5 Chef: 1
Turn 6 Happy Hour: 0
Turn 6 Host: 0
Turn 6 Waiter: 1
Turn 6 Chef: 0
Turn 7 Happy Hour: 0
Turn 7 Host: 1
Turn 7 Waiter: 1
Turn 7 Chef: 1
Turn 8 Happy Hour: 0
Turn 8 Host: 0
Turn 8 Waiter: 1
Turn 8 Chef: 0
Turn 9 Happy Hour: 0
Turn 9 Host: 0
Turn 9 Waiter: 0
Turn 9 Chef: 0
Turn 10 Happy Hour: 0
Turn 10 Host: 1
Turn 10 Waiter: 1
Turn 10 Chef: 1
Turn 11 Happy Hour: 0
Turn 11 Host: 0
Turn 11 Waiter: 0
Turn 11 Chef: 1
Turn 12 Happy Hour: 0
Turn 12 Host: 0
Turn 12 Waiter: 1
Turn 12 Chef: 0
Turn 13 Happy Hour: 1
Turn 13 Host: 1
Turn 13 Waiter: 1
Turn 13 Chef: 1
Turn 14 Host: 0
Turn 14 Waiter: 1
Turn 14 Chef: 0
Turn 15 Host: 0
Turn 15 Waiter: 0
Turn 15 Chef: 0
Turn 16 Host: 1
Turn 16 Waiter: 1
Turn 16 Chef: 1
Turn 17 Host: 0
Turn 17 Waiter: 1
Turn 17 Chef: 0
Turn 18 Host: 0
Turn 18 Waiter: 0
Turn 18 Chef: 0
```

Expected result:
- Outcome: VICTORY
- Paid Customers: 5 / 4 required [PASS]
- Total Revenue: $71.64 / $45.00 required [PASS]
- Avg Satisfaction: 90.0 / 60.0 required [PASS]
- Unhappy Departures: 0

Key events in this run:
- Turn 4: C2 (VIP) orders a Salad + Burger Combo for $19.44 ($24.00 × 0.9 combo price × 0.9 VIP discount).
- Turn 13: Happy Hour is activated. C5 (Critic) orders a Salad for $7.20 ($9.00 × 0.8).
- Turn 14: Happy Hour shows `1 turn remaining`. From turn 15 it shows `USED`.

### Deterministic Defeat Run

Selects `0` (`Wait` or `Keep Happy Hour off`) for every prompt. That is 72 inputs: four prompts per turn for 18 turns, because Happy Hour is never used. No customers are served or pay, so the game ends in defeat after all 18 turns.

```text
Turn 1 Happy Hour: 0
Turn 1 Host: 0
Turn 1 Waiter: 0
Turn 1 Chef: 0
Turn 2 Happy Hour: 0
Turn 2 Host: 0
Turn 2 Waiter: 0
Turn 2 Chef: 0
Turn 3 Happy Hour: 0
Turn 3 Host: 0
Turn 3 Waiter: 0
Turn 3 Chef: 0
Turn 4 Happy Hour: 0
Turn 4 Host: 0
Turn 4 Waiter: 0
Turn 4 Chef: 0
Turn 5 Happy Hour: 0
Turn 5 Host: 0
Turn 5 Waiter: 0
Turn 5 Chef: 0
Turn 6 Happy Hour: 0
Turn 6 Host: 0
Turn 6 Waiter: 0
Turn 6 Chef: 0
Turn 7 Happy Hour: 0
Turn 7 Host: 0
Turn 7 Waiter: 0
Turn 7 Chef: 0
Turn 8 Happy Hour: 0
Turn 8 Host: 0
Turn 8 Waiter: 0
Turn 8 Chef: 0
Turn 9 Happy Hour: 0
Turn 9 Host: 0
Turn 9 Waiter: 0
Turn 9 Chef: 0
Turn 10 Happy Hour: 0
Turn 10 Host: 0
Turn 10 Waiter: 0
Turn 10 Chef: 0
Turn 11 Happy Hour: 0
Turn 11 Host: 0
Turn 11 Waiter: 0
Turn 11 Chef: 0
Turn 12 Happy Hour: 0
Turn 12 Host: 0
Turn 12 Waiter: 0
Turn 12 Chef: 0
Turn 13 Happy Hour: 0
Turn 13 Host: 0
Turn 13 Waiter: 0
Turn 13 Chef: 0
Turn 14 Happy Hour: 0
Turn 14 Host: 0
Turn 14 Waiter: 0
Turn 14 Chef: 0
Turn 15 Happy Hour: 0
Turn 15 Host: 0
Turn 15 Waiter: 0
Turn 15 Chef: 0
Turn 16 Happy Hour: 0
Turn 16 Host: 0
Turn 16 Waiter: 0
Turn 16 Chef: 0
Turn 17 Happy Hour: 0
Turn 17 Host: 0
Turn 17 Waiter: 0
Turn 17 Chef: 0
Turn 18 Happy Hour: 0
Turn 18 Host: 0
Turn 18 Waiter: 0
Turn 18 Chef: 0
```

Expected result:
- Outcome: DEFEAT
- Paid Customers: 0 / 4 required [FAIL]
- Total Revenue: $0.00 / $45.00 required [FAIL]
- Avg Satisfaction: N/A / 60.0 required [FAIL]
- Unhappy Departures: 1
