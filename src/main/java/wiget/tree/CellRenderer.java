package wiget.tree;

import icon.SDKIcons;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
        JLabel label = (JLabel)super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        //修改图标
        if(value instanceof TestCaseCell){
            label.setIcon(SDKIcons.JavaMethod);
        }else if(value instanceof TestClassCell){
            label.setIcon(SDKIcons.JavaClass);
        }else{
            label.setIcon(SDKIcons.Logo);
            //todo do nothing
        }
        return label;
    }

    public static TestClassCell generateTestClassCell(String name){
        return new TestClassCell(name);
    }
    public static TestCaseCell generateTestCaseCell(String name){
        return new TestCaseCell(name);
    }

}
