package com.hmt.analytics.customizeplugin;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void runAsm() throws Exception {
        //读取class 文件
        File srcFile = new File("E:\\github\\CustomizePluginDemo\\app\\src\\main\\assets\\Test.class");
        File destDir = new File("E:\\github\\CustomizePluginDemo\\app\\src\\main\\assets\\");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        InputStream ins = new FileInputStream(srcFile);
        byte[] bytes = referHackWhenInit(ins);

        File destFile = new File(destDir, "Test2.class");
        FileOutputStream fos = new FileOutputStream(destFile);
        fos.write(bytes);
        fos.close();

    }

    private static byte[] referHackWhenInit(InputStream inputStream) throws IOException {
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

