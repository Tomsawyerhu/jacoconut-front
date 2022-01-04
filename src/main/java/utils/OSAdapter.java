package utils;

public class OSAdapter {
    public static String formalizeFilePath(String s){
        String t=s;
        if(t.startsWith("file://")){
           t=t.substring(7);
        }
        t=t.replace("\\\\","/").replace("\\","/");
        return t;
    }
}
