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
package org.netbeans.jemmy.functions;

import java.util.Objects;
import java.util.function.Function;
import javax.swing.tree.TreePath;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.TreePathAndBoolean;
import org.netbeans.jemmy.TreePathChooserAndTreePath;
import org.netbeans.jemmy.operators.JTreeOperator;

public final class LoadedFunction implements Function<TreePathChooserAndTreePath, TreePathAndBoolean> {
    private final JTreeOperator jTreeOp;

    public LoadedFunction(JTreeOperator jTreeOp) {
        this.jTreeOp = jTreeOp;
    }

    @Override
    public @Nullable TreePathAndBoolean apply(TreePathChooserAndTreePath arg) {
        JTreeOperator.TreePathChooser treePathChooser =
                Objects.requireNonNull(arg, "arg").getTreePathChooser();
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
