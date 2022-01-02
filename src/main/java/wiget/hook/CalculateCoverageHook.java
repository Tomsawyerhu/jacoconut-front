package wiget.hook;

import com.intellij.openapi.project.Project;
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
        for(String s:ProjectParams.PROJECT_SOURCE_ROOT.get()){
            String ss=OSAdapter.formalizeFilePath(s);
            modifyClass(ss);
        }
    }
    private void modifyClass(String path){
        if(Files.isDirectory(Paths.get(path))){
            String[] files=new File(path).list();
            if(files!=null){
                for(String f:files){
                    modifyClass(path+File.separator+f);
                }
            }
        }
        if(path.endsWith(".class")){
            String className=path.replace(".class","");
            //todo
            //extern.api.JacoconutApi.lineCoverageProbe(className,path);
        }
    }

    public static class JacoconutReportToolWindow {
        public JPanel getContent(){
            return new JPanel();
        }
    }
}
