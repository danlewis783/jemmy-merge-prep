# PLAN — one-shot execution of TODO.md

Self-contained execution plan for a fresh agent session. Read this file and
[TODO.md](TODO.md) fully before acting. TODO.md defines **what** each item is
(keyed by mnemonic); this file defines **order, method, verification,
decision defaults, and hazards**. When the two disagree, TODO.md's item
descriptions win for scope; this file wins for sequencing and process.

## Mission context

- **This repo** (`C:\dev\jemmy-merge-prep`): a company fork of NetBeans Jemmy,
  diverged since ~2007 from Jemmy 2.2.7.5. Core engine was rewritten
  (`FunctionRunner`/`Predicate`-based; `Waiter`/`ComponentChooser`/`Dumper`
  deleted); operators and drivers layers retained. Long-term goal: converge
  with the jemmy fork below to enable an eventual merge.
- **Reference repo** (`C:\dev\jemmy`): fork of openjdk/jemmy-v2 (`upstream`
  remote), modernized with the same Gradle/Spotless/JSpecify conventions as
  this repo. Use it read-only: as the source for ports and for
  `coverage-parity`. **Never commit or push there.**
- **Common ancestor** (`C:\dev\jemmy-from-sources-jar-2-2-7-5`): extracted
  Jemmy 2.2.7.5 sources. Rarely needed; read-only.
- Upstream fixes (tiers 1–2) were already ported 2026-07-05/06 — see git log.
  Everything outstanding is in TODO.md (17 items).

## Ground rules

- **Branch/remote:** work directly on `main`; push to `origin main` after
  each completed phase. Working tree must be clean between phases.
- **Commits:** one commit per TODO item (or per coherent sub-unit for large
  items). Imperative summary, body explaining what/why, ending with:
  `Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>`.
  When an item completes, **remove its row and section from TODO.md in the
  same commit** (for partially completed items, edit the item to reflect the
  remainder).
- **Build/verify commands** (Windows, PowerShell, from repo root):
  - `.\gradlew.bat spotlessApply` — run before every commit; code must be
    palantir-java-format clean. NullAway/JSpecify `@NullMarked` runs during
    `compileJava` — new main-source code must satisfy it.
  - `.\gradlew.bat test` — unit suite (`src/test`), fast, headless-safe.
  - `.\gradlew.bat userInterfaceTest --tests "*SomeTest"` — UI suite opens
    **real Swing windows on the desktop**; do not use targeted `--tests` runs
    while the user may be typing. Run the full UI suite only twice: baseline
    (phase 0) and final; use targeted classes otherwise.
- **Test conventions:** JUnit 5. Unit suite (`src/test`) uses AssertJ; UI
  suite historically JUnit assertions (being migrated: `assertj-migration`).
  Operator-state predicates live in `org.netbeans.jemmy.predicates` as public
  generic classes; operator method mappings use
  `QueueTool.getInstance().invokeSmoothly(Caller.of(...))`; timeouts are
  `TimeoutKey` enum entries with `Timeouts.get/override`.
- **Prove regression tests bite:** for any test added to pin a fix, verify it
  by temporarily reverting the fix (working tree only), watching the test
  fail, then restoring. Report this in the commit body.
- **Spotless side effects:** `spotlessApply` sometimes rewrites unrelated
  files with line-ending-only changes (content-identical, empty `git diff`).
  `git restore` such files before committing.
- **Known-flaky baseline** (verified on a clean tree 2026-07-05, not caused
  by any recent change): `jemmy_036` (focus handoff),
  `JPopupMenuOperatorTest.testRobot56091` (menu push timeout), and
  intermittent AWT scroll stalls in `jemmy_035`/`jemmy_037` (root cause
  addressed in phase 2). A full `userInterfaceTest` run failing **only** in
  these is the expected baseline, not a regression.

## Phases

Each phase ends: spotless clean → unit tests green → targeted UI classes
green → commit(s) → TODO.md updated → push.

### Phase 0 — preflight
1. `git status` clean; `git log --oneline -5` sanity check.
2. `.\gradlew.bat build -x userInterfaceTest` green.
3. Full `userInterfaceTest` run once; record which classes fail and confirm
   they are within the known-flaky baseline above. This is the comparison
   point for the final run.

