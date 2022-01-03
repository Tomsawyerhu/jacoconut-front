package wiget.hook;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import extern.api.JacoconutApi;
import extern.api.LCType;
import org.apache.maven.it.Verifier;
import storage.ProjectParams;
import utils.OSAdapter;

/**
 * 加强MainToolWindow功能
 * 在用户点击Calculate Coverage后生成新的toolwindow tab展示结果
 */
public class CalculateCoverageHook implements ToolWindowHook{
    private final ToolWindow toolWindow;
    public CalculateCoverageHook(ToolWindow toolWindow){
        this.toolWindow=toolWindow;
    }

    @Override
    public void recall(Object... params) {
        for(Object o:params){
            if(o instanceof String){
                System.out.println((String) o);
            }
        }

        //build the project
        buildProject();

        //modify .class files
        modifyClass();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JacoconutReportToolWindow window= new JacoconutReportToolWindow();
        Content content = contentFactory.createContent(window.getContent(), "Report", true);
        toolWindow.getContentManager().addContent(content);
    }

    /*
     * 支持Maven项目
     * todo gradle/ant
     */
    private void buildProject(){
        String s= OSAdapter.formalizeFilePath(ProjectParams.PROJECT_ROOT.get());
        Map<String,String> env=new HashMap<>();
        env.put("maven.multiModuleProjectDirectory", s);

        try {
            Verifier v=new Verifier(s);
            v.executeGoals(Arrays.asList("compile"),env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modifyClass(){
        modifyClass(OSAdapter.formalizeFilePath(ProjectParams.PROJECT_ROOT.get())+"/target/classes");
    }

    private void modifyClass(String path){
        if(Files.isDirectory(Paths.get(path))){
            String[] files=new File(path).list();
            if(files!=null){
                for(String f:files){
                    modifyClass(path+"/"+f);
                }
            }
        }
        if(path.endsWith(".class")){
            //String className=path.replace(".class","").replace(OSAdapter.formalizeFilePath(ProjectParams.PROJECT_ROOT.get())+"/target/classes/","");
            //todo
            try {
                JacoconutApi.lineCoverageProbe(path, LCType.BASIC_BLOCK_RECORD);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class JacoconutReportToolWindow {
        public JPanel getContent(){
            return new JPanel();
        }
    }
}
