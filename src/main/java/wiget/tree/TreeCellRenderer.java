package wiget.tree;

import icon.SDKIcons;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class TreeCellRenderer extends DefaultTreeCellRenderer {
    public TreeCellRenderer(){}

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

        CheckBoxNodePanel checkBoxNodePanel=new CheckBoxNodePanel(label,new JCheckBox());
        //添加文本
        final String text =
                tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
        checkBoxNodePanel.label.setText(text);

        //是否选中
        if(value instanceof TreeCell){
            TreeCell node=(TreeCell)value;
            checkBoxNodePanel.check.setEnabled(true);
            checkBoxNodePanel.check.setSelected(node.isSelected);
        }

        checkBoxNodePanel.formalize();

        return checkBoxNodePanel;
    }

    public static TestClassCell generateTestClassCell(String name){
        return new TestClassCell(name);
    }
    public static TestCaseCell generateTestCaseCell(String name){
        return new TestCaseCell(name);
    }

}
