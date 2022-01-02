package wiget.hook;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import wiget.JacoconutReportToolWindow;

public class CalculateCoverageHook implements ToolWindowHook{
    private final ToolWindow toolWindow;
    public CalculateCoverageHook(ToolWindow toolWindow){
        this.toolWindow=toolWindow;
    }

    @Override
    public void recall() {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JacoconutReportToolWindow window=new JacoconutReportToolWindow();
        Content content = contentFactory.createContent(window.getContent(), "Report", true);
        toolWindow.getContentManager().addContent(content);
    }
}
