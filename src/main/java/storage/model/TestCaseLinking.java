package storage.model;

import com.github.javaparser.Range;

public class TestCaseLinking {
    private String name;
    private Range range;
    private TestClassLinking owner;
    public TestCaseLinking(String caseName,Range range,TestClassLinking testClass){
        this.name=caseName;
        this.range=range;
        this.owner=testClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public TestClassLinking getOwner() {
        return owner;
    }

    public void setOwner(TestClassLinking owner) {
        this.owner = owner;
    }
}
