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
import utils.OSAdapter;

import java.util.stream.Collectors;

public class ProjectServiceImpl implements ProjectScanService {
    @Override
    public void scanProject(Project project) {
        ProjectParams.PROJECT_ROOT.set(OSAdapter.formalizeFilePath(project.getBasePath()));
        //get all modules
        Module[] modules= ModuleManager.getInstance(project).getModules();
        //per module
        //record source root
        //record test root
        for(Module module:modules){
            ProjectParams.PROJECT_SOURCE_ROOT
                    .set(OSAdapter.formalizeFilePath(ModuleRootManager
                            .getInstance(module)
                            .getSourceRoots(JavaSourceRootType.SOURCE)
                            .stream().map(VirtualFile::getUrl)
                            .collect(Collectors.toList()).get(0)));
            ProjectParams.TEST_SOURCE_ROOT
                    .set(OSAdapter.formalizeFilePath(ModuleRootManager
                            .getInstance(module)
                            .getSourceRoots(JavaSourceRootType.TEST_SOURCE)
                            .stream().map(VirtualFile::getUrl)
                            .collect(Collectors.toList()).get(0)));
        }

        //Messages.showInfoMessage("Source roots for the " + project.getName() + " sourceRoot:\n" + ProjectParams.PROJECT_SOURCE_ROOT.get().stream().collect(Collectors.joining())+"\n"+" testRoot:\n" + ProjectParams.TEST_SOURCE_ROOT.get().stream().collect(Collectors.joining())+"\n", "Project Properties");
    }
}
