# TODO — remaining upstream port candidates

Source: three-way analysis (2026-07-05) of this fork against the common ancestor
(Jemmy 2.2.7.5, build 2007-06-12) and [openjdk/jemmy-v2](https://github.com/openjdk/jemmy-v2).

**All items are complete as of 2026-07-07.** Resolutions, most recent first:

| Mnemonic | Resolution |
|---|---|
| `assertj-migration` | Done 2026-07-07 — every JUnit value assertion in `test` and `userInterfaceTest` converted to AssertJ fluent assertions; only `assertTimeout`/`assertTimeoutPreemptively` remain JUnit, as timing guards with no AssertJ equivalent |
| `internal-frame-popup-driver` | Ported 2026-07-07 — `InternalFramePopupMenuDriver` + `getPopupButton()`, installed when Motif is the startup LAF (CODETOOLS-7902300) |
| `filechooser-accessible-names` | Ported 2026-07-07 — accessible-name file list matching, JTable details view, LAF-aware `goHome`/`getCancelButton` (CODETOOLS-7902413, 7901960, 7902339) |
| `color-chooser-accessors` | Ported 2026-07-07 — `JColorChooserOperator` tab-conditional spinner/slider/text-field getters (CODETOOLS-7901925) |
| `tooltip-operator` | Ported 2026-07-07 — `JToolTipOperator` with the `WaitToolTipTimeout` mechanism (CODETOOLS-7902278, 7902342) |

Earlier tiers (upstream bug fixes and the reliability program) were completed
2026-07-05/07; see the git history and [TEST-COVERAGE-MAP.md](TEST-COVERAGE-MAP.md)
for the cross-repo test picture.
