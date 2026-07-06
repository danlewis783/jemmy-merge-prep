/*
 * Copyright (c) 1997, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.netbeans.jemmy.operators;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.CharBindingMap;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.FunctionRunner;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.callables.CallablesJ;
import org.netbeans.jemmy.functions.OperatorPredicateFunction;
import org.netbeans.jemmy.util.ComponentVisualizer;
import org.netbeans.jemmy.util.DefaultPathParser;
import org.netbeans.jemmy.util.DefaultVisualizer;
import org.netbeans.jemmy.util.MouseVisualizer;
import org.netbeans.jemmy.util.PathParser;
import org.netbeans.jemmy.util.StringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Operator {
    private static final Logger logger = LoggerFactory.getLogger(Operator.class);
    private static final List<String> operatorPkgs = new ArrayList<>();

    static {
        String os = System.getProperty("os.name").toUpperCase();
        if (os.startsWith("LINUX")) {
            setDefaultComponentVisualizer(new MouseVisualizer(0.5, 10));
        } else if (os.startsWith("SUNOS")) {
            setDefaultComponentVisualizer(new MouseVisualizer(0.0, 0));
        } else {
            setDefaultComponentVisualizer(new DefaultVisualizer());
        }

        setDefaultPathParser(new DefaultPathParser("|"));
        addOperatorPackage("org.netbeans.jemmy.operators");
        setDefaultVerification(true);
    }

    private final CharBindingMap charBindingMap;
    private final PathParser pathParser;
    private boolean verification;
    private ComponentVisualizer visualizer;
    final QueueTool queueTool;

    public Operator() {
        queueTool = QueueTool.getInstance();
        this.charBindingMap = JemmyProperties.getInstance().getCharBindingMap();
        this.visualizer = getDefaultComponentVisualizer();
        this.verification = getDefaultVerification();
        this.pathParser = getDefaultPathParser();
    }

    public abstract Component getSource();

    public ComponentVisualizer getVisualizer() {
        return visualizer;
    }

    public CharBindingMap getCharBindingMap() {
        return charBindingMap;
    }

    public boolean getVerification() {
        return verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
    }

    public String[] getParentPath(String path[]) {
        if (path.length > 1) {
            String[] ppath = new String[path.length - 1];
            System.arraycopy(path, 0, ppath, 0, ppath.length);
            return ppath;
        } else {
            return new String[0];
        }
    }

    public List<Predicate<Component>> getParentPath(List<Predicate<Component>> path) {
        if (path.isEmpty()) {
            return Collections.unmodifiableList(new ArrayList<>());
        } else {
            return Collections.unmodifiableList(new ArrayList<>(path.subList(0, path.size() - 1)));
        }
    }

    public String[] parseString(String path) {
        return pathParser.parse(path);
    }

    public String[] parseString(String path, String delim) {
        return new DefaultPathParser(delim).parse(path);
    }

    public int getCharKey(char c) {
        return charBindingMap.getCharKey(c);
    }

    public int getCharModifiers(char c) {
        return charBindingMap.getCharModifiers(c);
    }

    public int[] getCharsKeys(char[] c) {
        int[] result = new int[c.length];
        for (int i = 0; i < c.length; i++) {
            result[i] = getCharKey(c[i]);
        }

        return result;
    }

    public int[] getCharsModifiers(char[] c) {
        int[] result = new int[c.length];
        for (int i = 0; i < c.length; i++) {
            result[i] = getCharModifiers(c[i]);
        }

        return result;
    }

    public int[] getCharsKeys(String s) {
        return getCharsKeys(s.toCharArray());
    }

    public int[] getCharsModifiers(String s) {
        return getCharsModifiers(s.toCharArray());
    }

    public <T extends Operator> void waitState(Predicate<T> predicate) {
        try {
            FunctionRepeater.on(new OperatorPredicateFunction<>(predicate, (T) this))
                    .runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new JemmyException("Waiting of \"" + predicate.toString() + "\" state has been interrupted!", e);
        }
    }

    /**
     * Like {@link #waitState(Predicate)}, but evaluates the predicate on the event dispatch thread, so it can safely
     * read Swing component state.
     */
    public <T extends Operator> void waitStateOnQueue(Predicate<T> predicate) {
        waitState(new OnQueuePredicate<>(predicate));
    }

    // the result's nullness follows the function's type argument, which pre-generics
    // NullAway cannot express
    @SuppressWarnings("NullAway")
    <F, T> T produceTimeRestricted(Function<F, T> function, @Nullable F f, TimeoutKey timeoutKey) {
        FunctionRunner<F, T> functionRunner = FunctionRunner.on(function);
        T ret;
        try {
            ret = functionRunner.submitAndGet(f, timeoutKey);
        } catch (InterruptedException e) {
            throw new JemmyException("interrupted waiting for function", e);
        }

        Throwable throwable = functionRunner.getThrowable();
        if (throwable != null) {
            if (throwable instanceof JemmyException) {
                throw (JemmyException) throwable;
            } else {
                throw new JemmyException("Exception during " + function.toString(), throwable);
            }
        }

        return ret;
    }

    protected <F, T> void produceNoBlocking(Function<F, T> function, @Nullable F f) {
        FunctionRunner<F, T> functionRunner = FunctionRunner.on(function);
        try {
            functionRunner.run(f);
        } catch (InterruptedException e) {
            throw new JemmyException("interrupted during execution of non-blocking function", e);
        }

        @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
        Throwable t = functionRunner.getThrowable();
        if (t != null) {
            throw new JemmyException(
                    String.format("throwable encountered during exception of function \"%s\"", function));
        }
    }

    public String getSourceToString() {
        return queueTool.invokeSmoothly(Caller.of(CallablesJ.toStringOfOperatorSource(this)));
    }

    public void setVisualizer(ComponentVisualizer visualizer) {
        this.visualizer = visualizer;
    }

    public static @Nullable ComponentVisualizer setDefaultComponentVisualizer(ComponentVisualizer visualizer) {
        return (ComponentVisualizer)
                JemmyProperties.getInstance().put("ComponentOperator.ComponentVisualizer", visualizer);
    }

    public static ComponentVisualizer getDefaultComponentVisualizer() {
        return (ComponentVisualizer) Objects.requireNonNull(
                JemmyProperties.getInstance().get("ComponentOperator.ComponentVisualizer"),
                "ComponentOperator.ComponentVisualizer property not set");
    }

    private static @Nullable PathParser setDefaultPathParser(PathParser parser) {
        return (PathParser) JemmyProperties.getInstance().put("ComponentOperator.PathParser", parser);
    }

    private static PathParser getDefaultPathParser() {
        return (PathParser) Objects.requireNonNull(
                JemmyProperties.getInstance().get("ComponentOperator.PathParser"),
                "ComponentOperator.PathParser property not set");
    }

    private static boolean setDefaultVerification(boolean verification) {
        Boolean ret = (Boolean) JemmyProperties.getInstance().put("Operator.Verification", verification);
        return (ret != null) && ret;
    }

    private static boolean getDefaultVerification() {
        return (Boolean) Objects.requireNonNull(
                JemmyProperties.getInstance().get("Operator.Verification"), "Operator.Verification property not set");
    }

    public static boolean isCaptionEqual(String caption, String match, StringComparator comparator) {
        return comparator.equals(caption, match);
    }

    public static int getDefaultMouseButton() {
        return InputEvent.BUTTON1_MASK;
    }

    public static int getPopupMouseButton() {
        return InputEvent.BUTTON3_MASK;
    }

    public static @Nullable ComponentOperator createOperator(Component comp) {
        try {
            Class clazz = Class.forName("java.awt.Component");
            Class compClass = comp.getClass();
            ComponentOperator result;
            do {
                if ((result = createOperator(comp, compClass)) != null) {
                    return result;
                }
            } while (clazz.isAssignableFrom(compClass = compClass.getSuperclass()));
        } catch (Throwable t) {
            logger.warn("unable to create operator for component {}", comp.toString(), t);
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            throw new RuntimeException("unable to create operator for component", t);
        }

        return null;
    }

    public static void addOperatorPackage(String pkgName) {
        operatorPkgs.add(pkgName);
    }

    private static @Nullable ComponentOperator createOperator(Component comp, Class compClass) {
        List<String> splitClassName = Arrays.asList(compClass.getName().split("\\."));
        String className = splitClassName.get(splitClassName.size() - 1);
        Object[] params = {comp};
        Class[] paramClasses = {compClass};
        for (String operatorPkg : operatorPkgs) {
            String fullyQualifiedClassName = operatorPkg + "." + className + "Operator";
            ClassReference classReference;
            try {
                classReference = new ClassReference(fullyQualifiedClassName);
            } catch (ClassNotFoundException e) {
                logger.warn("", e);

                continue;
            }

            Object instance = null;
            try {
                instance = classReference.newInstance(params, paramClasses);
            } catch (InvocationTargetException
                    | NoSuchMethodException
                    | IllegalAccessException
                    | InstantiationException e) {
                logger.warn("", e);
            }

            if (instance != null) {
                return (ComponentOperator) instance;
            }
        }

        return null;
    }

    private static final class OnQueuePredicate<T extends Operator> implements Predicate<T> {
        private final Predicate<T> predicate;

        OnQueuePredicate(Predicate<T> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T operator) {
            return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> predicate.test(operator)));
        }

        @Override
        public String toString() {
            return predicate.toString();
        }
    }
}
