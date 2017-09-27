package com.hypers

import com.android.build.api.transform.Context
import org.apache.commons.io.FileUtils
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

public class Inject {

    static File injectClass(File dir, File inputFile, Context context) {
        String buildTypes = dir.name
        String productFlavors = dir.parentFile.name
        File outputDir = new File(context.temporaryDir, "$productFlavors\\$buildTypes")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        File outputFile = new File(outputDir.absolutePath + "\\$inputFile.name")
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
        InputStream ins = new FileInputStream(inputFile)
        byte[] bytes = injectTimeWhenInit(ins)
        ins.close()

        FileUtils.writeByteArrayToFile(outputFile, bytes)
        return outputFile
    }

    private static byte[] injectTimeWhenInit(InputStream inputStream) throws IOException {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 1)
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {
            @Override
            public MethodVisitor visitMethod(int access, final String name, String desc,
                                             String signature, String[] exceptions) {

                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
                if ("<init>".equals(name)) {
                    return new RedefineAdvice(Opcodes.ASM4, mv, access, name, desc);
                }
                return mv
            }

        };
        cr.accept(cv, 12)
        return cw.toByteArray()
    }

    static class RedefineAdvice extends AdviceAdapter {

        protected RedefineAdvice(int api, MethodVisitor mv, int access,
                                 String name, String desc) {
            super(api, mv, access, name, desc)
        }

        @Override
        protected void onMethodEnter() {
            mv.visitCode();
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
                    "Ljava/io/PrintStream;")
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
                    "currentTimeMillis", "()J", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
                    "println", "(J)V", false)
            super.onMethodEnter()
        }

    }
}