### Phase 1 — `typed-driver-lookup` (design cleanup; unlocks later renames)
Replace string-based driver discovery with `Class`-based, per the TODO item:
`LightDriver.getSupported()` → `List<Class<? extends ComponentOperator>>`;
`DriverManager` gets a real `EnumMap<DriverType, Map<Class<?>, DriverMarker>>`
registry (out of the `JemmyProperties` string-keyed bag); lookup and
`checkSupported` collapse onto `isAssignableFrom` (the unused
`checkSupported(Class, Class[], Class)` overload shows the shape). Migrate all
~57 FQCN string literals to `.class` references. Watch for: external
registration API compatibility (`setDriver`/`removeDriver` signatures change —
update all in-repo callers; note the break in the commit body). Verification:
compile is the main proof; run unit suite + a spread of UI operator classes
(`JSliderOperatorTest`, `JInternalFrameOperatorTest`, `ComponentOperatorTest`).

### Phase 2 — `awt-adjustable-scroll` (kills a flaky root cause)
Rewrite `ScrollbarDriver`/`ScrollPaneDriver` scrolling to drive
`java.awt.Adjustable.setValue` directly (pattern: `JScrollBarAPIDriver`),
replacing the held-synthetic-mouse-press push-and-wait that native AWT
controls ignore on current JDK/Windows. Keep `AbstractScrollDriver`'s freeze
detection intact for remaining push-and-wait users. Verification: run
`jemmy_035`, `jemmy_037`, `ScrollbarOperatorTest`, `ScrollPaneOperatorTest`
several times (e.g. 3×) — they must pass consistently, not once.

### Phase 3 — `dump-on-failure`, then `flaky-ui-tests`
1. `dump-on-failure`: JUnit `TestWatcher` in `testFixtures` that on failure
   walks the hierarchy via `ComponentStreamer` and prints class/name/bounds/
   visible/showing/enabled/`hasFocus()`/accessible name/selected text.
   Test-layer only; also delete the ~15 unconsumed `*_DPROP` vestige constants
   from main sources (separate commit).
2. `flaky-ui-tests`: register the watcher on `jemmy_036` and
   `JPopupMenuOperatorTest`, reproduce (loop the single tests), diagnose with
   the dump output, fix root causes if tractable. If a root cause is not
   tractable in this session, document findings in TODO.md and rely on
   phase 4 tagging instead — do not sink unbounded time here.

### Phase 4 — `first-hygiene`
Per the TODO item: `@TempDir` for `JFileChooserOperatorTest` fixture files;
`@Isolated`/`@ResourceLock` on global-state unit tests (`TimeoutsTest`,
`LookAndFeelTest` Nimbus switch); `@Tag("flaky")` + default-`check`
exclusion for whatever remains flaky after phases 2–3; optional cooperative
cancellation in the `-1`-sentinel test adjusters.

### Phase 5 — `scenario-test-cleanup` (largest single item)
Inventory all 39 `jemmy_nnn` tests; classify each: duplicate → delete;
unique single-operator → fold into the matching `*OperatorTest` as a named
method; genuine workflow → rename test + fixture descriptively. Fixtures
follow their tests; record old→new mappings in commit messages. Batch commits
by operator area (not one mega-commit). Convert assertions to AssertJ in
every file touched (pre-payment on phase 6).

### Phase 6 — `assertj-migration`
Exception-shaped tests first (`try/fail/catch` →
`assertThatExceptionOfType(...).isThrownBy(...).withMessage(...)`) — files
listed in the TODO item; then mechanical `assertEquals`/`assertTrue`/
`assertNotNull` → `assertThat` for the remaining UI test classes not already
converted during phase 5. Keep `assertTimeoutPreemptively` (no AssertJ
equivalent). Commit in class batches; suite must stay green per batch.

### Phase 7 — `internal-frame-api-driver`
Port upstream `InternalFrameAPIDriver` (see `C:\dev\jemmy`
`src/main/.../drivers/windows/InternalFrameAPIDriver.java`), adapted to this
repo's driver API (now `Class`-based after phase 1). Decision default:
**install as the default internal frame driver** (as upstream does), keeping
`DefaultInternalFrameDriver` available; `JInternalFrameOperatorTest` (52
tests) must stay green — it is the behavioral contract.

### Phase 8 — `coverage-parity` + `winlaf-button-test`
1. Adopt `LookAndFeelProvider` from the jemmy fork's test tree (parameterizes
   tests across installed LAFs).
