package com.hypers;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * Created by renbo on 2017/10/12.
 */

public class TransformAgent {

    //1.5++
    public static void premain(String args, Instrumentation inst) {
        System.out.println("----premain----");
        inst.addTransformer(new MyClassTransformer());
    }

    public static void agentmain(String args, Instrumentation inst) throws UnmodifiableClassException {
        System.out.println("----agentmain----");
        inst.addTransformer(new MyClassTransformer(), true);
        Class[] classes = inst.getAllLoadedClasses();
        for (Class cls : classes) {
            System.out.println("cls.getName = " + cls.getName());
        }
//        inst.retransformClasses(MyClass.class);
    }
}
