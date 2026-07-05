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

package org.netbeans.jemmy.callables;

import java.util.concurrent.Callable;
import org.netbeans.jemmy.operators.Operator;

public final class CallablesJ {
    private CallablesJ() {}

    public static Callable<Void> forRunnable(Runnable runnable) {
        return new RunnableCallable(runnable);
    }

    public static Callable<String> toStringOf(Object object) {
        return new ObjectToStringCallable(object);
    }

    public static Callable<String> toStringOfOperatorSource(Operator operator) {
        return new OperatorSourceToStringCallable(operator);
    }

    private static class ObjectToStringCallable implements Callable<String> {
        private final Object object;

        public ObjectToStringCallable(Object object) {
            this.object = object;
        }

        @Override
        public String call() throws Exception {
            return object.toString();
        }
    }

    private static class OperatorSourceToStringCallable implements Callable<String> {
        private final Operator operator;

        public OperatorSourceToStringCallable(Operator operator) {
            this.operator = operator;
        }

        @Override
        public String call() throws Exception {
            return operator.getSource().toString();
        }
    }

    private static class RunnableCallable implements Callable<Void> {
        private final Runnable runnable;

        public RunnableCallable(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public Void call() throws Exception {
            runnable.run();
            return null;
        }
    }
}
