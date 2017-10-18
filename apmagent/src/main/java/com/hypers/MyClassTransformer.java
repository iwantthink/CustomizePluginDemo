package com.hypers;

import com.hypers.classvisitor.ClassVisitorCreator;
import com.hypers.utils.FileLog;
import com.hypers.utils.Log;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;

/**
 * Created by renbo on 2017/10/12.
 */

public class MyClassTransformer implements ClassFileTransformer {

    private Log mLog;
    private final HashMap<String, ClassVisitorCreator> mClassVisitorCreator = new HashMap<>();

    public MyClassTransformer(Log log) {
        mLog = log;
        mClassVisitorCreator.put("java/lang/ProcessBuilder", new ClassVisitorCreator.ProcessBuilderClassVisitorCreator(log));
        mClassVisitorCreator.put("com/android/dx/command/dexer/Main", new ClassVisitorCreator.DexerMainClassVisitorCreator(log));
    }

    @Override
    public byte[] transform(ClassLoader classLoader, String className,
                            Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        mLog.d("MyClassTransformer --- transform --- className = " + className);
        ClassVisitorCreator creator = mClassVisitorCreator.get(className);
        if (null != creator) {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, 1);
            ClassVisitor cv = creator.create(cw);
            //12
            cr.accept(cv, ClassReader.SKIP_FRAMES);
            return bytes;
        }
        if (mLog instanceof FileLog) {
            ((FileLog) mLog).close();
        }
        return bytes;
    }

//    public byte[] injectTimeWhenInit(byte[] inpuByte) throws IOException {
//        System.out.println("enter injectTimeWHEN init");
//        ClassReader cr = new ClassReader(inpuByte);
//        ClassWriter cw = new ClassWriter(cr, 1);
//        ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {
//            @Override
//            public MethodVisitor visitMethod(int access, final String name, String desc,
//                                             String signature, String[] exceptions) {
//
//                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//                if ("test".equals(name)) {
//                    return new Inject.RedefineAdvice(Opcodes.ASM4, mv, access, name, desc);
//                }
//                return mv;
//            }
//
//        };
//        cr.accept(cv, 12);
//        return cw.toByteArray();
//    }
//
//    class RedefineAdvice extends AdviceAdapter {
//
//        protected RedefineAdvice(int api, MethodVisitor mv, int access,
//                                 String name, String desc) {
//            super(api, mv, access, name, desc);
//        }
//
//        @Override
//        protected void onMethodEnter() {
//            mv.visitCode();
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
//                    "Ljava/io/PrintStream;");
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
//                    "currentTimeMillis", "()J", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
//                    "println", "(J)V", false);
//            super.onMethodEnter();
//        }
//
//    }
}
