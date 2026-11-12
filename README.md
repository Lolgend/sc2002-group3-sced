# Restaurant Rush (Stage 1)

A turn-based command-line restaurant management game in Java (JDK 17+).

## Repository

[Lolgend/sc2002-group3-sced](https://github.com/Lolgend/sc2002-group3-sced)

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


---

# Restaurant Rush (Stage 2)

Stage 2 adds VIP and Critic customers with different waiting penalties, menu preferences and billing rules.

- **Regular customers**: Lose 8 satisfaction points per waiting turn and pay the full price.
- **VIP customers**: Lose 5 satisfaction points per waiting turn and receive a 10% discount, rounded to the nearest cent.
- **Critic customers**: Lose 12 satisfaction points per waiting turn and pay the full price. They choose the cheapest menu item; ties are resolved by choosing the first item in menu order.

The six arrival turns are 1, 4, 7, 10, 13 and 16. Customer 2 is a VIP arriving on turn 4, and Customer 5 is a Critic arriving on turn 13. The other four customers are Regular customers.

## Reproducible Test Runs

For turn-by-turn inputs to reproduce deterministic victory and defeat outcomes for stage-2, see the [Stage 2 Test Notes](test_notes/stage-2.md).

## Sequence Diagram

[View the Stage 2 Sequence Diagram](diagrams/stage-2/Stage_2_Sequence_Diagram.png) · [PlantUML source](diagrams/stage-2/Stage%202%20Sequence%20Diagram.puml)

The diagram shows the restaurant service flow, VIP pricing, and how the manager's service priority affects customer satisfaction and final statistics.

## Stage 2 Change Analysis

### 1. Delegate order billing to customers

[Commit 61ec7a1](https://github.com/Lolgend/sc2002-group3-sced/commit/61ec7a18f5b3ca7777951304c237081e06b9b22b) modifies `Customer.java` and `Waiter.java`. `Customer` gains `calculateBillCents()`, which returns the full menu price by default and can be overridden by `VIPCustomer` to apply a 10% discount. `Waiter.takeOrder()` now delegates bill calculation to the customer instead of reading the chosen menu item's price directly.

In Stage 1, using the menu price directly was sufficient because all customers paid the same price. Stage 2 introduces customer-specific billing, so this responsibility is placed in `Customer`. This supports the Single Responsibility Principle (SRP) by keeping customer pricing rules separate from the waiter's order-taking workflow, while the default implementation preserves full-price billing for Regular customers.

### 2. Add the VIP customer subclass

[Commit 61b3af1](https://github.com/Lolgend/sc2002-group3-sced/commit/61b3af11af22822e0bd265d654e05d9029ee7f78) adds `VIPCustomer.java`, which extends `Customer`. It overrides `getCustomerType()` to return `"VIP"` and `getWaitingSatisfactionLoss()` to return 5, while inheriting the existing customer state and lifecycle methods.

The original `Customer` design makes this addition straightforward because `applyTurnEndSatisfactionDecay()` already calls the abstract `getWaitingSatisfactionLoss()` method to obtain the penalty. Through dynamic binding, the inherited decay method uses the VIP subclass's value automatically. The shared logic for reducing satisfaction and handling abandonment can therefore be reused without modification. This separation between the common process and the variable penalty supports the Open–Closed Principle (OCP): the new waiting behaviour is added through a subclass while the existing decay algorithm remains unchanged.

### 3. Apply the VIP discount

[Commit 4cd9d36](https://github.com/Lolgend/sc2002-group3-sced/commit/4cd9d361a456825e18acd18f3afc7013664d1362) modifies `VIPCustomer.java` to override `calculateBillCents()`. The method applies a 10% discount to the parent calculation and rounds the result to the nearest cent.

The benefit of applying the Single Responsibility Principle (SRP) in Change 1 becomes clearer here. The waiter delegates price calculation to the customer, placing the discount rule alongside the customer behaviour it depends on. This matches the Stage 2 requirement that different customer types can pay different prices, while the waiter remains responsible for taking orders.

The extension approach based on the Open–Closed Principle (OCP) discussed in Change 2 makes this addition natural. `VIPCustomer` overrides the billing method, and dynamic binding allows the existing call in `Waiter.takeOrder()` to use the discounted calculation automatically. `Order` already accepts the calculated price and stores it in a private, final field, so it can retain the discounted amount for payment. Only `VIPCustomer` needs modification for this step.

### 4. Add the Critic customer subclass

[Commit cc66194](https://github.com/Lolgend/sc2002-group3-sced/commit/cc661949ddc372660ef4a9dde4c87d4bdf0a34f7) adds `CriticCustomer.java`, which extends `Customer`. It overrides `getCustomerType()` to return `"Critic"` and `getWaitingSatisfactionLoss()` to return 12.

This follows the same subclass extension approach as Change 2, reusing the existing customer lifecycle and satisfaction handling. The Critic inherits the default billing method because it pays the full price, so no billing override is needed.

### 5. Define the Critic's menu preference

[Commit 835bc4e](https://github.com/Lolgend/sc2002-group3-sced/commit/835bc4ed4c88255ae19bf9cde807a357b8a01e26) modifies `CriticCustomer.java` to override `chooseMenuItem()`. The Critic follows a preference for the cheapest menu item, which we defined and documented as its distinct order preference. If prices are tied, it chooses the first item in menu order.

The existing overridable method allows this preference to be added without changing the waiter's order-taking flow. The `random` parameter is retained to match the parent method's signature, although the Critic does not use it because its selection follows a fixed price-based rule.

### 6. Integrate mixed customer arrivals

[Commit b43a1eb](https://github.com/Lolgend/sc2002-group3-sced/commit/b43a1eb1de08794c29643340f3723e9858196cc1) modifies `Restaurant.java` and `Main.java`. `Restaurant.createCustomer()` creates a VIP for Customer 2, a Critic for Customer 5 and Regular customers for the remaining arrivals. `Main` updates the displayed stage title.

We considered updating the existing creation method directly or adding a `MixedCustomerRestaurant` subclass that overrides it. Since Stage 2 introduces a different mix of customer types, we treated this as an adjustment to the game's customer creation setup. Manual §4.4 explicitly expects small registration or setup changes when extending earlier stages. We therefore chose to update `createCustomer()` directly: the setup determines which customer types participate in the new stage, while the stable arrival and service processes remain reusable. This allows the implementation to follow the new stage requirements without introducing a separate restaurant subclass solely to preserve the previous arrival arrangement.

The original separation of customer creation from arrival timing and queue insertion makes this a small, local change. The conditions select which customer objects to create, while their waiting penalties, menu preferences and billing rules remain in the customer subclasses. This balances simplicity with extensibility: the arrival setup is updated directly, while the shared service flow continues to use polymorphism without customer-type checks in the controller.

### Regression check

Regular customers still pay the full menu price, and orders retain the bill calculated when they are taken. The existing arrival schedule, staff task limits, service lifecycle and 18-turn ending rules are also retained. We checked the integrated Stage 2 game using the reproducible victory and defeat runs in the [Stage 2 Test Notes](test_notes/stage-2.md). Both runs completed all 18 turns with the documented results, providing regression evidence for the shared game flow.


---

# Restaurant Rush (Stage 3)

Stage 3 adds a Host, combo meals and a Happy Hour pricing policy.

- **Host**: Seats at most one queued customer per turn, in addition to the Waiter's task. The Manager chooses the Host's task before the Waiter's, and the Waiter can still seat customers.
- **ComboMeal**: Made of two existing menu items. Preparation units are the sum of both components, and the price is 90% of their combined price, rounded to the nearest cent. The base menu offers Salad + Burger ($21.60, 2 units), Salad + Pasta ($24.30, 3 units) and Burger + Pasta ($29.70, 3 units).
- **Combo choice rule**: Regular and VIP customers choose uniformly at random from all six menu items using the seeded random generator. The Critic still chooses the cheapest item, so it never orders a combo.
- **Happy Hour**: The Manager may activate it once per game, at the start of a turn. It lasts for that turn and the next, and the start-of-turn display shows the remaining duration. Orders taken while it is active get 20% off, applied after the VIP discount and locked when the order is taken. During each active turn, customers still waiting for a table, an order or food recover 15 satisfaction at turn start, up to 100.

## Reproducible Test Runs

For turn-by-turn inputs to reproduce deterministic victory and defeat outcomes for stage-3, see the [Stage 3 Test Notes](test_notes/stage-3.md).

## Stage 3 Change Analysis

### 1. Add the ComboMeal menu item

[Commit 019fb97](https://github.com/Lolgend/sc2002-group3-sced/commit/019fb9758a1ed6525c4da1797cd7d1003fda3b3b) adds `ComboMeal.java`, which implements the existing `MenuItem` interface and is composed of two `MenuItem` components. Its name, price and preparation units are derived from the components.

Because `Order`, `Chef` and `ConsoleUI` already depend only on the `MenuItem` interface, a combo can be ordered, cooked and displayed without changing those classes. This reflects the Dependency Inversion Principle (DIP): high-level order handling depends on the abstraction rather than on `Dish`. Since `ComboMeal` can replace a `Dish` wherever a `MenuItem` is expected, it also follows the Liskov Substitution Principle (LSP).

### 2. Register combo meals in the base menu

[Commit c77590e](https://github.com/Lolgend/sc2002-group3-sced/commit/c77590e92163ca46c1799214e0d70df65ae46cb4) modifies `Menu.createBaseMenu()` to build the three combos from the existing dishes and add them to the menu. It also removes the old comment that listed only the three base dishes.

This is a small registration change, as anticipated by Manual §4.4. The customers' existing `chooseMenuItem()` methods already select from the full menu, so the combo choice rule needs no change to the customer classes.

### 3. Add the Happy Hour pricing policy

[Commit 6626a96](https://github.com/Lolgend/sc2002-group3-sced/commit/6626a96d4e43f96e7a6fe3b23d60fc0418ccf4b5) adds `HappyHour.java`. It encapsulates the once-per-game rule, the two-turn duration, the 20% discount and the 15-point recovery value.

All Happy Hour state is private and changes only through `activate()` and `endTurn()`, so other classes cannot put it into an invalid state. Keeping the policy in its own class supports the Single Responsibility Principle (SRP): customers keep their own pricing rules, while the time-limited promotion is managed separately.

### 4. Apply the Happy Hour discount when taking orders

[Commit f4ca599](https://github.com/Lolgend/sc2002-group3-sced/commit/f4ca599adc36c2e9d2c97c820db5a325b90cb3ba) modifies `Restaurant.java`, `Waiter.java` and `GameController.java`. `Restaurant` now owns a `HappyHour`, and `Waiter.takeOrder()` passes the customer's bill through `HappyHour.applyDiscount()`.

This builds directly on Stage 2 Change 1. The customer still calculates its own bill, including the VIP discount, and Happy Hour is applied to that result. The required order of discounts is therefore expressed in a single line, and the final price is still stored in the `final` field of `Order`, so orders placed before activation are not affected.

### 5. Add Happy Hour activation, display and satisfaction recovery

[Commit 6ba6000](https://github.com/Lolgend/sc2002-group3-sced/commit/6ba600057663a22c3c77d8bcdcc4c0f3a9091da8) modifies `Customer.java`, `Restaurant.java`, `GameController.java` and `ConsoleUI.java`.

- `Customer` gains `recoverSatisfaction()`, which reuses `isWaitingForService()`, the same rule that decides which customers lose satisfaction.
- `Restaurant` gains `processHappyHourRecovery()`.
- `GameController` offers the activation choice at the start of each turn while Happy Hour is still available, and calls `endTurn()` after the summary.
- `ConsoleUI` displays the Happy Hour status and remaining turns.

The activation option is only offered while Happy Hour is available, in the same way the Waiter and Chef menus only list legal actions. The rule itself stays in `HappyHour`, and the UI only collects the choice and displays the state.

### 6. Add the Host staff role

[Commit d46ef6e](https://github.com/Lolgend/sc2002-group3-sced/commit/d46ef6e41e56db112804e433b603015182a1a022) adds `Host.java` and `HostAction.java`, and modifies `Restaurant.java`, `GameController.java` and `ConsoleUI.java`. `Host` extends `Staff`, and `HostAction` follows the same value-object pattern as `ChefAction`.

By extending `Staff`, the Host inherits the existing one-action-per-turn rule without modification, which follows the Open–Closed Principle (OCP). The Waiter's legal actions are built after the Host acts, so a table seated by the Host is no longer offered to the Waiter.

We considered moving the shared seating logic into `Staff` so that `Waiter` and `Host` reuse it. We decided against it because it would change the stable Stage 1 `Waiter` class for a two-line duplication, and the Waiter's seating method also validates the queue in a way the Host does not need.

### Regression check

The six-step turn sequence, staff task limits, VIP and Critic behaviour, satisfaction decay, abandonment, payment and the 18-turn ending rules are unchanged. VIP orders still receive the 10% discount (for example, $19.44 for a Salad + Burger Combo), and the Critic still chooses Salad.

Because the menu now contains six items and each turn has extra Happy Hour and Host prompts, the Stage 2 input sequences do not replay the same game in Stage 3. New victory and defeat runs are recorded in the [Stage 3 Test Notes](test_notes/stage-3.md), and both complete all 18 turns with the documented results.
