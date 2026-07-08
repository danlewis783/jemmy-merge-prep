# TODO — remaining upstream port candidates

Source: three-way analysis (2026-07-05) of this fork against the common ancestor
(Jemmy 2.2.7.5, build 2007-06-12) and [openjdk/jemmy-v2](https://github.com/openjdk/jemmy-v2).

**All items are complete as of 2026-07-08.** Resolutions, most recent first:

| Mnemonic | Resolution |
|---|---|
| `property-surface-cleanup` | Done 2026-07-08 — `jemmy.event_listening` is an **intentional omission** (upstream has it): a read-once negative option that silently degraded event waits; use `EventTool.addListeners()`/`removeListeners()` instead (see the `EventTool` class javadoc). Also removed the dead `jemmy.dump.a11y` write in `DialogComboListWorkflowTest` (nothing reads it — upstream's Dumper a11y feature was never ported), routed the raw `os.name` checks through `Platform`, dropped the SunOS visualizer branch, and replaced the string-keyed `JemmyProperties` map with typed fields (`CharBindingMap`, dispatching model) and typed statics on `Operator` (visualizer, path parser, verification); with the property map gone, `JemmyProperties` was renamed `JemmyContext` |
| `timeout-scale-removal` | Done 2026-07-08 — the `jemmy.timeouts.scale` multiplier is an **intentional omission** from this fork (upstream has it): a global multiplier silently distorts every wait, including explicit overrides, making timing environment-dependent and failures hard to reproduce; use `Timeouts.override(...)` per key instead (see the `Timeouts` class javadoc) |
| `assertj-migration` | Done 2026-07-07 — every JUnit value assertion in `test` and `userInterfaceTest` converted to AssertJ fluent assertions; only `assertTimeout`/`assertTimeoutPreemptively` remain JUnit, as timing guards with no AssertJ equivalent |
| `internal-frame-popup-driver` | Ported 2026-07-07 — `InternalFramePopupMenuDriver` + `getPopupButton()`, installed when Motif is the startup LAF (CODETOOLS-7902300) |
| `filechooser-accessible-names` | Ported 2026-07-07 — accessible-name file list matching, JTable details view, LAF-aware `goHome`/`getCancelButton` (CODETOOLS-7902413, 7901960, 7902339) |
| `color-chooser-accessors` | Ported 2026-07-07 — `JColorChooserOperator` tab-conditional spinner/slider/text-field getters (CODETOOLS-7901925) |
| `tooltip-operator` | Ported 2026-07-07 — `JToolTipOperator` with the `WaitToolTipTimeout` mechanism (CODETOOLS-7902278, 7902342) |

Earlier tiers (upstream bug fixes and the reliability program) were completed
2026-07-05/07; see the git history and [TEST-COVERAGE-MAP.md](TEST-COVERAGE-MAP.md)
for the cross-repo test picture.
