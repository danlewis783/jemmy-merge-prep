package org.netbeans.jemmy.functions;

import java.util.Objects;
import java.util.function.Function;
import org.netbeans.jemmy.TreePathAndBoolean;
import org.netbeans.jemmy.TreePathChooserAndTreePath;
import org.netbeans.jemmy.operators.JTreeOperator;

import javax.swing.tree.TreePath;

public final class LoadedFunction implements Function<TreePathChooserAndTreePath, TreePathAndBoolean> {
    private final JTreeOperator jTreeOp;

    public LoadedFunction(JTreeOperator jTreeOp) {
        this.jTreeOp = jTreeOp;
    }

    @Override
    public TreePathAndBoolean apply(TreePathChooserAndTreePath arg) {
        JTreeOperator.TreePathChooser treePathChooser = Objects.requireNonNull(arg, "arg must not be null").getTreePathChooser();
        TreePath path = arg.getTreePath();
        Object[] children = jTreeOp.getChildren(path.getLastPathComponent());
        for (int j = 0; j < children.length; j++) {
            TreePath treePath = path.pathByAddingChild(children[j]);

            if (treePathChooser.checkPath(treePath, j)) {
                return new TreePathAndBoolean(treePath, Boolean.TRUE);
            }

            if (treePathChooser.hasAsParent(treePath, j)) {
                return new TreePathAndBoolean(treePath, Boolean.FALSE);
            }
        }

        return null;
    }
}
