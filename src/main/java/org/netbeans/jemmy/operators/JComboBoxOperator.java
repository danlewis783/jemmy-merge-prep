/*
 * Copyright (c) 1997, 2016, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.plaf.ComboBoxUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ListDriver;
import org.netbeans.jemmy.predicates.JComboBoxByItemPredicate;
import org.netbeans.jemmy.predicates.JComboBoxOperatorByItemIndexPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;

public class JComboBoxOperator extends JComponentOperator {
    private @Nullable JButtonOperator button;
    private final ListDriver driver;
    private @Nullable JTextFieldOperator text;

    public JComboBoxOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JComboBoxOperator(JComboBox<?> b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getListDriver(getClass());
    }

    public JComboBoxOperator(ContainerOperator cont, int index) {
        this((JComboBox<?>) waitComponent(cont, PredicatesJ.of(JComboBox.class), index));
    }

    public JComboBoxOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JComboBoxOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JComboBoxOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JComboBox<?>) cont.waitSubComponent(PredicatesJ.of(JComboBox.class, chooser), index));
    }

    public JComboBoxOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JComboBox<?>) waitComponent(cont, new JComboBoxByItemPredicate(text, -1, stringComparator), index));
    }

    @SuppressWarnings("unchecked") // erased access; same behavior as the original raw-typed Jemmy API
    private JComboBox<Object> getJComboBox() {
        return (JComboBox<Object>) getSource();
    }

    public JButton findJButton() {
        return (JButton) waitSubComponent(PredicatesJ.of(JButton.class));
    }

    public JTextField findJTextField() {
        return (JTextField) waitSubComponent(PredicatesJ.of(JTextField.class));
    }

    public JButtonOperator getButton() {
        if (button == null) {
            button = new JButtonOperator(findJButton());
        }

        return button;
    }

    public @Nullable JTextFieldOperator getTextField() {
        if (getJComboBox().isEditable()) {
            text = new JTextFieldOperator(findJTextField());
        }

        return text;
    }

    public JList<?> waitList() {
        return (JList<?>)
                FunctionRepeater.on(new ComboBoxPopupListFunction(this)).runUntilNotNull(null);
    }

    public void pushComboButton() {
        makeComponentVisible();
        getButton().push();
    }

    public int findItemIndex(String item, StringComparator comparator) {
        ComboBoxModel<?> model = getModel();
        for (int i = 0, iMax = model.getSize(); i < iMax; i++) {
            if (comparator.equals(model.getElementAt(i).toString(), item)) {
                return i;
            }
        }

        return -1;
    }

    public int waitItem(String item, StringComparator comparator) {
        waitState(new JComboBoxOperatorByItemPredicate(item, comparator));

        return findItemIndex(item, comparator);
    }

    public int waitItem(int itemIndex) {
        waitState(new JComboBoxOperatorByItemIndexPredicate(itemIndex));

        return itemIndex;
    }

    public void selectItem(String item, StringComparator comparator) {
        selectItem(waitItem(item, comparator));
    }

    public void selectItem(int index) {
        waitComponentEnabled();

        driver.selectItem(this, waitItem(index));

        if (getVerification()) {
            waitItemSelected(index);
        }
    }

    public void typeText(String text) {
        makeComponentVisible();
        JTextFieldOperator tfo = Objects.requireNonNull(getTextField(), "combo box is not editable");
        tfo.setVisualizer(new EmptyVisualizer());
        tfo.typeText(text);
    }

    public void clearText() {
        makeComponentVisible();
        JTextFieldOperator tfo = Objects.requireNonNull(getTextField(), "combo box is not editable");
        tfo.setVisualizer(new EmptyVisualizer());
        tfo.clearText();
    }

    public void enterText(String text) {
        makeComponentVisible();
        JTextFieldOperator tfo = Objects.requireNonNull(getTextField(), "combo box is not editable");
        tfo.setVisualizer(new EmptyVisualizer());
        tfo.enterText(text);
    }

    public void waitItemSelected(int index) {
        waitState(new JComboBoxOperatorBySelectedIndexPredicate(index));
    }

    public void waitItemSelected(String item, StringComparator stringComparator) {
        waitState(new JComboBoxOperatorByItemPredicate(item, stringComparator));
    }

    public void actionPerformed(ActionEvent actionEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().actionPerformed(actionEvent);

            return null;
        }));
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().addActionListener(actionListener);

            return null;
        }));
    }

    public void addItem(Object object) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().addItem(object);

            return null;
        }));
    }

    public void addItemListener(ItemListener itemListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().addItemListener(itemListener);

            return null;
        }));
    }

    public void configureEditor(ComboBoxEditor comboBoxEditor, Object object) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().configureEditor(comboBoxEditor, object);

            return null;
        }));
    }

    public void contentsChanged(ListDataEvent listDataEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().contentsChanged(listDataEvent);

            return null;
        }));
    }

    public String getActionCommand() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getActionCommand()));
    }

    public ComboBoxEditor getEditor() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getEditor()));
    }

    public Object getItemAt(int i) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getItemAt(i)));
    }

    public int getItemCount() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getItemCount()));
    }

    public KeySelectionManager getKeySelectionManager() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getKeySelectionManager()));
    }

    public int getMaximumRowCount() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getMaximumRowCount()));
    }

    public ComboBoxModel<?> getModel() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getModel()));
    }

    public ListCellRenderer<?> getRenderer() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getRenderer()));
    }

    public int getSelectedIndex() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getSelectedIndex()));
    }

    public Object getSelectedItem() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getSelectedItem()));
    }

    public Object[] getSelectedObjects() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getSelectedObjects()));
    }

    public ComboBoxUI getUI() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().getUI()));
    }

    public void hidePopup() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().hidePopup();

            return null;
        }));
    }

    public void insertItemAt(Object object, int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().insertItemAt(object, i);

            return null;
        }));
    }

    public void intervalAdded(ListDataEvent listDataEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().intervalAdded(listDataEvent);

            return null;
        }));
    }

    public void intervalRemoved(ListDataEvent listDataEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().intervalRemoved(listDataEvent);

            return null;
        }));
    }

    public boolean isEditable() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().isEditable()));
    }

    public boolean isLightWeightPopupEnabled() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().isLightWeightPopupEnabled()));
    }

    public boolean isPopupVisible() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().isPopupVisible()));
    }

    public void processKeyEvent(KeyEvent keyEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().processKeyEvent(keyEvent);

            return null;
        }));
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().removeActionListener(actionListener);

            return null;
        }));
    }

    public void removeAllItems() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().removeAllItems();

            return null;
        }));
    }

    public void removeItem(Object object) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().removeItem(object);

            return null;
        }));
    }

    public void removeItemAt(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().removeItemAt(i);

            return null;
        }));
    }

    public void removeItemListener(ItemListener itemListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().removeItemListener(itemListener);

            return null;
        }));
    }

    public boolean selectWithKeyChar(char c) {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getJComboBox().selectWithKeyChar(c)));
    }

    public void setActionCommand(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setActionCommand(string);

            return null;
        }));
    }

    public void setEditable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setEditable(b);

            return null;
        }));
    }

    public void setEditor(ComboBoxEditor comboBoxEditor) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setEditor(comboBoxEditor);

            return null;
        }));
    }

    public void setKeySelectionManager(KeySelectionManager keySelectionManager) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setKeySelectionManager(keySelectionManager);

            return null;
        }));
    }

    public void setLightWeightPopupEnabled(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setLightWeightPopupEnabled(b);

            return null;
        }));
    }

    public void setMaximumRowCount(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setMaximumRowCount(i);

            return null;
        }));
    }

    public void setModel(ComboBoxModel<?> comboBoxModel) {
        @SuppressWarnings("unchecked") // erased access; same behavior as the original raw-typed Jemmy API
        ComboBoxModel<Object> model = (ComboBoxModel<Object>) comboBoxModel;
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setModel(model);

            return null;
        }));
    }

    public void setPopupVisible(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setPopupVisible(b);

            return null;
        }));
    }

    public void setRenderer(ListCellRenderer<?> listCellRenderer) {
        @SuppressWarnings("unchecked") // erased access; same behavior as the original raw-typed Jemmy API
        ListCellRenderer<Object> renderer = (ListCellRenderer<Object>) listCellRenderer;
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setRenderer(renderer);

            return null;
        }));
    }

    public void setSelectedIndex(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setSelectedIndex(i);

            return null;
        }));
    }

    public void setSelectedItem(Object object) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setSelectedItem(object);

            return null;
        }));
    }

    public void setUI(ComboBoxUI comboBoxUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().setUI(comboBoxUI);

            return null;
        }));
    }

    public void showPopup() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            getJComboBox().showPopup();

            return null;
        }));
    }

    public static @Nullable JComboBox<?> findJComboBox(Container cont, Predicate<Component> chooser, int index) {
        return (JComboBox<?>) findComponent(cont, PredicatesJ.of(JComboBox.class, chooser), index);
    }

    public static @Nullable JComboBox<?> findJComboBox(Container cont, Predicate<Component> chooser) {
        return findJComboBox(cont, chooser, 0);
    }

    public static @Nullable JComboBox<?> findJComboBox(
            Container cont, String text, StringComparator stringComparator, int itemIndex, int index) {
        return findJComboBox(cont, new JComboBoxByItemPredicate(text, itemIndex, stringComparator), index);
    }

    public static @Nullable JComboBox<?> findJComboBox(
            Container cont, String text, StringComparator stringComparator, int itemIndex) {
        return findJComboBox(cont, text, stringComparator, itemIndex, 0);
    }

    public static JComboBox<?> waitJComboBox(Container cont, Predicate<Component> chooser, int index) {
        return (JComboBox<?>) waitComponent(cont, PredicatesJ.of(JComboBox.class, chooser), index);
    }

    public static JComboBox<?> waitJComboBox(Container cont, Predicate<Component> chooser) {
        return waitJComboBox(cont, chooser, 0);
    }

    public static JComboBox<?> waitJComboBox(
            Container cont, String text, StringComparator stringComparator, int itemIndex, int index) {
        return waitJComboBox(cont, new JComboBoxByItemPredicate(text, itemIndex, stringComparator), index);
    }

    public static JComboBox<?> waitJComboBox(
            Container cont, String text, StringComparator stringComparator, int itemIndex) {
        return waitJComboBox(cont, text, stringComparator, itemIndex, 0);
    }

    private static class JComboBoxOperatorByItemPredicate implements Predicate<JComboBoxOperator> {
        private final String item;
        private final StringComparator comparator;

        public JComboBoxOperatorByItemPredicate(String item, StringComparator comparator) {
            this.item = item;
            this.comparator = comparator;
        }

        @Override
        public boolean test(JComboBoxOperator jComboBoxOperator) {
            return jComboBoxOperator.findItemIndex(item, comparator) > -1;
        }
    }

    private class JComboBoxOperatorBySelectedIndexPredicate implements Predicate<JComboBoxOperator> {
        private final int index;

        public JComboBoxOperatorBySelectedIndexPredicate(int index) {
            this.index = index;
        }

        @Override
        public boolean test(JComboBoxOperator comp) {
            return getSelectedIndex() == index;
        }
    }
}
