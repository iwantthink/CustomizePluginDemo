package com.hypers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.IOException;

public class Inject {

    public static byte[] injectTimeWhenInit(byte[] inpuByte) throws IOException {
        ClassReader cr = new ClassReader(inpuByte);
        ClassWriter cw = new ClassWriter(cr, 1);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {
            @Override
            public MethodVisitor visitMethod(int access, final String name, String desc,
                                             String signature, String[] exceptions) {

                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if ("<init>".equals(name)) {
                    return new RedefineAdvice(Opcodes.ASM4, mv, access, name, desc);
                }
                if ("test".equals(name)) {
                    return new RedefineAdvice(Opcodes.ASM4, mv, access, name, desc);
                }
                return mv;
            }

        };
        cr.accept(cv, 12);
        return cw.toByteArray();
    }

    static class RedefineAdvice extends AdviceAdapter {

        protected RedefineAdvice(int api, MethodVisitor mv, int access,
                                 String name, String desc) {
            super(api, mv, access, name, desc);
        }

        @Override
        protected void onMethodEnter() {
            mv.visitCode();
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
                    "Ljava/io/PrintStream;");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
                    "currentTimeMillis", "()J", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
                    "println", "(J)V", false);
            super.onMethodEnter();
        }

    }
}