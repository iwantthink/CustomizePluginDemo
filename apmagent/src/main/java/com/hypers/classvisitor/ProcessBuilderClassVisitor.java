package com.hypers.classvisitor;

import com.hypers.utils.Log;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by renbo on 2017/10/18.
 */

public class ProcessBuilderClassVisitor extends ClassVisitor {

    private Log mLog;

    public ProcessBuilderClassVisitor(ClassVisitor cv, Log log) {
        super(Opcodes.ASM5, cv);
        mLog = log;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        mLog.d("ProcessBuilderClassVisitor---MethodVisitor----name = " + name);
        return methodVisitor;
    }
}
