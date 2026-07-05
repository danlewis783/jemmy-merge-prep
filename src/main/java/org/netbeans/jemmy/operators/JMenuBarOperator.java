package org.netbeans.jemmy.operators;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.*;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.predicates.JMenuItemByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import javax.swing.plaf.MenuBarUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.Callable;


public class JMenuBarOperator extends JComponentOperator {
    public static final String SUBMENU_PREFIX_DPROP = "Submenu";
    private final MenuDriver driver;

    public JMenuBarOperator(ContainerOperator cont) {
        this((JMenuBar) waitComponent(cont, PredicatesJ.of(JMenuBar.class), 0));
    }

    public JMenuBarOperator(JMenuBar b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getMenuDriver(getClass());
    }

    public JMenuBarOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JMenuBarOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JMenuBar) cont.waitSubComponent(PredicatesJ.of(JMenuBar.class, chooser), index));
    }

    public JMenuItem pushMenu(List<Predicate<Component>> predicates) {
        makeComponentVisible();

        return produceTimeRestricted((Function<Void, JMenuItem>) v -> (JMenuItem) driver.pushMenu(JMenuBarOperator.this, predicates), null, TimeoutKey.JMenuOperator_PushMenuTimeout);
    }

    public void pushMenuNoBlock(List<Predicate<Component>> predicates) {
        makeComponentVisible();
        produceNoBlocking((Function<Void, MenuElement>) v -> driver.pushMenu(JMenuBarOperator.this, predicates), null);
    }

    public JMenuItem pushMenu(String[] names, StringComparator comparator) {
        return pushMenu(JMenuItemOperator.createPredicates(names, comparator));
    }

    public void pushMenuNoBlock(String[] names, StringComparator comparator) {
        pushMenuNoBlock(JMenuItemOperator.createPredicates(names, comparator));
    }

    public JMenuItem pushMenu(String path, String delim, StringComparator comparator) {
        return pushMenu(parseString(path, delim), comparator);
    }

    public JMenuItem pushMenu(String path, StringComparator comparator) {
        return pushMenu(parseString(path), comparator);
    }

    public void pushMenuNoBlock(String path, String delim, StringComparator comparator) {
        pushMenuNoBlock(parseString(path, delim), comparator);
    }

    public void pushMenuNoBlock(String path, StringComparator comparator) {
        pushMenuNoBlock(parseString(path), comparator);
    }

    public JMenuItemOperator[] showMenuItems(List<Predicate<Component>> predicate) {
        if ((predicate == null) || (predicate.isEmpty())) {
            return JMenuItemOperator.getMenuItems((MenuElement) getSource());
        } else {
            return JMenuItemOperator.getMenuItems((JMenu) pushMenu(predicate));
        }
    }

    public JMenuItemOperator[] showMenuItems(String[] path, StringComparator comparator) {
        if ((path == null) || (path.length == 0)) {
            return JMenuItemOperator.getMenuItems((MenuElement) getSource());
        } else {
            return JMenuItemOperator.getMenuItems((JMenu) pushMenu(path, comparator));
        }
    }

    public JMenuItemOperator[] showMenuItems(String path, String delim, StringComparator comparator) {
        return showMenuItems(parseString(path, delim), comparator);
    }

    public JMenuItemOperator[] showMenuItems(String path, StringComparator comparator) {
        return showMenuItems(parseString(path), comparator);
    }

    public JMenuItemOperator showMenuItem(List<Predicate<Component>> predicates) {
        List<Predicate<Component>> parentPath = getParentPath(predicates);
        JMenu menu;
        ContainerOperator menuCont;
        if (parentPath.isEmpty()) {
            menuCont = this;
        } else {
            menu = (JMenu) pushMenu(getParentPath(predicates));
            menuCont = new ContainerOperator(menu.getPopupMenu());
        }

        return new JMenuItemOperator(menuCont, predicates.get(predicates.size() - 1));
    }

    public JMenuItemOperator showMenuItem(String[] path, StringComparator comparator) {
        String[] parentPath = getParentPath(path);
        JMenu menu;
        ContainerOperator menuCont;
        if (parentPath.length > 0) {
            menu = (JMenu) pushMenu(getParentPath(path), comparator);
            menuCont = new ContainerOperator(menu.getPopupMenu());
        } else {
            menuCont = this;
        }

        JMenuItemOperator result;
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            ComponentSearcher searcher = new ComponentSearcher((Container) menuCont.getSource());
            Component c = searcher.findComponent(new JMenuItemByTextPredicate(path[path.length - 1],
                              comparator));
            result = new JMenuItemOperator((JMenuItem) c);
        } else {
            result = new JMenuItemOperator(menuCont, path[path.length - 1], comparator);
        }

        return result;
    }

    public JMenuItemOperator showMenuItem(String path, String delim, StringComparator comparator) {
        return showMenuItem(parseString(path, delim), comparator);
    }

    public JMenuItemOperator showMenuItem(String path, StringComparator comparator) {
        return showMenuItem(parseString(path), comparator);
    }

    public void closeSubmenus() {
        JMenu menu = (JMenu) findSubComponent(new IsJMenuAndPopupIsVisiblePredicate());
        if (menu != null) {
            JMenuOperator oper = new JMenuOperator(menu);
            oper.push();
        }
    }

    public JMenu add(JMenu jMenu) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).add(jMenu)));
    }

    public int getComponentIndex(Component component) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getComponentIndex(component)));
    }

    public JMenu getHelpMenu() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getHelpMenu()));
    }

    public Insets getMargin() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getMargin()));
    }

    public JMenu getMenu(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getMenu(i)));
    }

    public int getMenuCount() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getMenuCount()));
    }

    public SingleSelectionModel getSelectionModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getSelectionModel()));
    }

    public MenuElement[] getSubElements() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getSubElements()));
    }

    public MenuBarUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).getUI()));
    }

    public boolean isBorderPainted() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).isBorderPainted()));
    }

    public boolean isSelected() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JMenuBar) getSource()).isSelected()));
    }

    public void menuSelectionChanged(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).menuSelectionChanged(b);

            return null;
        }));
    }

    public void processKeyEvent(KeyEvent keyEvent, MenuElement[] menuElement,
                                MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).processKeyEvent(keyEvent, menuElement, menuSelectionManager);

            return null;
        }));
    }

    public void processMouseEvent(MouseEvent mouseEvent, MenuElement[] menuElement,
                                  MenuSelectionManager menuSelectionManager) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).processMouseEvent(mouseEvent, menuElement, menuSelectionManager);

            return null;
        }));
    }

    public void setBorderPainted(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setBorderPainted(b);

            return null;
        }));
    }

    public void setHelpMenu(JMenu jMenu) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setHelpMenu(jMenu);

            return null;
        }));
    }

    public void setMargin(Insets insets) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setMargin(insets);

            return null;
        }));
    }

    public void setSelected(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setSelected(component);

            return null;
        }));
    }

    public void setSelectionModel(SingleSelectionModel singleSelectionModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setSelectionModel(singleSelectionModel);

            return null;
        }));
    }

    public void setUI(MenuBarUI menuBarUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JMenuBar) getSource()).setUI(menuBarUI);

            return null;
        }));
    }

    public static JMenuBar findJMenuBar(JFrame frame) {
        return findJMenuBar((Container) frame);
    }

    public static JMenuBar findJMenuBar(JDialog dialog) {
        return findJMenuBar((Container) dialog);
    }

    public static JMenuBar waitJMenuBar(Container cont) {
        return (JMenuBar) waitComponent(cont, PredicatesJ.of(JMenuBar.class));
    }

    public static JMenuBar waitJMenuBar(JFrame frame) {
        return waitJMenuBar((Container) frame);
    }

    public static JMenuBar waitJMenuBar(JDialog dialog) {
        return waitJMenuBar((Container) dialog);
    }

    public static JMenuBar findJMenuBar(Container cont) {
        return (JMenuBar) findComponent(cont, PredicatesJ.of(JMenuBar.class));
    }

    private static class IsJMenuAndPopupIsVisiblePredicate implements Predicate<Component> {
        @Override
        public boolean test(Component comp) {
            return (comp instanceof JMenu) && ((JMenu) comp).isPopupMenuVisible();
        }
    }
}
