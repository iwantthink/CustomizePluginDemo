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

    public static void agentmain(String args, Instrumentation inst) throws UnmodifiableClassException, ClassNotFoundException {
        System.out.println("----agentmain step1----");
        inst.addTransformer(new MyClassTransformer(), true);
        System.out.println("----agentmain step2---- ,args = " + args);

//        Class[] classes = inst.getAllLoadedClasses();
//        for (Class cls : classes) {
//            if (cls.getName().startsWith("ModifedClass")) {
//                System.out.println("AgentMain::agentmain, transform class: "
//                        + cls.getName());
//                inst.retransformClasses(cls);
//            }
//        }
        System.out.println("----agentmain step3----");
//        try {
//            System.out.println("----start redefine----");
//            File f = new File(args);
//            byte[] reporterClassFile = new byte[(int) f.length()];
//            DataInputStream in = new DataInputStream(new FileInputStream(f));
//            in.readFully(reporterClassFile);
//            in.close();
//            ClassDefinition reporterDef =
//                    new ClassDefinition(Class.forName("ModifedClass"), reporterClassFile);
//            inst.redefineClasses(reporterDef);
//            System.out.println("----end redefine----");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


//        Class[] classes = inst.getAllLoadedClasses();
//        for (Class cls : classes) {
//            System.out.println("cls.getName = " + cls.getName());
//        }
//        inst.retransformClasses(MyClass.class);
    }
}
