package wiget.tree;

import javax.swing.tree.DefaultMutableTreeNode;

public class TestClassCell extends DefaultMutableTreeNode implements TreeCell{
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
}
