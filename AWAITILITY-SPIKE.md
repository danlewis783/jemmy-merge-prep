# Awaitility spike: what it taught us about Jemmy's API

We tried [Awaitility](https://github.com/awaitility/awaitility) 4.3.0 in the UI tests to see
whether it could replace Jemmy's waits for checks on UI feedback. It could, but only with
settings at every call site that worked around Jemmy. Each of those settings pointed to a gap
in Jemmy's own API. We closed those gaps and added a Jemmy-native assertion wait,
`waitAsserted`. Awaitility was then dropped, since it no longer added anything.

## What the spike tried

`ButtonGridLookupTest.doit()` clicks 20 buttons, and each click updates a status label and a
progress bar. The test originally used three Jemmy waits per click:

```java
byTextButtonOp.push();
statusLabelOp.waitText("Button \"" + buttonText + "\" has been pushed", strict());
progressBarOp.waitValue(buttonText, strict());
progressBarOp.waitValue(buttonIndex + 1);
```

The spike replaced them with one Awaitility assertion:

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

A second test, in `JemmyFailureArtifactsTest`, checked that an uncaught exception on the Swing
event thread during an Awaitility wait still reached Jemmy's failure report.

## What was worth keeping

- **One assertion over several values.** The three values were read together on the event
  thread and checked in one statement. Three separate waits check three different moments.
- **Expected and actual values on failure.** AssertJ reports
  `expected: "Ready: Ada" but was: "Loading profile"`. Jemmy's predicate waits named what they
  were waiting for and captured the component's state, but didn't pair expected with actual.
- **Stricter checks where they fit.** `waitValue(int)` passes once the value is at least the
  target, while `containsExactly` requires it exactly. That suits this fixture, where each
  click sets a known value.

## What it cost, and what that showed about Jemmy

Most of the Awaitility snippet above is settings that keep Jemmy working, not the check itself.

1. **Diagnostics are recorded per thread.** Jemmy keeps each test's recording in a
   `ThreadLocal`, so the wait had to poll on the test thread (`pollInSameThread()`). A read
   that hangs therefore can't be cut off at Awaitility's deadline. Only `QueueTool`'s
   dispatch timeouts or JUnit's `@Timeout` stop it. *Lesson:* a wait that runs on the test
   thread gets Jemmy's diagnostics for free, as Jemmy's own waits do. The limit on hung reads
   is the same for every Jemmy wait, `waitAsserted` included.
2. **Uncaught-exception handler.** By default Awaitility installs its own default
   uncaught-exception handler during a wait. That replaces Jemmy's recorder of failures on
   the Swing event thread, so the setting `dontCatchUncaughtExceptions()` was needed. *Lesson:*
   a tool that takes over process-wide state for the length of a wait competes with Jemmy for it.
3. **A failed assertion looked like a UI fault.** `QueueTool` wraps anything thrown on the
   event thread in a `JemmyException`. Awaitility retries only on `AssertionError`, so an
   assertion inside `onQueue` would have ended the wait at the first failed poll. `QueueTool`
   also recorded every such throwable as a failure on the event thread, so each failed poll
   would have added a false failure to the report. The spike had to read values in `onQueue`
   and assert back on the test thread. *Lesson:* Jemmy had no way to say "this throwable is
   the check's answer, not a bug".
4. **The budget was outside Jemmy's timeout policy.** Nothing in `TimeoutKey` fitted:
   `Waiter_WaitingTime` is 60 s, too long for "the label should update now". The spike
   hard-coded 5 s, which `Timeouts.override` can't change. *Lesson:* assertion waits need
   their own key.
5. **The report lost the component under test.** Jemmy's waits tell the diagnostics which
   component they were waiting on. Awaitility can't. The test extension then captured a
   generic snapshot, and its component tree was cut down to the focused button. The status
   label under test was missing from the report. *Lesson:* this was the real regression. Jemmy
   offered `attachTo(failure, target, component)` for waits outside its own loop, but it
   couldn't record how long the wait lasted, and nothing called it.
6. **Verbosity.** Seven lines of settings per wait, compared with one line per Jemmy wait.

## What changed in Jemmy

| Lesson | API change |
|---|---|
| 1, 5, 6: waits should run in Jemmy's loop and diagnose their component | `Operator.waitAsserted(check)` and `AssertionRepeater`, built on `Repeater`, the loop behind every Jemmy wait |
| 3: a failed check isn't a UI fault | `QueueTool.assertOnQueue(Runnable)` rethrows an `AssertionError` unchanged and doesn't record it. Any other throwable is wrapped and recorded, as in `runOnQueue` |
| 4: assertion waits need their own budget | `TimeoutKey.Waiter_AssertionWaitingTime`, 10 s by default, adjustable with `Timeouts.override` |
| 5: outside waits need to record their timeout | `JemmyDiagnostics.attachTo(failure, target, component, waitMillis, timeoutKey)`, and a `Timeout:` line in the report's *Wait condition* section, which Jemmy's own waits show too |

Lesson 2 needed no change. Jemmy's own waits never replace the uncaught-exception handler.

`ButtonGridLookupTest` now reads:

```java
byTextButtonOp.push();
// One EDT snapshot per poll, so all three values come from the same moment.
statusLabelOp.waitAsserted(() -> assertThat(new Object[] {
            statusLabelOp.getText(), progressBarOp.getString(), progressBarOp.getValue()
        })
        .as("status text, progress text, progress value")
        .containsExactly("Button \"" + buttonText + "\" has been pushed",
                buttonText, buttonIndex + 1));
```

### How `waitAsserted` behaves

- It waits like `waitState`: it refuses to wait on the event thread, polls at once, then
  every `Waiter_TimeDelta`, until its `TimeoutKey` runs out (`Waiter_AssertionWaitingTime`
  unless another key is passed).
- Each poll runs the check once on the event thread through `assertOnQueue`. Values read
  inside it come from the same moment, and operator getters run directly without another
  hop. The check must be a pure, non-blocking read, as a `waitState` predicate must.
- An `AssertionError` means "not yet". Any other throwable ends the wait at once.
- On timeout it throws `TimeoutExpiredException`:
  - the message's wait target ends with the last assertion message, for example
    `assertions on JLabelOperator; last failure: [profile status] expected: "Ready: Ada" but was: "Loading profile"`;
  - the cause is that last `AssertionError`;
  - the usual diagnostics are attached, with the operator's component and the budget.
- For a check that isn't tied to one operator, use
  `AssertionRepeater.on(check).describedAs(...).diagnosing(component).runUntilPassed()`.

### When to use it

Use a property wait such as `waitText` or `waitValue` for one property. Use `waitAsserted`
when several values must agree, or when the expected and actual values make a failure easier
to read. Keep tests of Jemmy's own waiting APIs on those APIs, so they go on testing the
library. CONVENTIONS.md lists assertion waits as a kind of wait for tests only; library code
keeps to predicates.

## Tests

- `AssertionRepeaterTest` (unit) checks that:
  - polls run on the event thread until the check passes;
  - a timeout carries the last assertion as its cause, together with diagnostics and the key;
  - any other exception ends the wait at once;
  - the wait refuses to run on the event thread;
  - failed polls aren't recorded as failures on the event thread (reverting that change in
    `Caller` makes this test fail);
  - `Operator.waitAsserted` diagnoses its component;
  - the report shows the `Timeout:` line for a wait attached through the new `attachTo`.
- `QueueToolExceptionWrappingTest` covers `assertOnQueue` on and off the event thread.
- `JemmyFailureArtifactsTest.capturesAnUncaughtEdtFailureWhileWaitAssertedWaits` replaces
  the spike's Awaitility version. A real uncaught `NullPointerException` on the event thread
  leaves the label at `Loading profile`. The test checks that:
  - the timeout carries that exception;
  - the report names the label and its text, the 500 ms budget, and the `AssertionError`
    cause;
  - the screenshot and attachment ZIP are produced.

Run them with:

```powershell
.\gradlew.bat test --tests org.netbeans.jemmy.AssertionRepeaterTest --tests org.netbeans.jemmy.QueueToolExceptionWrappingTest userInterfaceTest --tests org.netbeans.jemmy.testing.ButtonGridLookupTest --tests org.netbeans.jemmy.testing.JemmyFailureArtifactsTest checkLicenseHeaders --console=plain
```

## Why Awaitility was dropped

Once these changes were in, an Awaitility wrapper built on them was tried for comparison. It
worked, and its reports showed the same *Wait condition* as `waitAsserted`'s. It still needed its own settings at every
call site, and it only repeated what Jemmy now does itself. It added no capability the tests
needed, so the dependency, the wrapper and its test were removed.
