# Awaitility UI-test spike

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

Run the representative test with:

```powershell
.\gradlew.bat userInterfaceTest --tests org.netbeans.jemmy.testing.ButtonGridLookupTest checkLicenseHeaders --console=plain
```

## Assessment

Awaitility is useful for eventual assertions spanning several UI values, with
AssertJ's expected/actual reporting. It is more verbose than these existing Jemmy
waits. If adopted more broadly, extract the polling configuration into a UI-test
helper after agreeing on timeout policy. Keep tests specifically verifying Jemmy's
waiting APIs on those APIs, so they continue testing the library's own behavior.
This spike validates one representative test and its assertion-failure path;
it does not establish whole-suite compatibility or an uncaught-EDT-failure result.

References: [Awaitility usage](https://github.com/awaitility/awaitility/wiki/Usage)
and [setup](https://github.com/awaitility/awaitility/wiki/Getting_started).
