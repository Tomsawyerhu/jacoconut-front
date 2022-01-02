package wiget.tree;

import java.util.ArrayList;
import java.util.List;

public class TestRootCell extends TreeCell{
    public TestRootCell(String n) {
        super(n);
    }

    @Override
    public boolean isTestClass() {
        return false;
    }

    @Override
    public boolean isTestCase() {
        return false;
    }

    @Override
    public List<TreeCell> collect() {
        List<TreeCell> result=new ArrayList<>();
        if(this.hasChildren){
            int count=this.getChildCount();
            for(int i=0;i<count;i+=1){
                if(this.getChildAt(i) instanceof TreeCell){
                    result.addAll(((TreeCell)this.getChildAt(i)).collect());
                }
            }
        }
        return result;
    }
}