2. Use it to run `JInternalFrameOperatorTest` under Windows LAF once
   (`winlaf-button-test`); fix or document any LAF-specific failure.
3. Inventory jemmy's ~17 test classes; write `TEST-COVERAGE-MAP.md` per the
   TODO item's table format (covered same-name / covered different-name /
   N/A with mnemonic cross-reference / gap). Port the gaps, adapting to this
   repo's API. Names here are stable post-phase-5, so the map is durable.
4. JaCoCo per-class cross-check (add the plugin locally to both builds if
   quick; skip with a note in TEST-COVERAGE-MAP.md if it turns into a fight —
   the name-level map is the deliverable that matters).

### Phase 9 — feature ports (decision defaults)
- `tooltip-operator`, `click-on-reference`: **port them**, including
  upstream's `JToolTipOperatorTest`/`JEditorPaneOperatorTest` (+ HTML
  fixtures) — this converts their `coverage-parity` N/A rows into covered.
  Adapt `ComponentChooser`-based upstream code to `Predicate`.
- `table-change-selection`, `window-count-wait`: **port** (both are
  trivial; the second uses the `functions/` machinery).
- `color-chooser-accessors`: **skip** (bulk without a consumer); leave in
  TODO.md.

### Phase 10 — conditional items (default: skip, leave in TODO.md)
- `filechooser-accessible-names`, `internal-frame-popup-driver`: skip unless
  the platform matrix has grown beyond Windows + Metal/Windows LAF. Leave
  their TODO entries with a note that phases 1–9 did not change their status.

### Final gate
1. `.\gradlew.bat build -x userInterfaceTest` green.
2. Full `userInterfaceTest` run; compare against the phase-0 baseline —
   result must be equal or strictly better (flaky set shrunk or tagged out).
3. TODO.md contains only: skipped conditional items (phase 10, and
   `color-chooser-accessors`) and anything explicitly deferred with findings.
4. Everything pushed to `origin main`; report a phase-by-phase summary with
   commit hashes, the final flaky-set status, and the location of
   TEST-COVERAGE-MAP.md.

## Landmines (learned the hard way)

- UI tests are desktop-interactive: a user touching mouse/keyboard mid-run
  causes false failures, especially focus tests. Note run windows in the
  final report if results look anomalous.
- `Frame.getState()` only reports iconification; extended state is separate
  (`getExtendedState`) — don't "simplify" the `FrameOperator` waits.
- Internal frame title buttons are found by `UIManager` tooltip strings —
  correct per-LAF, which is why `winlaf-button-test` exists.
- `assertTimeoutPreemptively` abandons the executable's thread on failure —
  fine for regression pins, but don't wrap unbounded loops without noting it.
- The Gradle configuration cache is on; if it misbehaves after build-script
  edits (JaCoCo), `--no-configuration-cache` is acceptable for one-off runs.
- Timeout scaling (`jemmy.timeouts.scale`) was removed 2026-07-08 as an
  intentional omission: a global multiplier silently distorts every wait,
  including explicit overrides, making timing environment-dependent and
  failures hard to reproduce. Use `Timeouts.override(...)` per key instead.
- `jemmy.event_listening` was likewise removed 2026-07-08: it was a
  read-once negative option that silently degraded the event waits relying
  on the global listeners. `EventTool.addListeners()`/`removeListeners()`
  are the explicit controls. The only remaining system properties the
  library reads are `os.name` (via `Platform`) and Apple's own
  `apple.laf.useScreenMenuBar`.
- Reflective operator creation (`Operator.createOperator`,
  `addOperatorPackage`, `ContainerOperator.createSubOperator`) was removed
  2026-07-08 as an intentional omission: naming-convention reflection no
  typed caller needed, and it pinned the `(Component)` constructor to
  public, blocking the factory-method construction redesign.
- Operator construction is factory-based as of 2026-07-08: `X.of(component)`
  wraps a component you already have; `X.waitFor(...)` searches and waits
  (these were the searching constructors, which blocked for up to 60 s
  inside `this(...)` chains). Direct-hit constructors are package-private, so operator subclasses live in org.netbeans.jemmy.operators.
  When adding an operator subclass, declare its full `waitFor`/`of`
  overload set — statics don't override, so a missing overload silently
  resolves to the superclass variant and returns the supertype.
