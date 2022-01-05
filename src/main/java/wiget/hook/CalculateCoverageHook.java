package wiget.hook;

import api.JacoconutApi;
import api.LCType;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.it.Verifier;
import storage.ProjectParams;
import utils.Tracer;

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
    public void recall(Object[] params) {
        //build the project
        buildProject();

        //modify .class files
        modifyClass();

        //run test
        String[] methods=new String[params.length];
        for(int i=0;i<params.length;i+=1){
            methods[i]=(String)params[i];
        }
        runTest(methods);



        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JacoconutReportToolWindow window= new JacoconutReportToolWindow();
        window.initialize();
        int lines=JacoconutApi.getLine();
        int exec=JacoconutApi.getExec();
        double a=api.JacoconutApi.calculateCoverage(); //calculate coverage
        window.jTextField.setText(String.valueOf(api.JacoconutApi.calculateCoverage()));
        Content content = contentFactory.createContent(window.getContent(), "Report", true);
        toolWindow.getContentManager().addContent(content);
    }

    private static class ExtendedClassLoader extends URLClassLoader{
        public ExtendedClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        public void addURL(URL url) {
            super.addURL(url);
        }
    }

    /*
     * 支持Maven项目
     * todo gradle/ant
     */
    private void buildProject(){
        String s= ProjectParams.PROJECT_ROOT.get();
        Map<String,String> env=new HashMap<>();
        env.put("maven.multiModuleProjectDirectory", s);

        try {
            Verifier v=new Verifier(s);
            v.executeGoals(Arrays.asList("compile","test"),env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modifyClass(){
        modifyClass(ProjectParams.PROJECT_ROOT.get()+"/target/classes");
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
            //todo
            try {
                api.JacoconutApi.lineCoverageProbe(path, LCType.BASIC_BLOCK_RECORD);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void runTest(String[] methods)  {
        ClassLoader threadClassLoader =Thread.currentThread().getContextClassLoader();
        ExtendedClassLoader extendedClassLoader=new ExtendedClassLoader(new URL[0], JacoconutApi.class.getClassLoader());
        try {
            extendedClassLoader.addURL(new URL(("file:"+ProjectParams.PROJECT_ROOT.get()+"/target/classes/").replace("/","\\\\")));
            extendedClassLoader.addURL(new URL(("file:"+ProjectParams.PROJECT_ROOT.get()+"/target/test-classes/").replace("/","\\\\")));
            Thread.currentThread().setContextClassLoader(extendedClassLoader);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        for(String method:methods){
            String ss=method.substring(ProjectParams.TEST_SOURCE_ROOT.get().length()+1).replace(".java","").replace(".class","");
            int index=ss.lastIndexOf("/");
            String className=ss.substring(0,index);
            String methodName=ss.substring(index+1);
            Class<?> c= null;
            try {
                c = extendedClassLoader.loadClass(className.replace("/","."));
                c.getMethod(methodName).invoke(c.newInstance());
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            int lines=JacoconutApi.getLine();
            int exec=JacoconutApi.getExec();
        }
        Thread.currentThread().setContextClassLoader(threadClassLoader);




    }

    public static class JacoconutReportToolWindow {
        public JTextField jTextField;
        public JPanel jPanel;
        public void initialize(){
            this.jTextField=new JTextField();
            this.jPanel=new JPanel(new BorderLayout());
            this.jPanel.add(jTextField,BorderLayout.CENTER);
        }
        public JPanel getContent(){
            return jPanel;
        }
    }
}
