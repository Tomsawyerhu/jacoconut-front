package extern.statementCoverage.classAdapter;

import extern.statementCoverage.methodAdapter.SCType;
import extern.statementCoverage.methodAdapter.StatementCoverageMethodAdapterFactory;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;


public class StatementCoverageClassAdaptor extends ClassVisitor {
    private final SCType methodVisitorType;

    public StatementCoverageClassAdaptor(ClassVisitor cv, SCType scType) {
        super(458752,cv);
        this.methodVisitorType = scType;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = this.cv.visitMethod(access, name, desc, signature, exceptions);
        return StatementCoverageMethodAdapterFactory.getMethodVisitor(methodVisitorType, name, methodVisitor);
    }
}
