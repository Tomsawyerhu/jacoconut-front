package wiget.tree;

import java.util.ArrayList;
import java.util.List;

public class TestClassCell extends TreeCell {
    private String name;
    private String classPath;
    public TestClassCell(String n) {
        super(n);
        this.name=n;
    }
    public TestClassCell(String n,String path) {
        super(n+"("+path+")");
        this.name=n;
        this.classPath =path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
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
