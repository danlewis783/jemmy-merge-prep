# Awaitility UI-test spike

> **Follow-up:** the [Jemmy-native `waitAsserted` prototype](#follow-up-jemmy-native-waitasserted-prototype)
> at the end of this file changes Jemmy to remove the workarounds described below.
> `ButtonGridLookupTest` now uses `waitAsserted`, and the Awaitility fixture uses the
> `JemmyAwait` helper. The sections before it record the spike as first run.

`ButtonGridLookupTest.doit()` clicks all 20 buttons and checks the resulting status
label and progress bar. Awaitility 4.3.0 is added only to `userInterfaceTest` through
the version catalog. Production code and shared test fixtures gain no dependency.
4.3.1 was listed in the upstream documentation but could not be resolved from Maven
Central in this environment; 4.3.0 resolved and ran with the project's Java 8 toolchain.

## Before

```java
byTextButtonOp.push();
statusLabelOp.waitText("Button \"" + buttonText + "\" has been pushed", strict());
progressBarOp.waitValue(buttonText, strict());
progressBarOp.waitValue(buttonIndex + 1);
```

## After

```java
byTextButtonOp.push();
await("feedback after clicking " + buttonText)
        .pollInSameThread()
        .dontCatchUncaughtExceptions()
        .pollDelay(Duration.ZERO)
        .pollInterval(Duration.ofMillis(50))
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(() -> {
            Object[] feedback = onQueue(() -> new Object[] {
                    statusLabelOp.getText(), progressBarOp.getString(), progressBarOp.getValue()
            });
            assertThat(feedback).as("status text, progress text, progress value")
                    .containsExactly("Button \"" + buttonText + "\" has been pushed",
                            buttonText, buttonIndex + 1);
        });
```

Imports added: `java.time.Duration`, static `org.awaitility.Awaitility.await`, and
static `org.netbeans.jemmy.testing.OnQueue.onQueue`.

## Why this shape

- Click once, outside the retried assertion. Component discovery and input continue
  to use Jemmy. Do not nest Jemmy's blocking waits inside Awaitility predicates.
- Poll on the test thread to retain Jemmy's thread-local diagnostic recording.
  `dontCatchUncaughtExceptions()` leaves the existing default uncaught-exception
  handler in place for Jemmy's EDT failure recorder; it does not ignore exceptions
  thrown by the polled condition.
- Read all three values in one EDT hop. The operator getters execute inline when
  already on the EDT. Assert after returning to the test thread: `QueueTool` wraps
  throwables from EDT callbacks in `JemmyException`, which would prevent Awaitility
  from treating an assertion failure as a reason to retry.
- Poll immediately, then every 50 ms. One five-second budget covers the entire
  feedback assertion. The old waits each use `Waiter_WaitingTime` (60 seconds by
  default). This local budget deliberately does not follow `Timeouts.override`.
- `containsExactly` checks the numeric progress value for equality. The original
  `waitValue(int)` accepts values greater than or equal to its argument. Exact
  equality is appropriate for this fixture because each button sets a known value,
  but this is a deliberate strengthening, not a mechanically equivalent migration.
- Same-thread polling cannot interrupt a blocked condition at the Awaitility
  deadline. Retain the existing JUnit `@Timeout` as the hang guard; reads still
  depend on Jemmy's EDT dispatch completing.

## Validation

The original test and converted test passed. The license-header check passed.
A temporary impossible expected label exercised retries and failure reporting:
the test failed after roughly five seconds with the actual and expected values.
Jemmy generated a screenshot, diagnostic Markdown report, and attachment ZIP.
The diagnostic report included the Awaitility condition alias and five-second
timeout. Gradle's XML presented the underlying AssertJ assertion failure.
The deliberately incorrect expectation was then restored.

`JemmyFailureArtifactsTest.capturesAnUncaughtEdtFailureWhileAwaitilityWaits` now
also covers a real uncaught EDT exception. A nested JUnit fixture starts profile
loading once, after Awaitility's first condition evaluation. The posted Swing
callback dereferences an unset profile name and throws `NullPointerException`,
leaving the label at `Loading profile` instead of `Ready: Ada`.

The outer test verifies that:

- Awaitility times out with both label values and the named condition.
- Jemmy's default uncaught-exception handler stays installed during polling and
  the previous handler is restored after the fixture completes.
- Exactly one EDT exception is attached to the timeout, with the original NPE
  stack, EDT thread identity, and uncaught-handler capture mechanism.
- The report retains the NPE after window teardown, the screenshot is readable,
  and the attachment ZIP contains byte-identical copies of the report and screenshot.

The nested fixture deliberately fails; the outer regression test passes only if
the failure and artifacts match these expectations. It publishes those artifacts
outside its temporary directory for inspection after the run.

One diagnostic difference surfaced: Awaitility does not provide Jemmy's component
wait target. In this fixture the hierarchy is pruned to the focused button's path,
omitting the status label; the assertion message still contains its actual and
expected text. No production diagnostic behavior was changed for this spike.

Run the representative test and failure-artifact tests with:

```powershell
.\gradlew.bat userInterfaceTest --tests org.netbeans.jemmy.testing.ButtonGridLookupTest --tests org.netbeans.jemmy.testing.JemmyFailureArtifactsTest checkLicenseHeaders --console=plain
```

## Assessment

Awaitility is useful for eventual assertions spanning several UI values, with
AssertJ's expected/actual reporting. It is more verbose than these existing Jemmy
waits. If adopted more broadly, extract the polling configuration into a UI-test
helper after agreeing on timeout policy. Keep tests specifically verifying Jemmy's
waiting APIs on those APIs, so they continue testing the library's own behavior.
This spike validates one representative test, its assertion-failure path, and
capture of an uncaught EDT exception during an Awaitility wait. It does not
establish whole-suite compatibility.

References: [Awaitility usage](https://github.com/awaitility/awaitility/wiki/Usage)
and [setup](https://github.com/awaitility/awaitility/wiki/Getting_started).

## Follow-up: Jemmy-native `waitAsserted` prototype

The spike found four points of friction. Each is now addressed in Jemmy, and Jemmy
also gained its own assertion wait, so a test doesn't need Awaitility for this job.

| Friction in the spike | Change |
|---|---|
| Assertions inside `onQueue` get wrapped in `JemmyException`, so Awaitility stops retrying, and each failed poll is recorded as an EDT failure | `QueueTool.assertOnQueue(Runnable)` rethrows an `AssertionError` unchanged and doesn't record it. Any other throwable is wrapped and recorded, as `runOnQueue` does |
| Hard-coded 5 s budget that ignores `Timeouts.override` | New `TimeoutKey.Waiter_AssertionWaitingTime` (10 s default). The poll interval comes from `Waiter_TimeDelta` |
| The report has no wait target and cuts the component tree down to the focused component, so the label under test is missing | New `JemmyDiagnostics.attachTo(failure, target, component, waitMillis, timeoutKey)` overload. The report's *Wait condition* section now shows a `Timeout:` line |
| Seven lines of Awaitility settings at each call site | `Operator.waitAsserted(check)` and `AssertionRepeater`, or the `JemmyAwait` test helper for Awaitility |

### `waitAsserted`

```java
byTextButtonOp.push();
statusLabelOp.waitAsserted(() -> assertThat(new Object[] {
            statusLabelOp.getText(), progressBarOp.getString(), progressBarOp.getValue()
        })
        .as("status text, progress text, progress value")
        .containsExactly("Button \"" + buttonText + "\" has been pushed",
                buttonText, buttonIndex + 1));
```

`AssertionRepeater` is built on `Repeater`, the loop behind every Jemmy wait, so it
behaves like `waitState`:

- It refuses to wait on the EDT.
- It polls at once, then every `Waiter_TimeDelta`, until its `TimeoutKey` runs out.
- Each poll runs the check once on the EDT through `assertOnQueue`, so the values it
  reads come from the same moment. The check must be a pure, non-blocking read, as a
  `waitState` predicate must.
- An `AssertionError` means "not yet". Any other throwable ends the wait at once.
- On timeout it throws `TimeoutExpiredException`. The exception's wait target ends with
  the last assertion message, e.g. `assertions on JLabelOperator; last failure:
  [profile status] expected: "Ready: Ada" but was: "Loading profile"`. Its cause is
  that last `AssertionError`, and the usual Jemmy diagnostics are attached, including
  the operator's component.

`AssertionRepeater.on(check).describedAs(...).diagnosing(component).runUntilPassed()`
covers checks that aren't tied to one operator.

### `JemmyAwait` (Awaitility with items 1-3)

`src/userInterfaceTest/.../JemmyAwait.java` bundles the spike's settings: it polls on
the test thread and leaves the uncaught-exception handler alone. It adds three things:

- the budget and poll interval come from `TimeoutKey`s;
- the check runs through `assertOnQueue`;
- on `ConditionTimeoutException`, it calls the new `attachTo` overload before teardown.

```java
JemmyAwait.await("profile ready after loading")
        .diagnosing(statusOp.getSource())
        .untilAsserted(() -> assertThat(statusOp.getText()).isEqualTo("Ready: Ada"));
```

The Awaitility failure-artifact test now finds the label under test in the report,
together with its `Loading profile` text and the budget
(`Timeout: 2 s (Waiter_AssertionWaitingTime)`). The spike's version could only check
for `BrokenProfilePanel`.

### Tests

- `AssertionRepeaterTest` (unit) covers:
  - polling on the EDT until the check passes;
  - a timeout carrying the last assertion as its cause, with diagnostics and the key;
  - fast failure on any other exception;
  - refusal to wait on the EDT;
  - failed polls not being recorded as EDT failures (the test fails if that
    `Caller` change is reverted);
  - `Operator.waitAsserted`;
  - the report's `Timeout:` line for the new `attachTo`.
- `QueueToolExceptionWrappingTest` covers `assertOnQueue` on and off the EDT.
- `JemmyFailureArtifactsTest.capturesAnUncaughtEdtFailureWhileWaitAssertedWaits`
  mirrors the Awaitility artifact test for `waitAsserted`.

### Assessment

`waitAsserted` keeps Jemmy's timeout policy, thread rules and diagnostics, with no new
dependency. For assertion-style waits it replaces what Awaitility offered in the
spike. Awaitility remains an option through `JemmyAwait`, but it adds nothing Jemmy
now lacks, apart from its own settings (for example condition evaluation listeners).
If `waitAsserted` is adopted, the Awaitility dependency, `JemmyAwait` and the Awaitility
artifact test can be dropped.
