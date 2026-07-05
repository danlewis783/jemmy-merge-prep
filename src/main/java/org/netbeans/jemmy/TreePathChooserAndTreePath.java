package org.netbeans.jemmy;

import org.netbeans.jemmy.operators.JTreeOperator;

import javax.swing.tree.TreePath;

public final class TreePathChooserAndTreePath {
    private final JTreeOperator.TreePathChooser treePathChooser;
    private final TreePath treePath;

    public TreePathChooserAndTreePath(JTreeOperator.TreePathChooser treePathChooser, TreePath treePath) {
        this.treePathChooser = treePathChooser;
        this.treePath = treePath;
    }

    public JTreeOperator.TreePathChooser getTreePathChooser() {
        return treePathChooser;
    }

    public TreePath getTreePath() {
        return treePath;
    }
}
