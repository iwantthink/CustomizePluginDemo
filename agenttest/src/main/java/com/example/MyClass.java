package com.example;

import com.sun.tools.attach.VirtualMachine;

import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Scanner;

public class MyClass {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("what do u need ?");
        String input;
        while (!(input = s.nextLine()).equals("gg")) {
            if (input.startsWith("agent")) {
                try {
                    System.setProperty("java.library.path",
                            "C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\bin");
                    Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
                    fieldSysPath.setAccessible(true);
                    fieldSysPath.set(null, null);


                    String pid = input.split("-")[1];
                    int indexOf = pid.indexOf('@');
                    if (indexOf > 0) {
                        pid = pid.substring(0, indexOf);
                    }
                    System.out.println("pid = " + pid);

                    // attach to target VM
                    VirtualMachine vm = VirtualMachine.attach(pid);

                    // get system properties in target VM
                    Properties props = vm.getSystemProperties();

                    // construct path to management agent
                    String agent = "C:\\Users\\Administrator\\Documents\\CustomizePluginDemo\\agenttest\\libs\\apmagent.jar";

                    // load agent into target VM
                    vm.loadAgent(agent);

                    // detach
                    vm.detach();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                new MyClass().test();
            }
        }
    }

    public void test() {
        System.out.println("test");
    }
}
