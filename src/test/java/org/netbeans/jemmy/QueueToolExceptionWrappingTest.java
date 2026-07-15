/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.netbeans.jemmy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.Test;

/**
 * Exceptions from dispatched work reach the caller with exactly one {@link JemmyException}
 * wrapper: a checked or runtime exception is wrapped once, and a JemmyException thrown by the
 * work (operators pre-wrap checked exceptions inside their lambdas) propagates as-is, so
 * {@code getCause()} is always the original exception.
 */
class QueueToolExceptionWrappingTest {

    private final QueueTool queueTool = QueueTool.getInstance();

    @Test
    void callOnQueueWrapsCheckedExceptionOnce() {
        ParseException original = new ParseException("unparseable", 0);

        assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(() -> queueTool.callOnQueue((Callable<Object>) () -> {
                    throw original;
                }))
                .withMessage("Throwable captured by caller")
                .satisfies(e -> assertThat(e.getCause()).isSameAs(original));
    }

    @Test
    void runOnQueueWrapsRuntimeExceptionOnce() {
        IllegalStateException original = new IllegalStateException("action failed");

        assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(() -> queueTool.runOnQueue(() -> {
                    throw original;
                }))
                .withMessage("Throwable captured by caller")
                .satisfies(e -> assertThat(e.getCause()).isSameAs(original));
    }

    /**
     * Errors must be captured by the caller too: JMenu.setAccelerator and friends throw Error on
     * the dispatch thread, and an Error that escaped to the InvocationEvent is recorded only
     * after the end gate has released the waiting thread, which raced and sometimes missed it.
     */
    @Test
    void runOnQueueWrapsErrorOnce() {
        Error original = new Error("not defined for JMenu");

        assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(() -> queueTool.runOnQueue(() -> {
                    throw original;
                }))
                .withMessage("Throwable captured by caller")
                .satisfies(e -> assertThat(e.getCause()).isSameAs(original));
    }

    @Test
    void callOnQueueWrapsErrorOnce() {
        Error original = new Error("not defined for JMenu");

        assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(() -> queueTool.callOnQueue((BooleanSupplier) () -> {
                    throw original;
                }))
                .withMessage("Throwable captured by caller")
                .satisfies(e -> assertThat(e.getCause()).isSameAs(original));
    }

    @Test
    void callOnQueuePropagatesJemmyExceptionUnwrapped() {
        JemmyException preWrapped = new JemmyException("could not commit edit", new ParseException("unparseable", 0));

        assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(() -> queueTool.callOnQueue((BooleanSupplier) () -> {
                    throw preWrapped;
                }))
                .isSameAs(preWrapped);
    }

    @Test
    void runOnQueuePropagatesJemmyExceptionUnwrapped() {
        JemmyException preWrapped = new JemmyException("could not commit edit", new ParseException("unparseable", 0));

        assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(() -> queueTool.runOnQueue(() -> {
                    throw preWrapped;
                }))
                .isSameAs(preWrapped);
    }

    @Test
    void callOnQueueWrapsCheckedExceptionOnceOnDispatchThread() throws InterruptedException, InvocationTargetException {
        ParseException original = new ParseException("unparseable", 0);

        EventQueue.invokeAndWait(() -> assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(() -> queueTool.callOnQueue((Callable<Object>) () -> {
                    throw original;
                }))
                .withMessage("Exception when calling")
                .satisfies(e -> assertThat(e.getCause()).isSameAs(original)));
    }

    @Test
    void callOnQueuePropagatesJemmyExceptionUnwrappedOnDispatchThread()
            throws InterruptedException, InvocationTargetException {
        JemmyException preWrapped = new JemmyException("could not commit edit", new ParseException("unparseable", 0));

        EventQueue.invokeAndWait(() -> assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(() -> queueTool.callOnQueue((BooleanSupplier) () -> {
                    throw preWrapped;
                }))
                .isSameAs(preWrapped));
    }

    @Test
    void runOnQueuePropagatesJemmyExceptionUnwrappedOnDispatchThread()
            throws InterruptedException, InvocationTargetException {
        JemmyException preWrapped = new JemmyException("could not commit edit", new ParseException("unparseable", 0));

        EventQueue.invokeAndWait(() -> assertThatExceptionOfType(JemmyException.class)
                .isThrownBy(() -> queueTool.runOnQueue(() -> {
                    throw preWrapped;
                }))
                .isSameAs(preWrapped));
    }
}
