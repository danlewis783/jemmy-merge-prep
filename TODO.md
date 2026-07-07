# TODO — remaining upstream port candidates

Source: three-way analysis (2026-07-05) of this fork against the common ancestor
(Jemmy 2.2.7.5, build 2007-06-12) and [openjdk/jemmy-v2](https://github.com/openjdk/jemmy-v2).
All upstream *bug fixes* judged applicable have already been ported (tiers 1 and 2,
see git history circa 2026-07-05). What remains is features, platform hardening,
and reliability follow-ups.

Each item has a mnemonic. To pick one up later, reference it by name
(e.g. "port `tooltip-operator`").

| Mnemonic | Summary | Recommendation |
|---|---|---|
| `internal-frame-api-driver` | LAF-immune internal frame driver using JInternalFrame API | **Do proactively** if internal frames matter |
| `scenario-test-cleanup` | Give the jemmy_nnn/Application_nnn pairs purpose, names, and deduplication | Incremental; fold, rename, or delete |
| `assertj-migration` | Convert all assertions to AssertJ, killing try/fail/catch idioms first | Incremental; exception tests first |
| `coverage-parity` | Verify this fork has all of jemmy's test coverage; keep a cross-repo test name map | Do after scenario-test-cleanup stabilizes names |
| `winlaf-button-test` | Run JInternalFrameOperatorTest under Windows LAF once | Cheap sanity check, ~minutes |
| `filechooser-accessible-names` | Accessible-name based file list selection + LAF/Mac handling | Only if non-Windows or non-default LAF |
| `internal-frame-popup-driver` | Title-actions-in-popup LAF support (Motif-style) | Only if such a LAF is ever used |
| `tooltip-operator` | JToolTipOperator convenience API | Only when a test wants it |
| `click-on-reference` | Click hyperlink in JEditorPane by anchor | Only when a test wants it |
| `color-chooser-accessors` | JColorChooserOperator subcomponent getters | Only when a test wants it |
| `table-change-selection` | JTableOperator.changeSelection mapping | Trivial, on demand |
| `window-count-wait` | Wait until N windows match a predicate | Trivial with functions/, on demand |

---

## Reliability follow-ups (not upstream ports)

### `scenario-test-cleanup`

`src/userInterfaceTest/.../testing/` holds 39 numbered legacy scenario tests
(`jemmy_001` … `jemmy_048`, with gaps) driving 36 numbered fixtures
(`Application_nnn` in `testFixtures`). The names convey nothing, each class is
usually a single `@Test`, coverage overlaps the per-operator test classes to an
unknown degree.
Goal: every surviving test has a focused, coherent, encapsulated purpose and a
name that states it.

Suggested approach:

1. **Inventory** — one line per `jemmy_nnn`: what does it exercise that the
   corresponding `*OperatorTest` does not? (~40 small files; a read-through,
   not tooling.)
2. **Classify and act:**
   - *Pure duplicate* of operator-test coverage → delete test and fixture.
   - *Unique single-operator behavior* → move into the matching
     `*OperatorTest` as a well-named test method; delete the numbered pair.
   - *Genuine multi-operator workflow* (e.g. dialog + combo + list flows in
     `jemmy_001`) → keep as a scenario test, but rename both halves for the
     workflow: `jemmy_037`/`Application_037` → e.g.
     `TabbedPaneScrollbarScrollingTest`/`TabbedScrollbarsApp`.
3. **Fixtures follow their tests** — name `Application_nnn` for the UI it
   builds; if only one test uses a trivial fixture, inline it into the test
   class and reserve `testFixtures` for genuinely shared apps.
4. **Traceability** — the numbering gaps (008, 012–014, 023, 034, 044–046)
   show pruning has already happened; record old→new name mappings in commit
   messages so history against the legacy NetBeans suite stays searchable.
5. **Do it incrementally** — batch by operator area, or opportunistically
   whenever a numbered test breaks or is touched.

**Recommendation: incremental; batch by operator area.**

### `assertj-migration`

Convert all test assertions to AssertJ fluent assertions (`assertj-core`
3.27.7, already in `gradle/libs.versions.toml`). Current state (2026-07-06):
86 of 93 `userInterfaceTest` files and 3 of 8 `test` files still use
`org.junit.jupiter.api.Assertions`.

Priority order:

1. **try/fail/catch idioms first** — at least `JFrameOperatorTest`,
   `JListOperatorTest`, `JSpinnerOperatorTest`, `jemmy_001/002/018/021/029`
   use the pre-JUnit-4 pattern, several with `fail("did not work")` and a
   meaningless `assertTrue(true)` in the catch block:

   ```java
   try {
       operator1.clickOnItem("blabla", StringComparators.strict());
       fail("did not work");
   } catch (JListOperator.NoSuchItemException ex) {
       assertEquals("No such item as \"blabla\"", ex.getMessage());
   }
   ```

   becomes

   ```java
   assertThatExceptionOfType(JListOperator.NoSuchItemException.class)
           .isThrownBy(() -> operator1.clickOnItem("blabla", StringComparators.strict()))
           .withMessage("No such item as \"blabla\"");
   ```

2. **Mechanical conversions** — `assertEquals`/`assertTrue`/`assertNotNull`
   → `assertThat(...)`, per test class, opportunistically alongside
   `scenario-test-cleanup` edits rather than as one big
   diff.

Notes:

- Converges test style with the C:\dev\jemmy fork, which already had its
  AssertJ refactor ("Refactor tests to AssertJ 3.27.7 fluent assertions") —
  one less textual difference for the eventual merge.

**Recommendation: incremental; exception-shaped tests first, the rest
class-by-class when touched.**

### `winlaf-button-test`

The tooltip-based internal frame button lookup (ported 2026-07-05) is only
test-verified under Metal, the suite default. If the company application runs
the Windows look and feel, run `JInternalFrameOperatorTest` once under it —
LAF dependence was the point of that fix.

**Recommendation: cheap one-off sanity check.**

---

## Platform / LAF hardening (upstream fixes, latent on Windows + default LAF)

### `filechooser-accessible-names`

`JFileChooserOperator.selectFile` selects by raw list index. Upstream filters
the file list by accessible name (CODETOOLS-7902413), fixes a macOS NPE in
`selectFile` (7901960), and handles LAF differences (7902339). Latent here
because the suite runs Windows + Metal; becomes real on macOS or other LAFs.

**Recommendation: port the series together if the platform matrix ever grows.**

### `internal-frame-api-driver`

Upstream `InternalFrameAPIDriver` (7902202) implements Window/Frame/InternalFrame
driver operations directly through the `JInternalFrame` API (`setMaximum`,
`setIcon`, …) instead of pushing title buttons — immune to LAF entirely, and
consistent with this fork's preference for API drivers. Upstream installs it as
the default internal frame driver.

**Recommendation: worth doing even on Windows if internal frames are used much;
pair with a driver-installation choice (default vs opt-in).**

### `internal-frame-popup-driver`

Upstream `InternalFramePopupMenuDriver` (7902300) supports LAFs where title
actions live in a popup menu (Motif-style) rather than title buttons.

**Recommendation: skip unless such a LAF is ever in play.**

---

## Feature ports (adopt on demand, each is self-contained)

### `tooltip-operator`

Upstream `JToolTipOperator` (7902278) — 500-line operator with
`showToolTip`/`waitJToolTip`, plus the `WaitToolTipTimeout` mechanism (7902342).
This fork already has tooltip predicates (`JToolTipWindowPredicate`,
`TooltipIsVisibleAndShowingPredicate`, `TrimmingTooltipPredicate`), so this is
convenience API, not a capability gap. Upstream `JToolTipOperatorTest` adapts
as the test.

### `click-on-reference`

`JEditorPaneOperator.clickOnReference(reference)` (7902156) — scroll to and
click a hyperlink by anchor name in an HTML `JEditorPane`. Upstream ships
`JEditorPaneOperatorTest` plus two small HTML fixtures.

### `color-chooser-accessors`

`JColorChooserOperator` subcomponent accessor methods (7901925, ~335 lines of
getters for the chooser panels/fields). Port only if tests interact with color
choosers beyond the existing `enterColor` helpers.

### `table-change-selection`

`JTableOperator.changeSelection(int, int, boolean, boolean)` mapping through
the queue (from 7901960's diff). Five-minute add following the existing
`Caller.of` mapping pattern.

### `window-count-wait`

Upstream `WindowWaiter.waitWindowCount(chooser, count)` / `countWindows`
(from 7902020): wait until the number of windows matching a predicate reaches
an expected count. `WindowWaiter` was deleted in this fork; recreate with the
`functions/` machinery when a test needs "wait until N dialogs exist."

---

## Merge preparation

### `coverage-parity`

Full test-coverage analysis of `C:\dev\jemmy` (the openjdk/jemmy-v2 fork,
~17 test classes across `test`/`userInterfaceTest`) versus this repository
(~100 test classes), ensuring this fork has **all** the coverage jemmy has,
with a durable, documented key mapping each jemmy test to its
differently-named equivalent here.

Deliverable: `TEST-COVERAGE-MAP.md` — one row per jemmy test class (method
granularity where classes diverge):

| jemmy test | equivalent here | status |
|---|---|---|
| e.g. `TimeoutsGetSetTest` | `TimeoutsTest` | covered |
| e.g. `JToolTipOperatorTest` | — | N/A until `tooltip-operator` |

Method:

1. **Inventory** jemmy's test classes and classify each test method:
   - *covered, same name* — no action;
   - *covered, different name* — record the mapping (the point of the key);
   - *N/A, subsystem replaced or feature not ported* — record the rationale
     and cross-reference the TODO mnemonic
     (`JToolTipOperatorTest` → `tooltip-operator`,
     `JEditorPaneOperatorTest` → `click-on-reference`,
     `DumpTest`/`AcessibleDumpPropertiesTest`/`UIStatusTest` →
     the test-layer `DumpOnFailure` extension (done 2026-07-06),
     `ComponentChooserTest` → Predicate-based search);
   - *gap* — port the test, adapting to this fork's API
     (`Predicate`/`FunctionRunner`/`TimeoutKey`).
2. **Quantitative cross-check** — name matching misses semantic gaps; run
   JaCoCo per-class line coverage of the shared `src/main` classes in both
   repos and diff the per-class percentages to flag classes jemmy's tests
   exercise more thoroughly.
3. **Adopt `LookAndFeelProvider`** from jemmy's test tree while here — it
   parameterizes tests across installed LAFs and directly serves
   `winlaf-button-test`.
4. **Keep the map current** — update it whenever `scenario-test-cleanup`
   renames a test here, so the key never goes stale.

**Recommendation: do after (or alongside) `scenario-test-cleanup` so the
mapping is written against stable, meaningful names. This item directly
serves the repository's purpose — driving the two forks toward each other.**

