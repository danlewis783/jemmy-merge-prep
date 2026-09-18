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
package org.netbeans.jemmy.operators;

import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.awt.Component;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JTreeOperatorCustomNodeToString extends JTreeOperator {

//    Object userObject = node.getUserObject();
//    if (userObject instanceof ModelParent) {
//        return ((ModelParent) userObject).getName();
//    } else {
//        return node.toString();
//    }

    private final Function<DefaultMutableTreeNode, String> customNodeToStringFunc;

    JTreeOperatorCustomNodeToString(JTree tree, Function<DefaultMutableTreeNode, String> customToStringFunc) {
        super(Objects.requireNonNull(tree, "tree"));
        this.customNodeToStringFunc = Objects.requireNonNull(customToStringFunc, "customToStringFunc");
    }

    public static JTreeOperatorCustomNodeToString waitFor(ContainerOperator containerOp, int index, Function<DefaultMutableTreeNode, String> customToStringFunc) {
        Objects.requireNonNull(containerOp, "containerOp");
        Objects.requireNonNull(customToStringFunc, "customToStringFunc");
        if (index < 0) {
            throw new IllegalArgumentException("index must not be negative");
        }
        Component component = waitComponent(containerOp, PredicatesJ.of(JTree.class), index);
        JTree componentAsTree = (JTree) component;
        JTreeOperatorCustomNodeToString result = new JTreeOperatorCustomNodeToString(componentAsTree, customToStringFunc);
        return result;
    }

    public static JTreeOperatorCustomNodeToString waitFor(ContainerOperator containerOp, Function<DefaultMutableTreeNode, String> customToStringFunc) {
        return waitFor(containerOp, 0, customToStringFunc);
    }

    /**
     * Override this method to handle non-toString nodes.
     */
    @Override
    public Object chooseSubnode(Object parent, String text, int index, StringComparator comparator) {
        return QueueTool.getInstance().callOnQueue(() -> {
            TreeModel md = getSource().getModel();
            int count = -1;
            DefaultMutableTreeNode node;
            for (int i = 0, iMax = md.getChildCount(parent); i < iMax; i++) {
                try {
                    node = (DefaultMutableTreeNode) md.getChild(parent, i);
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }

                String value = customNodeToStringFunc.apply(node);

                if (comparator.equals(value, text)) {
                    count++;

                    if (count == index) {
                        return node;
                    }
                }
            }

            return null;
        });
    }


}
