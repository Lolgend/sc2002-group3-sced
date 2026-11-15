# Conversation History

## User

Analyze this repo extensively.

With reference to:
@SC2002_Group_Project_Briefing.pdf
@SC2002 Restaurant Rush Report.pdf

Perform an analysis at our attempt at this project. Flag out anything that we've missed from the reference docs, any mistakes we make or any drift from what the project demands. Mark each findings with a severity and reccomended fix

## Assistant

I have analyzed the repository, git history/tags, UML diagrams, `README.md`, test notes, Java source code, [SC2002_Group_Project_Briefing.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002_Group_Project_Briefing.pdf), [SC2002 Restaurant Rush Report.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002%20Restaurant%20Rush%20Report.pdf), and the assignment manual in [assignment_extracted.txt](file:///D:/Repos/sc2002-group3-sced/.scratch/assignment_extracted.txt).

> **Note:** `java` and `javac` are not available in this environment's `PATH`, so I could not compile or execute the game in this session; all findings below are verified by inspecting the source files, PlantUML files, git objects/refs, PDFs, and documentation directly.

---

## Executive Summary

Your core Java architecture and Stage 1 → Stage 2 → Stage 3 game logic align well with the assignment rules (arrivals, 18-turn loop, integer-cent pricing, decoupled `CustomerState`/`OrderStatus`, polymorphic `Customer` and `MenuItem`, `Host`, `ComboMeal`, and `HappyHour`).

However, there are **several critical and high-severity submission/process compliance gaps** around **AI history requirements, git tagging/links, and report change-evidence** that directly affect rubric criteria (*Thinking and development process* and *Design evidence*).

---

## Findings by Severity

### Critical

#### 1. Missing Stage 1 AI History & Non-Compliant Stage 2 AI History Summary
- **Reference:** [SC2002_Group_Project_Briefing.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002_Group_Project_Briefing.pdf) Slides 7, 8, 12 (*"All project-related prompts, AI replies and follow-up exchanges... Keep the full conversation in order. A summary alone is insufficient."*).
- **Observed in repo:**
  - In [README.md](file:///D:/Repos/sc2002-group3-sced/README.md#L32), Stage 1 has a broken empty link: `| Stage 1 | [stage-1-ai-history.txt]() |`, and `.git/refs/remotes/origin/` only contains `main`, `xiekang`, and `chuxin`—there is no personal branch or AI chat log for ZhiWei (Stage 1).
  - On `origin/xiekang` (`b2cc338`), `stage-2-ai-history.txt` (42 lines) explicitly states: *"Selected exchanges, translated from Chinese and condensed for readability. Each INPUT / OUTPUT pair combines related exchanges from the actual project conversation; these are edited summaries, not verbatim quotations or a complete transcript."* This directly violates *"Keep the full conversation in order. A summary alone is insufficient."*
- **Recommended Fix:**
  1. Create ZhiWei's personal branch (e.g., `zhiwei`) and upload his complete, ordered Stage 1 AI chat transcripts (e.g., `ai-history/stage-1/chat-01.md`).
  2. Replace XieKang's condensed `stage-2-ai-history.txt` on `xiekang` with the full, unedited chronological prompt/response transcript (original language is fine, or full transcript + translation).
  3. Update [README.md](file:///D:/Repos/sc2002-group3-sced/README.md#L30-L34) so all three active members' links resolve to valid files on their personal branches. *(Also note that your 4th member's complete absence is already documented on Page 1 of the report.)*

#### 2. `stage-3` Git Tag Is Stale (6 Commits Behind `main`) and Earlier Tags Miss Final Diagram/Doc Updates
- **Reference:** [SC2002_Group_Project_Briefing.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002_Group_Project_Briefing.pdf) Slides 3 & 8; [assignment_extracted.txt](file:///D:/Repos/sc2002-group3-sced/.scratch/assignment_extracted.txt#L192-L195) (*"Tags stage-1, stage-2 and stage-3 on runnable commits."*).
- **Observed in repo:**
  - `stage-3` (`.git/refs/tags/stage-3`) points to commit `d392088`, which precedes 6 commits on `main` (`92368de`, `7bdc0e4`, `6b0e641`, `2c9163d`, `7119226`, `172368a`). Consequently, checking out `stage-3` gives outdated Stage 1/2/3 class diagrams (before the multiplicity fixes in `6b0e641`), an incomplete `README.md` (missing repo URL, Stage 2/3 AI links, video link/timestamps), and no `Video Presentation.mp4`. *(The Java code at `d392088` is identical to `main`.)*
  - Similarly, `stage-1` (`ea6a7fc`) precedes `3dcc13e` (which added expected results to `test_notes/stage-1.md`) and `59c5c9e`–`21c0576` (which simplified/fixed `Stage 1 Class Diagram.puml`), and `stage-2` (`c01838f`) precedes `e90c211` (which added `Stage 2 Sequence Diagram.puml`) and `6b0e641` (multiplicity fixes).
- **Recommended Fix:**
  - Move the `stage-3` tag to the final `main` commit (after fixing `README.md`/code/diagram items below) and push `git push origin -f stage-3` as an annotated tag.
  - Keep `stage-1` (`ea6a7fc`) and `stage-2` (`c01838f`) on their runnable code snapshots (so you don't leak later-stage features), and document in `README.md` that diagram polish commits happened afterward on `main`.

---

### High

#### 3. `README.md` Missing Stage Tag Links, GitHub Compare Links, and Complete File-Change Lists
- **Reference:** [SC2002_Group_Project_Briefing.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002_Group_Project_Briefing.pdf) Slide 8 (*"README: link to every stage and every member's AI history"*); [assignment_extracted.txt](file:///D:/Repos/sc2002-group3-sced/.scratch/assignment_extracted.txt#L194) (*"In the README, for each transition (1 → 2 and 2 → 3): new files; existing files changed or deleted, with reasons; the relevant commit or compare link."*).
- **Observed in repo:**
  - [README.md](file:///D:/Repos/sc2002-group3-sced/README.md) has individual commit links for Stage 2 and Stage 3 changes, **but contains no links to the `stage-1`, `stage-2`, and `stage-3` tags** and **no GitHub compare links** (`.../compare/stage-1...stage-2` and `.../compare/stage-2...stage-3`).
  - Under `## Stage 3 Change Analysis` ([README.md](file:///D:/Repos/sc2002-group3-sced/README.md#L153-L202)), [Main.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/Main.java) is omitted even though it was modified from Stage 2 to Stage 3 (banner & `Host` intro text). Both Stage 2 and Stage 3 change lists also omit newly added `test_notes/` and `diagrams/` files.
- **Recommended Fix:**
  - Add a "Stages & Comparisons" table in `README.md` linking directly to `tree/stage-1`, `tree/stage-2`, `tree/stage-3`, `compare/stage-1...stage-2`, and `compare/stage-2...stage-3`.
  - Add `Main.java` (plus `test_notes/` and `diagrams/` files) to the Stage 2 / Stage 3 transition lists.

#### 4. Report Missing Dedicated Stage-to-Stage Change Evidence & Regression Section
- **Reference:** [SC2002_Group_Project_Briefing.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002_Group_Project_Briefing.pdf) Slide 4 (*"Compare stage-1 with stage-2, then stage-2 with stage-3: What did you add? What did you modify? What still works?"*) and Slide 9 (*"Include stage diagrams, change evidence and results"*).
- **Observed in [SC2002 Restaurant Rush Report.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002%20Restaurant%20Rush%20Report.pdf):**
  - While Section 3.2 discusses OOD principles per stage, there is no structured `stage-1 -> stage-2` and `stage-2 -> stage-3` change-evidence table/section in the report. For instance:
    - Section 1.2 does not mention `Customer`, `Waiter`, `Restaurant`, or `Main` being modified in Stage 2.
    - Section 1.3 states *"targeted updates to 7 existing classes"* without naming any of the 7 classes (`Main`, `ConsoleUI`, `GameController`, `Customer`, `Menu`, `Restaurant`, `Waiter`).
    - There are no explicit regression-check summaries in the report showing what Stage 1/2 behavior still works, and no GitHub tag/compare links.
- **Recommended Fix:** Add a concise "Stage Evolution & Change Evidence" subsection (covering `stage-1 → stage-2` and `stage-2 → stage-3`) with a table of Added Classes, Modified Classes + Reasons, Regression Checks, Design Impact, and GitHub compare links.

#### 5. Report Missing Stage 1 Functional Evidence & Cites a Stale Stage 3 Commit
- **Reference:** [SC2002_Group_Project_Briefing.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002_Group_Project_Briefing.pdf) Slide 3 & Slide 9; [test_notes/stage-1.md](file:///D:/Repos/sc2002-group3-sced/test_notes/stage-1.md).
- **Observed in [SC2002 Restaurant Rush Report.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002%20Restaurant%20Rush%20Report.pdf):**
  - Section 4 (Page 17) states excerpts are from Stage 2 (`c01838f`) and Stage 3 (`a358c7b`). Section 4.5 (`Reproducible Victory and Defeat Runs`, Page 24) includes Stage 2 and Stage 3 final outcomes, **but omits Stage 1's victory ($63.00, 74.4 avg satisfaction) and defeat results completely**.
  - Stage 3 replays cite WIP commit `a358c7b` (`"almost done. added docs and tests"`) instead of the tagged `stage-3` commit.
- **Recommended Fix:** Add Stage 1's reproducible victory and defeat outcomes to Section 4.5 (and note its regression baseline), and update the Stage 3 commit reference to the final `stage-3` tag commit hash.

#### 6. Declarations Required for Submission Are Not in Repo (Submission Zip Checklist)
- **Reference:** [SC2002_Group_Project_Briefing.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002_Group_Project_Briefing.pdf) Slide 12; [assignment_extracted.txt](file:///D:/Repos/sc2002-group3-sced/.scratch/assignment_extracted.txt#L198-L212).
- **Observed:** `Signed Declaration of Original Work` and each active member's `Declaration on Use of GAI Assistance` are not present in the workspace/repo. (They may be kept outside git to put directly into `LABGroup_AssignmentGrp.zip`, so this is a submission-blocking reminder.)
- **Recommended Fix:** Ensure the signed Declaration of Original Work and the 3 GAI Assistance declarations are included inside the final `LABGroup_AssignmentGrp.zip` archive.

---

### Medium

#### 7. Factual Error & Missing Classes in Report Design Discussion
- **Observed in [SC2002 Restaurant Rush Report.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002%20Restaurant%20Rush%20Report.pdf):**
  - **Page 14 (Section 3.3.1):** Calls the order enum **`OrderState`** (*"CustomerState with 7 states & OrderState with 5 states"*). In [OrderStatus.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/model/OrderStatus.java), the enum is named **`OrderStatus`**.
  - **Pages 9–10 (Table 3.1, Key Abstractions):** Omits [Main.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/Main.java), [HostAction.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/HostAction.java), [WaiterAction.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/WaiterAction.java), [WaiterActionType.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/WaiterActionType.java), and [ChefAction.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/ChefAction.java) (as well as enums `CustomerState` and `OrderStatus`).
- **Recommended Fix:** Change `OrderState` to `OrderStatus` in Section 3.3.1, and add the Action value-object classes and `Main` (and optionally the two state enums) to Table 3.1.

#### 8. Stage 1 Individual Reflection (ZhiWei) Lacks Concrete Code Examples
- **Reference:** [SC2002_Group_Project_Briefing.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002_Group_Project_Briefing.pdf) Slide 10 (*"How did you solve it? Describe your attempts, solution and what you learned. Support your reflection with examples from your own work."*).
- **Observed in [SC2002 Restaurant Rush Report.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002%20Restaurant%20Rush%20Report.pdf) Page 25 (Section 5.1):** Unlike Sections 5.2 and 5.3—which reference concrete methods and design alternatives—Section 5.1 speaks in generalities about avoiding god classes and over-engineering without citing actual Stage 1 classes/refactorings (e.g., extracting `GameConfig` and `GameResult` from `Restaurant`/`GameController`, or designing `Customer.applyTurnEndSatisfactionDecay()`).
- **Recommended Fix:** Expand Section 5.1 with 1–2 concrete Stage 1 refactoring examples from ZhiWei's commits (such as extracting `GameConfig`/`GameResult` or decoupling `ConsoleUI` from victory evaluation).

#### 9. Tiny/Low-Readability Class Diagrams in the PDF Report
- **Reference:** [assignment_extracted.txt](file:///D:/Repos/sc2002-group3-sced/.scratch/assignment_extracted.txt#L239) (*"Class diagram: clarity and match to code"*).
- **Observed in [SC2002 Restaurant Rush Report.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002%20Restaurant%20Rush%20Report.pdf) Pages 4–6:** Because [Stage 1 Class Diagram.puml](file:///D:/Repos/sc2002-group3-sced/diagrams/stage-1/Stage%201%20Class%20Diagram.puml), [Stage 2 Class Diagram.puml](file:///D:/Repos/sc2002-group3-sced/diagrams/stage-2/Stage%202%20Class%20Diagram.puml), and [Stage 3 Class Diagram.puml](file:///D:/Repos/sc2002-group3-sced/diagrams/stage-3/Stage%203%20Class%20Diagram.puml) use `left to right direction`, embedding the wide PNGs onto portrait A4 pages shrinks class attributes and method signatures to barely legible sizes.
- **Recommended Fix:** Place the class diagrams on landscape pages in the report (or embed `.svg`/higher-DPI cropped panels) so every attribute, visibility symbol, and multiplicity is crisp and readable.

#### 10. Validation & State-Atomicity Defects in Stage 3 / Staff Java Code
- **Observed in source code:**
  1. [Host.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/staff/Host.java#L17-L21): `seatCustomer()` lacks the `Objects.requireNonNull` checks and `!queue.getCustomers().contains(customer)` guard that [Waiter.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/staff/Waiter.java#L24-L37) has.
  2. [HostAction.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/controller/HostAction.java#L23-L27): `createSeat()` lacks `Objects.requireNonNull` checks (unlike `WaiterAction.createSeat()`).
  3. [Waiter.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/staff/Waiter.java#L39-L47): `takeOrder()` omits `Objects.requireNonNull(happyHour, "HappyHour cannot be null")`.
  4. [ComboMeal.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/model/ComboMeal.java#L7-L10): constructor lacks null checks on `firstItem` and `secondItem`, and omits `equals`/`hashCode`/`toString` (which `Dish` provides).
  5. **Non-atomic `markActed()` ordering across all staff classes:** In `Host.seatCustomer`, `Waiter.seatCustomer`/`takeOrder`/`serveOrder`, `Chef.prepareOrder`, and `Cashier.processOldestReadyPayment`, `markActed()` is called *after* mutating `Table`, `Customer`, `WaitingQueue`, or `Order`. If a caller invokes a staff method twice in one turn, domain state is partially mutated before `IllegalStateException` is thrown.
- **Recommended Fix:** Add the missing null/queue guards in `Host`, `HostAction`, `Waiter`, and `ComboMeal`, and validate `!hasActedThisTurn()` at the start of each staff action method before mutating any domain objects.

---

### Low

#### 11. Minor `README.md`, Video Timestamp, and Report Polish Issues
- **Orphaned sentence in [README.md](file:///D:/Repos/sc2002-group3-sced/README.md#L93):** Line 93 (*"The diagram shows the restaurant service flow..."*) sits under `# Stage 2 Implementation` without naming the Sequence Diagram.
- **Video segment indexing ([README.md](file:///D:/Repos/sc2002-group3-sced/README.md#L13-L19)):** Currently lists the 5 Slide 11 buckets rather than explicitly noting sub-timestamps for the 7 items in [assignment_extracted.txt](file:///D:/Repos/sc2002-group3-sced/.scratch/assignment_extracted.txt#L215-L230) (especially segment 7: *"The stage tag comparisons and the diagrams"*). Make sure the video covers tag comparisons on GitHub and list sub-timestamps if helpful.
- **Report typos & grammar in [SC2002 Restaurant Rush Report.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002%20Restaurant%20Rush%20Report.pdf):**
  - Page 1: `absenceabsence` → `absence`
  - Page 4: `architecture..` → `architecture.` (and Page 6 missing period after `GitHub`)
  - Page 11: `Restaurant. createCustomer()` → `Restaurant.createCustomer()`; `finalPriceCents are final` → `finalPriceCents is final`
  - Page 16: `PatiencesStrategy` → `PatienceStrategy`, `CusomterPricingStrategy` → `CustomerPricingStrategy`, `over architecture` → `overall architecture`, `Why the current design chosen?:` → `Why was the current design chosen?`

#### 12. Minor Code & UML Consistency Items
- [Customer.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/model/Customer.java#L87-L93): `recoverSatisfaction(int points)` returns `true` even when satisfaction is already `100` (so `ConsoleUI.displayHappyHourRecovery` logs *"recovered up to 15 satisfaction (Satisfaction: 100)"* for newly arrived customers). Checking `if (!isWaitingForService() || satisfaction >= 100) return false;` avoids noisy output (note: updating this would change console log lines if you re-capture evidence).
- [Restaurant.java](file:///D:/Repos/sc2002-group3-sced/src/main/java/restaurant/model/Restaurant.java#L183-L199): `recordPayment(Order)` does not guard against recording the same `PAID` order twice if invoked directly outside `GameController`.
- [Stage 3 Class Diagram.puml](file:///D:/Repos/sc2002-group3-sced/diagrams/stage-3/Stage%203%20Class%20Diagram.puml#L190-L247): Inconsistently lists `getHost()` and `getHappyHour()` on `Restaurant` while omitting `getWaiter()`, `getChef()`, `getCashier()`, and omits `GameResult` record accessors that were shown in Stage 1 and Stage 2 diagrams.
- [Stage 2 Sequence Diagram.puml](file:///D:/Repos/sc2002-group3-sced/diagrams/stage-2/Stage%202%20Sequence%20Diagram.puml#L248-L251): Turns 4–8 are shown directly under `runGame()`, whereas Turns 9–18 switch to `loop turn = 9..18` calling `runTurn(turn)`.

#### 13. Leftover Build Artifact in Repository Root
- **Observed:** `FindCleanVictory$1.class` (a leftover compiled scratch file) sits in the repository root alongside `out/`.
- **Recommended Fix:** Delete `FindCleanVictory$1.class` and exclude `out/`, `.scratch/`, `.agents/`, `AGENTS.md`, `docs/`, `GLOSSARY.md`, `skills-lock.json`, and the briefing `.pdf`/`.docx` when building `LABGroup_AssignmentGrp.zip`.

---

## Priority Checklist Before Submission (Due 15 Nov 2026, 11:59 PM SGT)

1. **Fix AI histories:** Upload ZhiWei's full Stage 1 chat log on a `zhiwei` branch and replace XieKang's summary on `xiekang` with the full ordered transcript; fix the Stage 1 link in [README.md](file:///D:/Repos/sc2002-group3-sced/README.md#L32).
2. **Fix [README.md](file:///D:/Repos/sc2002-group3-sced/README.md):** Add links to `stage-1`, `stage-2`, `stage-3` tags and GitHub compare links (`stage-1...stage-2`, `stage-2...stage-3`), plus `Main.java` in Stage 3 change analysis.
3. **Update [SC2002 Restaurant Rush Report.pdf](file:///D:/Repos/sc2002-group3-sced/SC2002%20Restaurant%20Rush%20Report.pdf):** Add a Stage 1→2 and 2→3 Change Evidence & Regression section, add Stage 1 victory/defeat results to Section 4.5, fix `OrderState` → `OrderStatus` and typos, enlarge class diagrams (landscape pages), add concrete Stage 1 examples to ZhiWei's reflection, and update the Stage 3 commit reference.
4. **Update `stage-3` tag:** Re-tag `stage-3` onto the final `main` commit after your fixes so the tag includes the final code, diagrams, `README.md`, and video.
5. **Clean zip & include Declarations:** Remove `FindCleanVictory$1.class`, package clean source/README/report/video into `LABGroup_AssignmentGrp.zip`, and include the signed Original Work & GAI Assistance declarations.

## User

Export this conversation history, just my input and your output in an md

