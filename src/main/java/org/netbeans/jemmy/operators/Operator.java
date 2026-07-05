
package org.netbeans.jemmy.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.*;
import org.netbeans.jemmy.callables.CallablesJ;
import org.netbeans.jemmy.functions.OperatorPredicateFunction;
import org.netbeans.jemmy.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
            FunctionRepeater.on(new OperatorPredicateFunction<>(predicate, (T) this)).runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new JemmyException("Waiting of \"" + predicate.toString() + "\" state has been interrupted!", e);
        }
    }

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
                throw(JemmyException) throwable;
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

        @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"}) Throwable t = functionRunner.getThrowable();
        if (t != null) {
            throw new JemmyException(String.format("throwable encountered during exception of function \"%s\"", function));
        }
    }

    public String getSourceToString() {
        return queueTool.invokeSmoothly(Caller.of(CallablesJ.toStringOfOperatorSource(this)));
    }

    public void setVisualizer(ComponentVisualizer visualizer) {
        this.visualizer = visualizer;
    }

    public static ComponentVisualizer setDefaultComponentVisualizer(ComponentVisualizer visualizer) {
        return (ComponentVisualizer) JemmyProperties.getInstance().put("ComponentOperator.ComponentVisualizer",
                visualizer);
    }

    public static ComponentVisualizer getDefaultComponentVisualizer() {
        return (ComponentVisualizer) JemmyProperties.getInstance().get("ComponentOperator.ComponentVisualizer");
    }

    private static PathParser setDefaultPathParser(PathParser parser) {
        return (PathParser) JemmyProperties.getInstance().put("ComponentOperator.PathParser", parser);
    }

    private static PathParser getDefaultPathParser() {
        return (PathParser) JemmyProperties.getInstance().get("ComponentOperator.PathParser");
    }

    private static boolean setDefaultVerification(boolean verification) {
        Boolean ret = (Boolean) JemmyProperties.getInstance().put("Operator.Verification", verification);
        return (ret != null) && ret;
    }

    private static boolean getDefaultVerification() {
        return (Boolean) JemmyProperties.getInstance().get("Operator.Verification");
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

    public static ComponentOperator createOperator(Component comp) {
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

    private static ComponentOperator createOperator(Component comp, Class compClass) {
        List<String> splitClassName = Arrays.asList(compClass.getName().split("\\."));
        String className = splitClassName.get(splitClassName.size() -1);
        Object[] params = { comp };
        Class[] paramClasses = { compClass };
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
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                logger.warn("", e);
            }

            if (instance != null) {
                return (ComponentOperator) instance;
            }
        }

        return null;
    }
}
