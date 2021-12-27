package listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;
import storage.ProjectParams;

/**
 * fixme
 * 暂时用作打开新项目时更新插件
 */
public class ProjectListener implements ProjectManagerListener {
    @Override
    public void projectOpened(@NotNull Project project) {
        ProjectManagerListener.super.projectOpened(project);
        ProjectParams.STATE.getAndSet(-1);
    }
}
