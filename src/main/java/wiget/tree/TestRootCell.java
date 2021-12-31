package wiget.tree;

public class TestRootCell extends TreeCell{
    public TestRootCell(String n) {
        super(n);
    }

    @Override
    boolean isTestClass() {
        return false;
    }

    @Override
    boolean isTestCase() {
        return false;
    }
}
