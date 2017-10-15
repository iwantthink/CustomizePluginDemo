package com.example;

import com.sun.tools.attach.VirtualMachine;

import java.util.Properties;
import java.util.Scanner;

public class MyClass {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);      //new 一个Scanner对象
        System.out.println("what do u need ?");//屏幕提示输入
        String input;
        while (!(input = s.nextLine()).equals("gg")) {
            if (input.startsWith("agent")) {
                try {
                    // 在windows上，获取到得name格式为 1234@userName
                    // 1234为PID，@为分隔符，userName为当前用户
//                    String pid =d ManagementFactory.getRuntimeMXBean().getName();
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
