package extern.statementCoverage.methodAdapter;

import com.github.javaparser.utils.Pair;
import extern.storage.Storage;
import extern.utils.Tracer;
import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 行覆盖
 * 非顺序语句、分支插入探针
 * 左探针：顺序执行代码块起始行
 *   1. 左探针为空
 * 右探针：顺序执行代码块结束行
 *   1. 第一遍记录右探针位置，第二遍修改class文件
 *   2.1 遍历
 *       2.1.1 athrow、return之前
 *       2.1.2 分支跳转语句记录跳转位置，等到visitlinenumber到这个位置的时候把上一次遍历到的行作为右探针位置进行记录
 */
public class StatementCoverageByBasicBlockMethodAdapter extends MethodVisitor {
    Logger logger = Logger.getLogger(StatementCoverageByBasicBlockMethodAdapter.class);
    String name;

    Label handler;
    int line;
    int left;
    Set<Integer> possibleLefts=new HashSet<>();

    protected StatementCoverageByBasicBlockMethodAdapter(MethodVisitor m, String n) {
        super(458752,m);
        name = n;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        line=-1;
        left=-1;
        handler=null;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line,start);

        if(possibleLefts.contains(line)&&left>0){
            //when reach the line where left probe should be inserted while last left probe exists,insert right probe
            insertRightProbe();
        }
        this.line=line;
        if(left==-1){
            left=line;
        }
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
        super.visitTableSwitchInsn(min, max, dflt, labels);
        //record where to insert left probe
        for(Label label:labels)try {
            Field line=label.getClass().getDeclaredField("line");
            line.setAccessible(true);
            this.possibleLefts.add(line.getInt(label));
            this.possibleLefts.add(this.line+1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        //insert right probe
        insertRightProbe();
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        super.visitLookupSwitchInsn(dflt, keys, labels);
        //record where to insert left probe
        for(Label label:labels)try {
            Field line=label.getClass().getDeclaredField("line");
            line.setAccessible(true);
            this.possibleLefts.add(line.getInt(label));
            this.possibleLefts.add(this.line+1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        //insert right probe
        insertRightProbe();
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        //if、goto指令
        //record where to insert left probe
        try {
            Field line=label.getClass().getDeclaredField("line");
            line.setAccessible(true);
            this.possibleLefts.add(line.getInt(label));
            this.possibleLefts.add(this.line+1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        //insert right probe
        insertRightProbe();
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
        if((opcode>= Opcodes.IRETURN && opcode<=Opcodes.RETURN)||opcode==Opcodes.ATHROW){
            //return、throw指令
            //insert right probe
            insertRightProbe();
        }
    }

    /*
     * mark catch clause
     */
    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        super.visitTryCatchBlock(start, end, handler, type);
        this.handler=handler;
    }

    /*
     * meet recorded exception handler,then record its lineNumber
     */
    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        if(this.handler==label){
            Field line;
            try {
                line = label.getClass().getDeclaredField("lineNumber");
                line.setAccessible(true);
                this.possibleLefts.add(line.getInt(label));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        this.line=-1;
        this.left=-1;
        this.handler=null;
    }

    private void insertRightProbe(){
        Tracer.executeLines(left,line-left+1,name);
        logger.info("left:"+left+";right:"+this.line);
        int i= Storage.lines.get();
        Storage.lines.compareAndSet(i,i+this.line-left+1);
        left=-1;
    }

    /**
     * after right probes have been all recorded
     * for the second time visitClass method called
     * probes should be inserted into bytecode
     * StatementCoverageMethodAdapterExecutor will modify bytecode to see
     * which line has been executed during runtime
     */
    public static class StatementCoverageMethodAdapterExecutor extends MethodVisitor{
        String name;
        boolean isTarget=false;
        List<Pair<Integer,Integer>> probes=null;

        protected StatementCoverageMethodAdapterExecutor(MethodVisitor m, String n) {
            super(458752,m);
            this.name=n;
        }

        @Override
        public void visitCode() {
            super.visitCode();
            isTarget= Storage.probes.get().containsKey(this.name);
            probes= Storage.probes.get().get(this.name);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            super.visitLineNumber(line, start);
            if(isTarget){
                for(Pair<Integer,Integer> pair:probes){
                    if(line == (int) pair.a + pair.b - 1){
                        insertRightProbe(pair.b);
                        break;
                    }
                }
            }
        }

        private void insertRightProbe(int lines){
            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "extern/utils/Tracer", "getInstance", "()L"
                            + "extern/utils/Tracer" + ";");
            mv.visitLdcInsn(lines);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    "extern/utils/Tracer", "executeLines2",
                    "(I)V");
        }
    }
}
