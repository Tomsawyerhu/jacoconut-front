package wiget.tree;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

/**
 * 带复选框的JTree
 * 节点类型:TreeCell
 */
public class CheckBoxTree extends JTree {
    private TestRootCell root;
    public CheckBoxTree(TestRootCell root){
        super(root);
        this.root=root;
        formalize();
        addListeners();
    }

    private CheckBoxTree(){}

    public TestRootCell getRoot() {
        return root;
    }

    private void formalize(){
        this.setDropMode(DropMode.USE_SELECTION);
        this.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //自定义节点绘制器
        this.setCellRenderer(new TreeCellRenderer());
    }

    private void addListeners(){
        CheckBoxTree that=this;
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                //右单击勾选
                int row=that.getRowForLocation(e.getX(),e.getY());
                if(row!=-1&&e.getButton()== MouseEvent.BUTTON3){
                    TreePath path = that.getPathForLocation(e.getX(), e.getY());
                    if(Objects.requireNonNull(path).getLastPathComponent() instanceof TreeCell){
                        TreeCell node=(TreeCell) path.getLastPathComponent();
                        if(node.isSelected()){
                            node.setSelected(false);
                            node.unselectAllChildren();
                            TreeCell father;
                            if(node.getParent() instanceof TreeCell&&(father=(TreeCell) node.getParent()).isSelected()){
                                father.setSelected(false);
                            }
                        }else{
                            node.setSelected(true);
                            node.selectAllChildren();
                            TreeCell father;
                            if(node.getParent() instanceof TreeCell&&!(father=(TreeCell) node.getParent()).isSelected()){
                                boolean allSelected=true;
                                for(int i=0;i<father.getChildCount();i+=1){
                                    if(father.getChildAt(i) instanceof TreeCell){
                                        if(!((TreeCell)father.getChildAt(i)).isSelected()) {
                                            allSelected = false;
                                            break;
                                        }
                                    }
                                }
                                if(allSelected){
                                    father.setSelected(true);
                                }
                            }
                        }
                    };
                    that.repaint();
                }
            }
        });
    }


}
