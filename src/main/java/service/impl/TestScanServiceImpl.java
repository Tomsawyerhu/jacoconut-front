package service.impl;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.sqlite.util.OSInfo;
import service.TestScanService;
import storage.CodeLink;
import storage.ProjectParams;
import storage.model.TestCaseLinking;
import storage.model.TestClassLinking;
import utils.OSAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class TestScanServiceImpl implements TestScanService {
    @Override
    public void scanTests() {
        String testRoot= ProjectParams.TEST_SOURCE_ROOT.get();
        if(testRoot==null){
            return;
        }
        try {
            List<String> javaFiles=Files.walk(Paths.get(testRoot))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(Path::toAbsolutePath)
                    .map(Path::toString)
                    .collect(Collectors.toList());
            for(String classFile:javaFiles){
                List<String> className = new ArrayList<>();
                // Create Compilation.
                CompilationUnit cu = StaticJavaParser.parse(new File(classFile));
                // Create Visitor.
                VoidVisitor<List<String>> classNameVisitor = new ClassNameCollector();
                // Visit.
                classNameVisitor.visit(cu,className);
                cu.accept(new MethodAnnotationCollector(className.get(0), classFile),null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static class ClassNameCollector extends VoidVisitorAdapter<List<String>>{
        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<String> collector) {
            super.visit(n, collector);
            collector.add(n.getNameAsString());
        }
    }
    private static class MethodAnnotationCollector extends VoidVisitorAdapter<HashSet<String>> {
        private final String className;
        private final String classPath;
        MethodAnnotationCollector(String className,String classPath){
            this.className=className;
            this.classPath=classPath;
        }
        @Override
        public void visit(MethodDeclaration md, HashSet<String> argNameSet) {
            List<AnnotationExpr> annotationExprs=md.getAnnotations();
            boolean isTestClass=false;
            for(AnnotationExpr annotationExpr:annotationExprs){
                if(annotationExpr.getName().toString().equals(org.junit.Test.class.getName())||annotationExpr.getName().toString().equals("Test")){
                    isTestClass=true;
                    CodeLink.PROJECT_TEST_CASE_LINK.get().add(new TestCaseLinking(md.getNameAsString(),md.getRange().get(),new TestClassLinking(this.className,this.classPath)));
                }
            }
            if(isTestClass){
                TestClassLinking t=new TestClassLinking(this.className,this.classPath);
                boolean exists=false;
                for(TestClassLinking testClassLinking:CodeLink.PROJECT_TEST_CLASS_LINK.get()){
                    if (testClassLinking.equals(t)){
                        exists=true;
                        break;
                    }
                }
                if (!exists)CodeLink.PROJECT_TEST_CLASS_LINK.get().add(t);
            }
            super.visit(md, argNameSet);
        }
    }

//    @Override
//    public void scanTests(Project project) {
//
//        //scan test classes
//        //scan test cases
//        GlobalSearchScope testScope=GlobalSearchScope.allScope(project);
//        List<VirtualFile> files1= new ArrayList<>(FilenameIndex
//                .getAllFilesByExt(project, "java", testScope));
//
//        List<VirtualFile> files3= new ArrayList<>(FilenameIndex
//                .getAllFilesByExt(project, "java", GlobalSearchScope.allScope(project)));
//        FilenameIndex
//                .getAllFilesByExt(project,"java",testScope)
//                .forEach(virtualFile -> {
//                    //class file path
//                    String absPath=virtualFile.getPath();
//                    PsiClass javaClass=((PsiJavaFile)Objects.requireNonNull(PsiManager.getInstance(project).findFile(virtualFile))).getClasses()[0];
//                    String className=javaClass.getQualifiedName();
//                    PsiMethod[] methods = javaClass.getMethods();
//                    boolean isTestClass=false;
//                    for(PsiMethod method:methods){
//                        for(PsiAnnotation annotation:method.getAnnotations()){
//                            //is junit annotation or not
//                            if(Objects.equals(annotation.getQualifiedName(), org.junit.Test.class.getName())){
//                                isTestClass=true;
//                                CodeLink.PROJECT_TEST_CASE_LINK.get().add(new TestCaseLinking(method.getName(),method.getTextRange(),new TestClassLinking(className,absPath)));
//                            }
//                        };
//                    }
//                    if(isTestClass){
//                        CodeLink.PROJECT_TEST_CLASS_LINK.get().add(new TestClassLinking(className,absPath));
//                    }
//                });
//        //Messages.showInfoMessage(CodeLink.PROJECT_TEST_CLASS_LINK.get().stream().map(TestClassLinking::getName).collect(Collectors.joining("\n")),"Test Class");
//    }

}
