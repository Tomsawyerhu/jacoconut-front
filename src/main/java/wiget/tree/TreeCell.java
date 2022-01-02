package wiget.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.List;

public abstract class TreeCell extends DefaultMutableTreeNode {
    public abstract boolean isTestClass();
    public abstract boolean isTestCase();
    public abstract List<TreeCell> collect();
    protected TreeCell(String n){
        super(n);
        this.isSelected=false;
        hasChildren=false;
    }
    private TreeCell(){}

    protected boolean isSelected;
    protected boolean hasChildren;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public void add(MutableTreeNode newChild) {
        super.add(newChild);
        if(!hasChildren){
            hasChildren=true;
        }
    }

    public void selectAllChildren(){
        if(this.hasChildren){
            for(int i=0;i<this.getChildCount();i+=1){
                if(this.getChildAt(i) instanceof TreeCell){
                    TreeCell son=(TreeCell)this.getChildAt(i);
                    son.isSelected=true;
                    son.selectAllChildren();
                }
            }
        }
    }

    public void unselectAllChildren(){
        if(this.hasChildren){
            for(int i=0;i<this.getChildCount();i+=1){
                if(this.getChildAt(i) instanceof TreeCell){
                    TreeCell son=(TreeCell)this.getChildAt(i);
                    son.isSelected=false;
                    son.unselectAllChildren();
                }
            }
        }
    }
}
