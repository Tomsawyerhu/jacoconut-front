package extern.statementCoverage.methodAdapter;

import extern.storage.Storage;
import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 行覆盖
 * 语句执行前插入探针
 */
public class NaiveStatementCoverageMethodAdapter extends MethodVisitor {
    Logger logger = Logger.getLogger(NaiveStatementCoverageMethodAdapter.class);
    String name;


    protected NaiveStatementCoverageMethodAdapter(MethodVisitor m, String n) {
        super(458752,m);
        name = n;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line,start);
        int currentLines= Storage.lines.get();
        Storage.lines.compareAndSet(currentLines,currentLines+1);
        this.visitMethodInsn(Opcodes.INVOKESTATIC,
                "extern/utils/Tracer", "getInstance", "()L"
                        + "extern/utils/Tracer" + ";");
        mv.visitLdcInsn(1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "extern/utils/Tracer", "executeLines2",
                "(I)V");
    }

}