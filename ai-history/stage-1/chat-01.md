# Conversation History

## User
Analyze the source code, should WaiterAction & ChefAction be classified under control class or misc? Why?

## Assistant
They should be classified under **Misc** (value/helper objects), **not** as **Control** classes.

### Why they are Misc, not Control

1. **Zero control or orchestration logic (pure immutable value objects)**
   - Both [WaiterAction.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/WaiterAction.java#L10-L78) and [ChefAction.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/ChefAction.java#L8-L50) are explicitly documented as *"Value object representing a selectable action..."*.
   - Each class only holds `private final` data fields (`type`/`wait`, `description`, and target entity references like `customer`, `table`, `order`), static factory methods (`createWait()`, `createSeat(...)`, `createCook(...)`, etc.), and getters/`toString()`.
   - Neither class has an `execute()` method, mutates any domain state, or coordinates between boundary and entity classes.

2. **All control responsibility lives in `GameController`**
   - In [GameController.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/GameController.java#L123-L280), `GameController` builds the legal `WaiterAction`/`ChefAction` lists (`buildLegalWaiterActions()`, `buildLegalChefActions()`), extracts their description strings to pass to [ConsoleUI.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/cli/ConsoleUI.java#L101), and then inspects `action.getType()` / `action.isWait()` inside `executeWaiterAction()` and `executeChefAction()` to invoke `Waiter`, `Chef`, `Restaurant`, and `ConsoleUI`.
   - Thus, `WaiterAction` and `ChefAction` act only as **typed parameter/menu-option carriers (DTOs)** between `GameController`'s option-building and execution methods.

3. **Package placement vs. ECB stereotype**
   - They are placed in `package restaurant.controller` because they are *controller-layer support types* used exclusively by `GameController` (not domain entities in `restaurant.model` and not UI in `restaurant.cli`).
   - However, in Entity–Control–Boundary (ECB) classification, **Control** describes an object's *architectural role* (coordinating a use case/flow), not merely its Java package. Marking DTOs as `<<control>>` would misrepresent them as active orchestrators alongside `GameController`.

4. **Consistent with the project's UML diagrams and Stage 3**
   - All three class diagrams ([Stage 1](file:///D:/Repos/sc2002-group3-sced/diagrams/stage-1/Stage%201%20Class%20Diagram.puml#L437-L445), [Stage 2](file:///D:/Repos/sc2002-group3-sced/diagrams/stage-2/Stage%202%20Class%20Diagram.puml#L458-L467), and [Stage 3](file:///D:/Repos/sc2002-group3-sced/diagrams/stage-3/Stage%203%20Class%20Diagram.puml#L504-L514)) stereotype only `GameController` as `<<control>>` and classify `WaiterAction`, `ChefAction`, and [HostAction.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/HostAction.java) under **Misc (`Misc (bootstrap, actions, config, result)`)** alongside `Main`, `GameConfig`, and `GameResult`.

*(Note: The only case where they would shift toward Control/Command is if the design were refactored into a true Command pattern where each action's `execute(...)` method owned the dispatch and orchestration logic instead of `GameController` switching on `WaiterActionType` / `isWait()`.)*

---

## User
Do they not implement Chef and Waiter logic?

## Assistant
No—they do **not** implement any Chef or Waiter logic. In fact, neither class even imports or references `Chef` or `Waiter`.

The actual responsibilities are split like this:

### 1. `Waiter` and `Chef` implement the staff logic (`restaurant.staff`)
- [Waiter.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/staff/Waiter.java#L24-L64) implements all Waiter operations and enforces the 1-task-per-turn rule via [Staff.markActed()](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/staff/Staff.java#L31-L36):
  - `seatCustomer(...)` (validates queue, seats customer at table, removes from queue, marks acted)
  - `takeOrder(...)` (asks customer to choose a menu item, applies VIP/Happy Hour pricing, creates `Order`, assigns to table, marks acted)
  - `serveOrder(...)` (marks `Order` and `Customer` as served, marks acted)
  - `waitTurn()` (marks acted)
- [Chef.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/staff/Chef.java#L23-L32) implements all Chef operations:
  - `prepareOrder(Order)` (calls `order.advancePreparation()` and `markActed()`)
  - `waitTurn()` (calls `markActed()`)

### 2. `GameController` implements action legality & dispatch (`restaurant.controller`)
- [GameController.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/GameController.java#L181-L280):
  - `buildLegalWaiterActions()` and `buildLegalChefActions()` contain the rules for *which* actions are currently legal and instantiate the action objects.
  - `executeWaiterAction(...)` switches on `action.getType()` and calls `waiter.waitTurn()`, `waiter.seatCustomer(...)`, `waiter.takeOrder(...)`, or `waiter.serveOrder(...)`.
  - `executeChefAction(...)` checks `action.isWait()` and calls `chef.waitTurn()` or `chef.prepareOrder(action.getOrder())`.

### 3. `WaiterAction` and `ChefAction` only hold menu option data
- [WaiterAction.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/WaiterAction.java#L12-L78) only bundles `(type, description, customer, table, order)` and formats the prompt string (e.g., `"Seat Customer #1 ... at Table #1"`).
- [ChefAction.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/ChefAction.java#L10-L50) only bundles `(wait, description, order)` and formats the prompt string (e.g., `"Cook Order #1 ..."`).

So they are strictly passive **data carriers (value objects)** describing a selectable choice—which is why they belong under **Misc** rather than **Control** (or **Entity**).

---

## User
Does it go against OODP?

## Assistant
**No, it does not go against OODP**—it is a reasonable design trade-off, though a strict SOLID review would note one minor trade-off (OCP) compared to the Command pattern.

### Why it aligns well with OODP

1. **Single Responsibility Principle (SRP) & Separation of Concerns**
   - [ConsoleUI.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/cli/ConsoleUI.java#L101) only takes `List<String>` options and returns an `int`—it knows nothing about `WaiterAction`, `ChefAction`, or legality rules.
   - [Waiter.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/staff/Waiter.java) and [Chef.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/staff/Chef.java) only execute domain actions—they are not polluted with CLI prompt formatting like `"Cook Order #1 (...)"`.
   - `WaiterAction` / `ChefAction` / `HostAction` act as an adapter/DTO layer between `GameController`'s option building and `ConsoleUI`'s string menu.

2. **Encapsulation & Immutability**
   - Both [WaiterAction.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/WaiterAction.java#L19-L52) and [ChefAction.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/ChefAction.java#L15-L32) hide their constructors (`private`) and expose validated static factory methods (`createWait()`, `createSeat(...)`, `createCook(...)`) with `Objects.requireNonNull(...)` guards.
   - All fields are `private final`, so an action cannot be mutated into an inconsistent state after creation.

3. **Appropriate use of polymorphism where variation actually exists**
   - In this codebase, polymorphism is applied to the axes that actually extend across Stage 2 and Stage 3 (`Customer` -> `RegularCustomer`/`VIPCustomer`/`CriticCustomer`, and `MenuItem` -> `Dish`/`ComboMeal`), avoiding unnecessary class explosion for fixed staff menus (aligning with the assignment brief in [assignment_extracted.txt](file:///D:/Repos/sc2002-group3-sced/.scratch/assignment_extracted.txt#L171): *"Do not create interfaces just to satisfy a checklist."*).

---

### The OODP trade-off (what could be critiqued)

If evaluated strictly against **OCP** and **polymorphism**:

1. **Type-tag `switch` instead of polymorphic Command objects**
   - [GameController.executeWaiterAction()](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/GameController.java#L226-L250) switches on `action.getType()` (`WaiterActionType`), and `executeChefAction()` branches on `action.isWait()`.
   - Adding a new Waiter action type (e.g., cleaning a table) would require editing `WaiterActionType`, `WaiterAction`, and `GameController`.
2. **Nullable union fields**
   - `WaiterAction` carries `customer`, `table`, and `order`, leaving unused ones `null` depending on `type` (e.g., `createWait()` sets all three to `null`). `ChefAction` likewise leaves `order` as `null` when `wait == true`.

### Summary
- **Current design (Value Object + `switch` in `GameController`)**: Keeps the codebase concise (`WaiterAction`, `ChefAction`, `HostAction` stay tiny and pure) because the Waiter's 4 actions and Chef's 2 actions never change across stages.
- **Alternative (Command pattern)**: Replace `WaiterAction`/`ChefAction` with a `StaffAction`/`Command` interface with an `execute(...)` method and separate subclasses per action. That removes the `switch` and `null` fields and improves OCP, at the cost of ~6–7 small command classes and pushing controller/UI wiring into commands.

In an SC2002 context, keeping them as value objects is **good OODP** as long as you recognize that choosing Value Objects over the Command pattern was a deliberate simplicity-vs-extensibility trade-off.
