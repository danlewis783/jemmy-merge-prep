# Conventions

Naming and structural conventions for this fork. These are house rules, not upstream
Jemmy's — where they differ from upstream, this file wins.

## Operators vs. components

The single most important distinction in this codebase is **operator or raw component?**
An operator dispatches its own UI access to the event dispatch thread; a raw
`Component`/`Container` does not, and touching one off the EDT is a bug. Names must answer
that question without the reader chasing the declaration.

**The bare noun is the component. The `Op` suffix is the operator.**

| Kind | Name | Example |
|---|---|---|
| Raw Swing/AWT component | plain lowercase noun | `table`, `list`, `frame`, `menu` |
| Operator wrapping it | same noun + `Op` | `tableOp`, `listOp`, `frameOp`, `menuOp` |
| Multi-word, only when needed | camelCase | `fileChooserOp`, `popupMenuOp`, `containerOp` |

Prefer a short all-lowercase name; reach for camelCase only when one word won't do.

### Reserved receiver names

Two names are reserved for the conventional parameters that nearly every operator and
driver method takes:

- **`op`** — the operator this method acts on. `void selectItem(ComponentOperator op, int index)`.
- **`rootOp`** — the `ContainerOperator` to search *within*, in the `waitFor`/`findX`
  family. `waitFor(ContainerOperator rootOp, Predicate<Component> chooser, int index)`.
  It is the search root, not the subject; naming it for that role is the point.

Never use `op` or `rootOp` for a raw component. Conversely, the abbreviations `comp` and
`cont` now mean *only* raw `Component`/`Container` — they never denote an operator.

Historical note: these were `oper` and `cont` (upstream Jemmy's convention), renamed so
the bare form matches the `Op` suffix instead of abbreviating the same word two ways.

### Anti-patterns

- **Type echo** — `jTreeOperator`, `componentOperator`. The declaration already states the
  type; the variable slot should say the role. Use `treeOp`, or `op` if it is the receiver.
- **Positional names** — `operator1`, `operator2`, `operator3`. Say what it holds:
  `frameOp`, `buttonOp`. Still common in `userInterfaceTest`; fix opportunistically when
  touching a test rather than in one sweep.
- **Single letters** — `of(JButton b)`. Spell the noun: `of(JButton button)`.

## EDT discipline

All main-source Swing/AWT state access runs on the event dispatch thread. See the
`ComponentSearcher`, `Operator.waitState`, and `WindowFunction.getWindow` funnels — fix
threading at a chokepoint rather than at call sites.

### Pure reads vs. waiting reads

A predicate evaluated by `waitState` runs **on the EDT**, where waiting is forbidden and
fails fast. So a read used by a wait predicate must not itself wait. Where both flavors are
needed, the naming rule is:

- **`somethingNow()`** — private, pure, non-blocking read. Safe inside an EDT predicate.
- **`getSomething()` / `findSomething()`** — the public waiting variant, test thread only.

`JFileChooserOperator` is the reference example: `fileCountNow()`, `filesNow()`,
`fileIndexNow()` alongside `getFileCount()`, `getFiles()`, `findFileIndex()`.

### One snapshot per decision

Values that must agree with each other come from **one** `callOnQueue`, returning a plain
value or a small holder. Two hops mean two moments, and the UI can change in between —
that is how you get a click at coordinates that no longer exist.

Prefer the existing atomic accessors over hand-rolling a hop:
`ComponentOperator.getCenter()`, `getClickCenter()`, `getSize()`.

Robot input, `Timeouts.sleep`, and waits **never** run inside a hop. The shape is: hop to
read, return a value, then act outside the hop.

```java
Point p = op.getClickCenter();            // one EDT snapshot
mouseDriver.clickMouse(op, p.x, p.y, ...); // input on the test thread
```

## Tests

- AssertJ with dedicated assertions — `containsExactly`, `isSameAs`, `isEmpty`,
  `assertThatThrownBy`. Not `assertThat(someBoolean).isTrue()` wrapping an expression.
- Construct and mutate Swing components on the EDT, via
  `QueueTool.getInstance().callOnQueue(...)` or the `onQueue` test-fixture helper.
- Unit tests live in `src/test` (headless, no windows); tests that show real windows and
  drive input live in `src/userInterfaceTest`.

### `@Timeout` is a hang guard, not a performance assertion

A JUnit `@Timeout` exists to kill a genuinely stuck test so the suite finishes and the
failure is attributable. It is **not** a statement about how fast the test should run —
performance expectations do not belong in the kill switch. A budget tuned close to the
fast development machine's nominal time converts every slower machine into a wall of
timeout failures that carry no diagnostic value (the interrupt lands mid-action, before
Jemmy's own enriched wait timeouts can fire).

Policy: set `@Timeout` to **at least 5× the fast-machine nominal duration**, with a floor
of 5 seconds for UI tests. A guard at 5s instead of 1s costs nothing when tests pass — it
only delays the report of a test that was already dead. Waits inside the test are governed
by `Timeouts`/`TimeoutKey` and fail with self-describing messages long before a
well-sized `@Timeout` fires; if the `@Timeout` is what triggers, that itself is the
signal that something hung outside a Jemmy wait.

(History: budgets of 1s tuned on a fast desktop caused seven scroll-heavy tests to fail
on a laptop running 1.5–3.5× slower — each failing at exactly its cap, none of them
actually broken. This is also distinct from the removed `jemmy.timeouts.scale`
multiplier: that silently stretched *waits* and distorted behavior; sizing the hang
guard changes nothing about what the test does.)
