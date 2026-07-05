
package org.netbeans.jemmy.callables;

import org.netbeans.jemmy.operators.Operator;

import java.util.concurrent.Callable;

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
