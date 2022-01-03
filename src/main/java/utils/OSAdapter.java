package utils;

import org.sqlite.util.OSInfo;

public class OSAdapter {
    public static String formalizeFilePath(String s){
        if(OSInfo.getOSName().equals("Windows")&&s.startsWith("file://")){
            return s.substring(7).replace("\\\\","/");
        }
        else return s;
    }
}
