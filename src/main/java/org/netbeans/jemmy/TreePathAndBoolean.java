package org.netbeans.jemmy;

import javax.swing.tree.TreePath;

public final class TreePathAndBoolean {
    private final TreePath treePath;
    private final boolean checked;

    public TreePathAndBoolean(TreePath treePath, boolean checked) {
        this.treePath = treePath;
        this.checked = checked;
    }

    public TreePath getTreePath() {
        return treePath;
    }

    public boolean isChecked() {
        return checked;
    }
}
