package wiget;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import service.ProjectScanService;
import service.TestScanService;
import storage.ProjectParams;
import wiget.hook.CalculateCoverageHook;
import wiget.hook.ToolWindowHook;

import java.util.Collections;

public class JacoconutToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        //数据准备
        if(ProjectParams.STATE.get()==-1){
            project.getService(ProjectScanService.class).scanProject(project);
            project.getService(TestScanService.class).scanTests();
            ProjectParams.STATE.compareAndSet(-1,1);
        }

        JacoconutMainToolWindow window=new JacoconutMainToolWindow(toolWindow, Collections.singletonList(new CalculateCoverageHook(toolWindow)));
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(window.getContent(), "Main", false);
        toolWindow.getContentManager().addContent(content);
    }
}
