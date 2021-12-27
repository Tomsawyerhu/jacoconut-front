package wiget.tree;

import javax.swing.tree.DefaultMutableTreeNode;

public class TestCaseCell extends DefaultMutableTreeNode implements TreeCell {
    private String name;
    private String classPath;
    private int startLine;
    private int endLine;
    private int startColumn;
    private int endColumn;
    public TestCaseCell(String n, int startLine, int endLine,int startColumn,int endColumn,String path) {
        super(n+"("+startLine+","+startColumn+"->"+endLine+","+endColumn+")");
        this.name=n;
        this.startLine = startLine;
        this.endLine = endLine;
        this.startColumn=startColumn;
        this.endColumn=endColumn;
        this.classPath=path;
    }
    public TestCaseCell(String n) {
        super(n);
        this.name=n;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
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
