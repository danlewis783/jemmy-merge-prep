package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.*;
import org.netbeans.jemmy.functions.ComponentSearcherFunction;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ContainerListener;
import java.util.concurrent.Callable;


public class ContainerOperator extends ComponentOperator {
    private static final Logger logger = LoggerFactory.getLogger(ContainerOperator.class);
    private final ComponentSearcher searcher;

    public ContainerOperator(Container b) {
        super(b);
        searcher = new ComponentSearcher(b);
    }

    public ContainerOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public ContainerOperator(ContainerOperator cont, int index) {
        this((Container) waitComponent(cont, PredicatesJ.of(Container.class), index));
    }

    public ContainerOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public ContainerOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((Container) cont.waitSubComponent(PredicatesJ.of(Container.class, chooser), index));
    }

    public Component findSubComponent(Predicate<Component> chooser, int index) {
        return searcher.findComponent(chooser, index);
    }

    public Component findSubComponent(Predicate<Component> chooser) {
        return findSubComponent(chooser, 0);
    }

    public Component waitSubComponent(Predicate<Component> chooser, int index) {
        return waitSubComponent(chooser, index, TimeoutKey.Waiter_WaitingTime);
    }

    public Component waitSubComponent(Predicate<Component> chooser) {
        return waitSubComponent(chooser, 0);
    }

    public Component waitSubComponent(Predicate<Component> chooser, TimeoutKey timeoutKey) {
        return waitSubComponent(chooser, 0, timeoutKey);
    }

    public Component waitSubComponent(Predicate<Component> chooser, int index, TimeoutKey timeoutKey) {
        ComponentSearcher searcher = new ComponentSearcher((Container) getSource());
        try {
            return FunctionRepeater.on(new ComponentSearcherFunction(searcher, chooser, index), timeoutKey).runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new JemmyException("Waiting for \"" + chooser.toString() + "\" component has been interrupted", e);
        }
    }
    
    public ComponentOperator createSubOperator(Predicate<Component> chooser, int index) {
        return createOperator(waitSubComponent(chooser, index));
    }

    public ComponentOperator createSubOperator(Predicate<Component> chooser) {
        return createSubOperator(chooser, 0);
    }

    public Component add(Component component) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Container) getSource()).add(component)));
    }

    public Component add(Component component, int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Container) getSource()).add(component, i)));
    }

    public void add(Component component, Object object) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Container) getSource()).add(component, object);

            return null;
        }));
    }

    public void add(Component component, Object object, int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Container) getSource()).add(component, object, i);

            return null;
        }));
    }

    public Component add(String string, Component component) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Container) getSource()).add(string, component)));
    }

    public void addContainerListener(ContainerListener containerListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Container) getSource()).addContainerListener(containerListener);

            return null;
        }));
    }

    public Component findComponentAt(int i, int i1) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Container) getSource()).findComponentAt(i, i1)));
    }

    public Component findComponentAt(Point point) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Container) getSource()).findComponentAt(point)));
    }

    public Component getComponent(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Container) getSource()).getComponent(i)));
    }

    public int getComponentCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Container) getSource()).getComponentCount()));
    }

    public Component[] getComponents() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Container) getSource()).getComponents()));
    }

    public Insets getInsets() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Container) getSource()).getInsets()));
    }

    public LayoutManager getLayout() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Container) getSource()).getLayout()));
    }

    public boolean isAncestorOf(Component component) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Container) getSource()).isAncestorOf(component)));
    }

    public void paintComponents(Graphics graphics) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Container) getSource()).paintComponents(graphics);

            return null;
        }));
    }

    public void printComponents(Graphics graphics) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Container) getSource()).printComponents(graphics);

            return null;
        }));
    }

    public void remove(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Container) getSource()).remove(i);

            return null;
        }));
    }

    public void remove(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Container) getSource()).remove(component);

            return null;
        }));
    }

    public void removeAll() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Container) getSource()).removeAll();

            return null;
        }));
    }

    public void removeContainerListener(ContainerListener containerListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Container) getSource()).removeContainerListener(containerListener);

            return null;
        }));
    }

    public void setLayout(LayoutManager layoutManager) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Container) getSource()).setLayout(layoutManager);

            return null;
        }));
    }

    public static Container findContainer(Container cont, Predicate<Component> chooser, int index) {
        return (Container) findComponent(cont, PredicatesJ.of(Container.class, chooser), index);
    }

    public static Container findContainer(Container cont, Predicate<Component> chooser) {
        return findContainer(cont, chooser, 0);
    }

    public static Container findContainer(Container cont, int index) {
        return findContainer(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static Container findContainer(Container cont) {
        return findContainer(cont, 0);
    }

    public static Container findContainerUnder(Component comp, Predicate<Component> chooser) {
        return new ComponentOperator(comp).getContainer(PredicatesJ.of(Container.class, chooser));
    }

    public static Container findContainerUnder(Component comp) {
        return findContainerUnder(comp, PredicatesJ.alwaysTrue());
    }

    public static Container waitContainer(Container cont, Predicate<Component> chooser, int index) {
        return (Container) waitComponent(cont, PredicatesJ.of(Container.class, chooser), index);
    }

    public static Container waitContainer(Container cont, Predicate<Component> chooser) {
        return waitContainer(cont, chooser, 0);
    }

    public static Container waitContainer(Container cont, int index) {
        return waitContainer(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static Container waitContainer(Container cont) {
        return waitContainer(cont, 0);
    }
}
