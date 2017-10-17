package com.example;

import java.lang.management.ManagementFactory;

/**
 * Created by Administrator on 2017/10/15.
 */

public class AgentThread {
    public static void main(String[] args) throws InterruptedException {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        int indexOf = pid.indexOf('@');
        if (indexOf > 0) {
            pid = pid.substring(0, indexOf);
        }
        System.out.println("pid = " + pid);
        new ModifedClass().test();
        while (true) {
            Thread.sleep(5000);
            new ModifedClass().test();
        }
    }
}
