package com.hypers;

import java.io.FileOutputStream;
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
//        System.out.println("className = " + className);
        try {
            if (className.equals("TestMain")) {
                System.out.println("find className = " + className + "begin inject");
                bytes = Inject.injectTimeWhenInit(bytes);
                System.out.println("end inject");
                FileOutputStream fos = new FileOutputStream("C:\\Users\\renbo\\Desktop\\java\\dd.class");
                fos.write(bytes);
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            System.out.println("something wrong");
        }
        return bytes;
    }
}
