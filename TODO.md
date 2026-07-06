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
| `awt-adjustable-scroll` | Scroll AWT ScrollPane/Scrollbar via Adjustable API instead of held mouse press | **Do proactively** — fixes known flakiness |
| `internal-frame-api-driver` | LAF-immune internal frame driver using JInternalFrame API | **Do proactively** if internal frames matter |
| `flaky-ui-tests` | Investigate jemmy_036 focus handoff and JPopupMenuOperatorTest timeout | Do when suite noise becomes annoying |
| `winlaf-button-test` | Run JInternalFrameOperatorTest under Windows LAF once | Cheap sanity check, ~minutes |
| `filechooser-accessible-names` | Accessible-name based file list selection + LAF/Mac handling | Only if non-Windows or non-default LAF |
| `internal-frame-popup-driver` | Title-actions-in-popup LAF support (Motif-style) | Only if such a LAF is ever used |
| `tooltip-operator` | JToolTipOperator convenience API | Only when a test wants it |
| `click-on-reference` | Click hyperlink in JEditorPane by anchor | Only when a test wants it |
| `color-chooser-accessors` | JColorChooserOperator subcomponent getters | Only when a test wants it |
| `table-change-selection` | JTableOperator.changeSelection mapping | Trivial, on demand |
| `dump-enrichment` | Richer failure dumps in ComponentStreamer | Nice-to-have, on demand |
| `window-count-wait` | Wait until N windows match a predicate | Trivial with functions/, on demand |

---

## Reliability follow-ups (not upstream ports)

### `awt-adjustable-scroll`

`AWTScrollDriver.startPushAndWait` holds a synthetic mouse press on the native
scrollbar arrow, but native AWT controls do not auto-repeat from synthetic
presses on current JDK/Windows 11. Scrolling stalls or never moves;
scroll-freeze detection (ported 2026-07-05, `AbstractScrollDriver_FreezeTimeout`)
now diagnoses this quickly instead of hanging 60 s, but the underlying
unreliability remains — observed intermittently in `jemmy_035` and `jemmy_037`.

Fix: rewrite `ScrollbarDriver` / `ScrollPaneDriver` push-and-wait (or the whole
scroll strategy) to drive `java.awt.Adjustable.setValue` directly, the way
`JScrollBarAPIDriver` does for Swing. Converts two known-flaky scenario tests
into deterministic ones.

**Recommendation: do proactively.**

### `flaky-ui-tests`

Verified failing intermittently on a clean tree (stash-baseline, 2026-07-05):

- `jemmy_036` — `TimeoutExpiredException` in `ComponentOperator.waitHasFocus`
  via `MouseFocusDriver.giveFocus`: focus never arrives after the mouse click.
- `JPopupMenuOperatorTest.testRobot56091` — `JMenuOperator_PushMenuTimeout`
  (60 s) exceeded in `JPopupMenuOperator.pushMenu`.

The AWT scroll flakiness in `jemmy_035`/`jemmy_037` is the same root cause as
`awt-adjustable-scroll`. A full `userInterfaceTest` run is not reliably green
until these are addressed.

**Recommendation: investigate when suite noise matters (CI gating, etc.).**

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

### `dump-enrichment`

Upstream enriched failure dumps over several changes: selected text for text
components (CODETOOLS-7902736), accessible name/description (7902811), last
mouse move location + `Component.hasFocus()` (2022 commits). This fork replaced
`Dumper` with `ComponentStreamer`, so these port as ideas, not code. Note if
copying upstream's `UIStatus`: it contains a leftover debug stack-trace
`System.out.println` in `mouseMoved()` — strip it.

### `window-count-wait`

Upstream `WindowWaiter.waitWindowCount(chooser, count)` / `countWindows`
(from 7902020): wait until the number of windows matching a predicate reaches
an expected count. `WindowWaiter` was deleted in this fork; recreate with the
`functions/` machinery when a test needs "wait until N dialogs exist."
