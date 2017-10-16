package com.hypers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by renbo on 2017/10/12.
 */

public class MyClassTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        System.out.println("transform className = " + className);
        try {
            if (className.equals("ModifedClass")) {
                System.out.println("find className = " + className + "  begin inject");
                bytes = injectTimeWhenInit(bytes);
                System.out.println("end inject");
//                FileOutputStream fos = new FileOutputStream("C:\\Users\\renbo\\Desktop\\java\\dd.class");
//                fos.write(bytes);
//                fos.flush();
//                fos.close();
            }
        } catch (IOException e) {
            System.out.println("something wrong");
        }
        return bytes;
    }

    public byte[] injectTimeWhenInit(byte[] inpuByte) throws IOException {
        System.out.println("enter injectTimeWHEN init");
        ClassReader cr = new ClassReader(inpuByte);
        ClassWriter cw = new ClassWriter(cr, 1);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {
            @Override
            public MethodVisitor visitMethod(int access, final String name, String desc,
                                             String signature, String[] exceptions) {

                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if ("<init>".equals(name)) {
                    return new Inject.RedefineAdvice(Opcodes.ASM4, mv, access, name, desc);
                }
                if ("test".equals(name)) {
                    return new Inject.RedefineAdvice(Opcodes.ASM4, mv, access, name, desc);
                }
                return mv;
            }

        };
        cr.accept(cv, 12);
        return cw.toByteArray();
    }

    class RedefineAdvice extends AdviceAdapter {

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
