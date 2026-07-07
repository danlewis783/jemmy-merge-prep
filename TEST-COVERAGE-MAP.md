# TEST-COVERAGE-MAP — jemmy fork tests vs this repository

Maps every test class in `C:\dev\jemmy` (the openjdk/jemmy-v2 fork) to its
equivalent here, per the `coverage-parity` plan. Statuses: **covered**
(same or different name), **N/A** (subsystem replaced or behavior rejected,
with rationale), **pending** (port planned). Update this file whenever a
test is renamed on either side.

| jemmy test | equivalent here | status |
|---|---|---|
| `TimeoutsGetSetTest` | `TimeoutsTest` (unit) | covered — get/set/override semantics via `TimeoutKey`/`TimeoutOverride` |
| `ComponentChooserTest` | — | N/A — `ComponentChooser` replaced by `java.util.function.Predicate`; default-`getDescription` behavior has no analogue |
| `OperatorTest` (waitState, waitStateOnQueue) | `OperatorTest` (unit) | covered — ported 2026-07-06 with the `waitStateOnQueue` feature |
| `ComponentOperatorTest` (waitComponentLocationOnScreen) | `ComponentOperatorTest.testWaitComponentLocationOnScreen` | covered — ported with the geometry waits |
| `TextComponentOperatorTest` (selection) | `TextComponentOperatorTest` | covered |
| `JTextComponentOperatorTest` (selection) | `JTextComponentOperatorTest` | covered |
| `FileChooserTest` (testSelection/testCount/testGoHome) | `JFileChooserOperatorTest` (testSelectFile/testGetFileCount/testWaitFileCount/testGoHome) | covered |
| `JInternalFrameOperatorTest` (ops + testTitleButtons across LAFs) | `JInternalFrameOperatorTest` + `InternalFrameTitleButtonsLafTest` | covered — LAF-parameterized via adopted `LookAndFeelProvider` |
| `JInternalFrameOperatorCloseTest` | `JInternalFrameOperatorTest.testClose` + `InternalFrameApiDriverTest.closeClosesTheFrame` | covered — `requestCloseAndThenHide` is unsupported in both implementations |
| `JEditorPaneOperatorTest` (clickOnReference ×2) | `JEditorPaneClickOnReferenceTest` | covered — ported 2026-07-07 with the HTML fixtures |
| `JToolTipOperatorTest` | — | pending — lands with the `tooltip-operator` port |
| `MenuTest` (showMenu, pushMenuNoBlock) | `MenuNavigationTest` + `JMenuBarOperatorTest` | covered |
| `ButtonsAndTooltipsTest` (testPush/testLookups) | `ButtonGridLookupTest` + `JButtonOperatorTest`; tooltip scenario pending with `tooltip-operator` | partial |
| `ComboBoxesAndListTest` | `DialogComboListWorkflowTest` + `JComboBoxOperatorTest` | covered |
| `MenuButtonTextTest` | `MenuNavigationTest` | covered — same scenario (ex-`jemmy_002`) |
| `UIStatusTest` | — | N/A — `UIStatus` mouse tracking rejected as production pollution; failure diagnostics live in the test-layer `DumpOnFailure` extension |
| `DumpTest` | — | N/A — `Dumper` subsystem was deleted; `DumpOnFailure` prints hierarchy snapshots on failure instead |
| `AcessibleDumpPropertiesTest` | — | N/A — accessible name/description appear in `DumpOnFailure` output rather than dump files |

Quantitative note: a JaCoCo per-class cross-check of shared `src/main`
classes was skipped (build-script churn not worth it while the name-level
map is fresh); revisit if a coverage regression is suspected.
