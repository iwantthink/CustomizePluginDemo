package com.hypers

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

public class Inject {

    static void injectClass(String srcPath, String srcFileName, String destPath) throws Exception {

        //读取class 文件
        File srcFile = new File(srcPath);
//        File destDir = new File(destPath);
//        if (!destDir.exists()) {
//            destDir.mkdirs();
//        }

        InputStream ins = new FileInputStream(srcFile);
        byte[] bytes = injectTimeWhenInit(ins);
        ins.close();
        if (srcFile.exists()) {
            srcFile.delete()
        }
        FileOutputStream fos = new FileOutputStream(srcFile);
        fos.write(bytes);
        fos.close();
    }

    private static byte[] injectTimeWhenInit(InputStream inputStream) throws IOException {
        ClassReader cr = new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(cr, 0);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {
            @Override
            public MethodVisitor visitMethod(int access, final String name, String desc,
                                             String signature, String[] exceptions) {

                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if ("<init>".equals(name)) {
                    return new RedefineAdvice(Opcodes.ASM4, mv, access, name, desc);
                }
                return mv;
            }

        };
        cr.accept(cv, 0);
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