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
| `assertj-migration` | Convert remaining JUnit assertions to AssertJ | Ongoing convention; convert classes when touched |
| `filechooser-accessible-names` | Accessible-name based file list selection + LAF/Mac handling | Only if non-Windows or non-default LAF |
| `internal-frame-popup-driver` | Title-actions-in-popup LAF support (Motif-style) | Only if such a LAF is ever used |
| `color-chooser-accessors` | JColorChooserOperator subcomponent getters | Only when a test wants it |

---

## Reliability follow-ups (not upstream ports)

### `assertj-migration`

Convert all test assertions to AssertJ fluent assertions (`assertj-core`
3.27.7, already in `gradle/libs.versions.toml`). Current state (2026-07-06):
86 of 93 `userInterfaceTest` files and 3 of 8 `test` files still use
`org.junit.jupiter.api.Assertions`.

Priority order:

1. **try/fail/catch idioms — DONE 2026-07-06** (all nine converted to
   `assertThatExceptionOfType`, boolean-fail pairs to `assertThat(...).isTrue()`,
   overrides collapsed to try-with-resources where structure allowed). Was:
   `JFrameOperatorTest`,
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
   other test edits rather than as one big
   diff.

Notes:

- Converges test style with the C:\dev\jemmy fork, which already had its
  AssertJ refactor ("Refactor tests to AssertJ 3.27.7 fluent assertions") —
  one less textual difference for the eventual merge.

**Recommendation: incremental; exception-shaped tests first, the rest
class-by-class when touched.**

### `filechooser-accessible-names`

`JFileChooserOperator.selectFile` selects by raw list index. Upstream filters
the file list by accessible name (CODETOOLS-7902413), fixes a macOS NPE in
`selectFile` (7901960), and handles LAF differences (7902339). Latent here
because the suite runs Windows + Metal; becomes real on macOS or other LAFs.

**Recommendation: port the series together if the platform matrix ever grows.**

### `internal-frame-popup-driver`

Upstream `InternalFramePopupMenuDriver` (7902300) supports LAFs where title
actions live in a popup menu (Motif-style) rather than title buttons.

**Recommendation: skip unless such a LAF is ever in play.**

---

## Feature ports (adopt on demand, each is self-contained)

### `color-chooser-accessors`

`JColorChooserOperator` subcomponent accessor methods (7901925, ~335 lines of
getters for the chooser panels/fields). Port only if tests interact with color
choosers beyond the existing `enterColor` helpers.

