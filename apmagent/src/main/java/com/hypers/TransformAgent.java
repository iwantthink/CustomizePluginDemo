package com.hypers;

import java.lang.instrument.Instrumentation;

/**
 * Created by renbo on 2017/10/12.
 */

public class TransformAgent {

    //1.5++
    public static void premain(String args, Instrumentation inst) {
        System.out.println("----premain----");
        inst.addTransformer(new MyClassTransformer());
    }

    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("----agentmain----");
        inst.addTransformer(new MyClassTransformer());
    }
}
