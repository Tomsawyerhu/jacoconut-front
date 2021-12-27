package storage.model;

public class TestClassLinking {
    private String name;
    private String path;
    public TestClassLinking(String className,String srcPath){
        this.name=className;
        this.path=srcPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TestClassLinking){
            return ((TestClassLinking) obj).name.equals(this.name) &&((TestClassLinking) obj).path.equals(this.path);
        }else{
            return false;
        }
    }
}
