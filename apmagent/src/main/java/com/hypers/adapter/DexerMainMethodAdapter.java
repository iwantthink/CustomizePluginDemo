package com.hypers.adapter;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by renbo on 2017/10/19.
 */

public class DexerMainMethodAdapter extends AdviceAdapter {

    protected DexerMainMethodAdapter(MethodVisitor methodVisitor, int access, String name, String desc) {
        super(Opcodes.ASM5, methodVisitor, access, name, desc);

    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();

    }

}
