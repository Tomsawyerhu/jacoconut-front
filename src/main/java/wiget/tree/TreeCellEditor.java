package wiget.tree;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class TreeCellEditor extends AbstractCellEditor implements javax.swing.tree.TreeCellEditor {
    private TreeCellRenderer treeCellRenderer;
    protected TreeCellEditor(TreeCellRenderer renderer){
        this.treeCellRenderer=renderer;
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        return null;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
}
