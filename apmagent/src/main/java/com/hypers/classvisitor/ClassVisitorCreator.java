package com.hypers.classvisitor;

import com.hypers.utils.Log;

import org.objectweb.asm.ClassVisitor;

/**
 * Created by renbo on 2017/10/18.
 */

public abstract class ClassVisitorCreator {

    protected Log mLog;

    public ClassVisitorCreator(Log log) {
        mLog = log;
    }

    public abstract ClassVisitor create(ClassVisitor classVisitor);

    public static class ProcessBuilderClassVisitorCreator extends ClassVisitorCreator {

        public ProcessBuilderClassVisitorCreator(Log log) {
            super(log);
        }

        @Override
        public ClassVisitor create(ClassVisitor classVisitor) {
            return new ProcessBuilderClassVisitor(classVisitor, mLog);
        }
    }

    public static class DexerMainClassVisitorCreator extends ClassVisitorCreator {

        public DexerMainClassVisitorCreator(Log log) {
            super(log);
        }

        @Override
        public ClassVisitor create(ClassVisitor classVisitor) {
            return new DexerMainClassVisitor(classVisitor, mLog);
        }
    }

}
