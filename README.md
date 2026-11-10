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


---

# Restaurant Rush (Stage 2)

Stage 2 adds VIP and Critic customers with different waiting penalties, menu preferences and billing rules.

- **Regular customers**: Lose 8 satisfaction points per waiting turn and pay the full price.
- **VIP customers**: Lose 5 satisfaction points per waiting turn and receive a 10% discount, rounded to the nearest cent.
- **Critic customers**: Lose 12 satisfaction points per waiting turn and pay the full price. They choose the cheapest menu item; ties are resolved by choosing the first item in menu order.

The six arrival turns are 1, 4, 7, 10, 13 and 16. Customer 2 is a VIP arriving on turn 4, and Customer 5 is a Critic arriving on turn 13. The other four customers are Regular customers.

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