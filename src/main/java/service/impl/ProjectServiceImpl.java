package service.impl;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import service.ProjectScanService;
import storage.ProjectParams;

import java.util.stream.Collectors;

public class ProjectServiceImpl implements ProjectScanService {
    @Override
    public void scanProject(Project project) {
        //get all modules
        Module[] modules= ModuleManager.getInstance(project).getModules();
        //per module
        //record source root
        //record test root
        for(Module module:modules){
            ProjectParams.PROJECT_SOURCE_ROOT
                    .get()
                    .addAll(ModuleRootManager
                            .getInstance(module)
                            .getSourceRoots(JavaSourceRootType.SOURCE)
                            .stream().map(VirtualFile::getUrl)
                            .collect(Collectors.toList()));
            ProjectParams.TEST_SOURCE_ROOT
                    .get()
                    .addAll(ModuleRootManager
                            .getInstance(module)
                            .getSourceRoots(JavaSourceRootType.TEST_SOURCE)
                            .stream().map(VirtualFile::getUrl)
                            .collect(Collectors.toList()));
        }

        //Messages.showInfoMessage("Source roots for the " + project.getName() + " sourceRoot:\n" + ProjectParams.PROJECT_SOURCE_ROOT.get().stream().collect(Collectors.joining())+"\n"+" testRoot:\n" + ProjectParams.TEST_SOURCE_ROOT.get().stream().collect(Collectors.joining())+"\n", "Project Properties");
    }
}
