package extern.api;

import extern.statementCoverage.classAdapter.StatementCoverageClassAdaptor;
import extern.statementCoverage.methodAdapter.SCType;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class JacoconutApi {
    public static void lineCoverageProbe(String classFile) throws IOException {
        lineCoverageProbe(classFile,LCType.NAIVE);
    }

    public static void lineCoverageProbe(String classFile,LCType lcType) throws IOException {
        FileInputStream inputStream=new FileInputStream(classFile);
        ClassReader cr=new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        StatementCoverageClassAdaptor classVisitor;
        if(lcType==LCType.NAIVE){
            classVisitor = new StatementCoverageClassAdaptor(cw, SCType.NAIVE);
        }else if(lcType==LCType.BASIC_BLOCK_RECORD){
            classVisitor = new StatementCoverageClassAdaptor(cw, SCType.BASIC_BLOCK_RECORD);
        }else{
            inputStream.close();
            return;
        }
        cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
        inputStream.close();
        if(lcType==LCType.NAIVE){
            //write
            byte[] data = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(classFile);
            fos.write(data);
            fos.flush();
            fos.close();
            return;
        }

        inputStream=new FileInputStream(classFile);
        cr=new ClassReader(inputStream);

        cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        StatementCoverageClassAdaptor classVisitor2 = new StatementCoverageClassAdaptor(cw,SCType.BASIC_BLOCK_EXEC);
        cr.accept(classVisitor2, ClassReader.SKIP_FRAMES);

        inputStream.close();
        //write
        byte[] data = cw.toByteArray();
        FileOutputStream fos = new FileOutputStream(classFile);
        fos.write(data);
        fos.flush();
        fos.close();
    }
}
